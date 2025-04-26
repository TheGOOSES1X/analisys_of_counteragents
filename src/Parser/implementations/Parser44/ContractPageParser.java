package Parser.implementations.Parser44;
import Parser.Database.models.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContractPageParser {
    public Contract parseContractInfo(String originalUrl, WebDriver driver, WebDriverWait wait) {
        String contractDraftUrl = findContractDraftUrl(originalUrl, driver, wait);
        if (contractDraftUrl == null) return null;

        try {
            // Сохраняем текущий URL и куки
            String currentUrl = driver.getCurrentUrl();
            Set<Cookie> cookies = driver.manage().getCookies();

            driver.get(contractDraftUrl);
            Map<String, Object> contractDetails = parseContractDraft(contractDraftUrl, driver, wait);

            Contract contract = new Contract();
            Supplier supplier = new Supplier();

            fillContractModel(contract, contractDetails);
            fillSupplierModel(supplier, contractDetails);

            contract.setSupplier(supplier);

            // Возвращаемся обратно
            driver.get(currentUrl);
            cookies.forEach(cookie -> driver.manage().addCookie(cookie));

            return contract;
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге контракта: " + e.getMessage());
            return null;
        }
    }
    private String findContractDraftUrl(String originalUrl, WebDriver driver, WebDriverWait wait) {
        try {
            // 1. Извлекаем номер закупки из оригинального URL
            String regNumber = extractRegNumber(originalUrl);
            if (regNumber == null) {
                System.out.println("Не удалось извлечь номер закупки из URL: " + originalUrl);
                return null;
            }

            // 2. Формируем URL черновика контракта напрямую
            String draftContractUrl = originalUrl.replaceAll(
                    "/epz/order/notice/\\w+/view/\\w+-info\\.html\\?regNumber=\\d+",
                    "/epz/order/notice/rpec/contract-draft.html?regNumber=" + regNumber + "0001"
            );

            // 3. Проверяем существование страницы контракта
            driver.get(draftContractUrl);

            // Проверяем, не попали ли мы на страницу "Запрашиваемая страница не существует"
            try {
                WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//td[contains(text(),'Запрашиваемая страница не существует')]")));
                if (errorMessage != null) {
                    System.out.println("Страница контракта не существует: " + draftContractUrl);
                    return null;
                }
            } catch (TimeoutException e) {
                // Ошибка не найдена, значит страница существует
            }

            // 4. Проверяем наличие данных контракта на странице
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.block")));
                System.out.println("Найден черновик контракта: " + draftContractUrl);
                return draftContractUrl;
            } catch (TimeoutException e) {
                System.out.println("Не удалось найти данные контракта на странице: " + draftContractUrl);
                return null;
            }

        } catch (Exception e) {
            System.out.println("Ошибка при поиске контракта: " + e.getMessage());
            return null;
        }
    }

    private Map<String, Object> parseContractDraft(String contractDraftUrl, WebDriver driver, WebDriverWait wait) {
        Map<String, Object> contractData = new LinkedHashMap<>();

        try {
            driver.get(contractDraftUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.block")));


            String stateContractId = parseStateContractId(driver);
            if (stateContractId != null) {
                contractData.put("state_contract_id", stateContractId);
            }

            List<WebElement> blocks = driver.findElements(By.cssSelector("div.block"));
            for (WebElement block : blocks) {
                String blockTitle = block.findElement(By.cssSelector("p.block_title")).getText().trim();
                Map<String, Object> blockData = new LinkedHashMap<>();

                if (blockTitle.equals("3. Предмет контракта")) {
                    // Основная информация о предмете контракта
                    blockData.put("3. Предмет контракта", parseSimpleTable(block));

                    // Объекты закупки
                    try {
                        WebElement subBlock = block.findElement(By.xpath(".//div[p[@class='sub-block_title' and contains(text(),'3.1. Объект закупки')]]"));
                        List<Map<String, String>> objectsTable = parseProcurementObjectsTable(subBlock);

                        Map<String, Object> procurementObjects = new LinkedHashMap<>();
                        procurementObjects.put("objects_table", objectsTable);

                        if (!objectsTable.isEmpty()) {
                            Map<String, String> lastRow = objectsTable.get(objectsTable.size() - 1);
                            if (lastRow.containsKey("Итого:")) {
                                procurementObjects.put("total_amount", lastRow.get("Итого:"));
                            }
                        }

                        blockData.put("3.1. Объект закупки", procurementObjects);
                    } catch (Exception e) {
                        System.out.println("Не удалось распарсить таблицу объектов закупки: " + e.getMessage());
                    }
                }
                else if (blockTitle.startsWith("4. Условия контракта")) {
                    // Обработка блока условий контракта
                    List<WebElement> subBlocks = block.findElements(By.cssSelector("div.sub-block"));
                    for (WebElement subBlock : subBlocks) {
                        String subBlockTitle = subBlock.findElement(By.cssSelector("p.sub-block_title")).getText().trim();

                        if (subBlockTitle.startsWith("4.3. Место поставки")) {
                            // Специальная обработка для места поставки
                            blockData.put(subBlockTitle, parseDeliveryPlaceTable(subBlock));
                        } else {
                            // Обычная обработка для других подблоков
                            blockData.put(subBlockTitle, parseSimpleTable(subBlock));
                        }
                    }
                }
                else {
                    // Обработка остальных блоков
                    List<WebElement> subBlocks = block.findElements(By.cssSelector("div.sub-block"));

                    if (!subBlocks.isEmpty()) {
                        for (WebElement subBlock : subBlocks) {
                            String subBlockTitle = subBlock.findElement(By.cssSelector("p.sub-block_title")).getText().trim();
                            blockData.put(subBlockTitle, parseSimpleTable(subBlock));
                        }
                    } else {
                        blockData.putAll(parseSimpleTable(block));
                    }
                }

                contractData.put(blockTitle, blockData);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге черновика контракта: " + e.getMessage());
        }

        return contractData;
    }
    private void fillContractModel(Contract contract, Map<String, Object> contractData) {
        try {
            // 3. Предмет контракта
            Map<String, Object> subjectData = (Map<String, Object>) contractData.get("3. Предмет контракта");
            if (subjectData != null) {
                Map<String, String> mainSubject = (Map<String, String>) subjectData.get("3. Предмет контракта");
                if (mainSubject != null) {
                    String subjectKey = findPartialKey(mainSubject, "Предмет контракта");
                    String specKey = findPartialKey(mainSubject, "Специализация");
                    String defenseKey = findPartialKey(mainSubject, "Контракт заключен для выполнения");
                    String lifecycleKey = findPartialKey(mainSubject, "Контракт жизненного цикла");
                    String quantityKey = findPartialKey(mainSubject, "Невозможно определить количество");

                    contract.setSubject(subjectKey != null ? mainSubject.get(subjectKey) : null);
                    contract.setSpecialization(specKey != null ? mainSubject.get(specKey) : null);
                    contract.setDefenseOrder(defenseKey != null ? mainSubject.get(defenseKey) : null);
                    contract.setLifecycleContract(lifecycleKey != null ? mainSubject.get(lifecycleKey) : null);
                    contract.setQuantityUndefined(quantityKey != null ? mainSubject.get(quantityKey) : null);
                }
            }
            contract.setStateContractId((String) contractData.get("state_contract_id"));

            // 1. Номер контракта
            String contractNumberKey = findPartialKey(contractData, "Номер контракта");
            if (contractNumberKey != null) {
                Object contractNumberValue = contractData.get(contractNumberKey);

                if (contractNumberValue instanceof Map) {
                    // Если значение - Map (старая логика)
                    Map<String, Object> contractNumberSection = (Map<String, Object>) contractNumberValue;
                    if (!contractNumberSection.isEmpty()) {
                        Object firstValue = contractNumberSection.values().iterator().next();
                        if (firstValue instanceof Map) {
                            Map<String, String> contractNumberData = (Map<String, String>) firstValue;
                            contract.setContractNumber(contractNumberData.get("Номер контракта"));
                        }
                    }
                } else if (contractNumberValue instanceof String) {
                    // Если значение - просто строка (новая логика)
                    contract.setContractNumber((String) contractNumberValue);
                } else if (contractNumberValue != null) {
                    // Другие случаи - преобразуем в строку
                    contract.setContractNumber(contractNumberValue.toString());
                }
            }

            // 4. Условия контракта
            Map<String, Object> conditionsData = (Map<String, Object>) contractData.get("4. Условия контракта");
            if (conditionsData != null) {
                // 4.1. Сроки исполнения контракта
                Map<String, String> executionTerms = (Map<String, String>) conditionsData.get("4.1. Сроки исполнения контракта");
                if (executionTerms != null) {
                    String startDateKey = findPartialKey(executionTerms, "Дата начала");
                    String endDateKey = findPartialKey(executionTerms, "Дата окончания");

                    contract.setStartDate(startDateKey != null ?
                            parseDate(executionTerms.get(startDateKey)) : null);
                    contract.setEndDate(endDateKey != null ?
                            parseDate(executionTerms.get(endDateKey)) : null);
                }
                // 4.2. Этапы исполнения контракта
                String executionStagesKey = findPartialKey(conditionsData, "Этапы исполнения");
                if (executionStagesKey != null) {
                    Map<String, String> executionStages = (Map<String, String>) conditionsData.get(executionStagesKey);
                    if (executionStages != null && !executionStages.isEmpty()) {
                        // Если мапа не пустая, значит есть этапы, иначе - "Контракт не разделен на этапы"
                        contract.setExecutionStages(executionStages.isEmpty() ?
                                "Контракт не разделен на этапы исполнения контракта" :
                                String.join(", ", executionStages.values()));
                    }
                }

                // 4.3. Место поставки
                Map<String, String> deliveryPlace = (Map<String, String>) conditionsData.get("4.3. Место поставки товара, выполнения работы или оказания услуги");
                if (deliveryPlace != null) {
                    String countryKey = findPartialKey(deliveryPlace, "Страна");
                    String addressKey = findPartialKey(deliveryPlace, "Место");
                    String addInfoKey = findPartialKey(deliveryPlace, "Адрес");

                    contract.setCountry(countryKey != null ? deliveryPlace.get(countryKey) : null);
                    contract.setAddress(addressKey != null ? deliveryPlace.get(addressKey) : null);
                    contract.setAdditionalAddressInfo(addInfoKey != null ?
                            deliveryPlace.get(addInfoKey) : null);
                }

                // 4.4. Требования к гарантии качества
                Map<String, String> qualityGuarantee = (Map<String, String>) conditionsData.get("4.4. Требования к гарантии качества товара, работы, услуги");
                if (qualityGuarantee != null) {
                    String qualReqKey = findPartialKey(qualityGuarantee, "Требования гарантия качества");
                    String warrantyReqKey = findPartialKey(qualityGuarantee, "Информация о требованиях ");
                    String manufReqKey = findPartialKey(qualityGuarantee, "Требования к гарантии ");
                    String warrantyPeriodKey = findPartialKey(qualityGuarantee, "Срок, на который предоставляется гарантия");
                    String warrantyGuarKey = findPartialKey(qualityGuarantee, "Требуется обеспечение исполнения");

                    contract.setQualityGuaranteeRequired(qualReqKey != null ? qualityGuarantee.get(qualReqKey) : null);
                    contract.setWarrantyRequirements(warrantyReqKey != null ? qualityGuarantee.get(warrantyReqKey) : null);
                    contract.setManufacturerWarrantyRequirements(manufReqKey != null ? qualityGuarantee.get(manufReqKey) : null);
                    contract.setWarrantyPeriod(warrantyPeriodKey != null ? qualityGuarantee.get(warrantyPeriodKey) : null);
                    contract.setWarrantyGuaranteeRequired(warrantyGuarKey != null ? qualityGuarantee.get(warrantyGuarKey) : null);
                }

                // 4.6. Условия привлечения субподрядчиков
                Map<String, String> subcontractors = (Map<String, String>) conditionsData.get("4.6.  Условия привлечения субподрядчиков, соисполнителей из числа СМП, СОНО");
                if (subcontractors != null) {
                    String smpReqKey = findPartialKey(subcontractors, "Предъявляется требование о привлечении к исполнению контракта субподрядчиков");
                    String smpExemptKey = findPartialKey(subcontractors, "Объем привлечения к исполнению контракта");
                    String smpPrecentKey = findPartialKey(subcontractors, "За неисполнение условий по привлечению");

                    contract.setSmpSubcontractorsRequired(smpReqKey != null ? subcontractors.get(smpReqKey) : null);
                    contract.setSmpSubcontractorsExempt(smpExemptKey != null ? subcontractors.get(smpExemptKey) : null);
                    contract.setSmpSubcontractorsLiability(smpPrecentKey != null ? subcontractors.get(smpExemptKey) : null);

                }

                // 4.7. Прочие условия
                Map<String, String> otherConditions = (Map<String, String>) conditionsData.get("4.7. Прочие условия контракта");
                if (otherConditions != null) {
                    String terminationKey = findPartialKey(otherConditions, "Предусмотрена возможность");
                    contract.setUnilateralTerminationAllowed(terminationKey != null ? otherConditions.get(terminationKey) : null);
                }
            }

            // 5. Финансирование контракта
            Map<String, Object> financingData = (Map<String, Object>) contractData.get("5. Финансирование контракта");
            if (financingData != null) {
                // 5.1. Источники финансирования
                Map<String, String> fundingSources = (Map<String, String>) financingData.get("5.1. Источники финансирования");
                if (fundingSources != null) {
                    String budgetNameKey = findPartialKey(fundingSources, "Наименование бюджета");
                    String budgetTypeKey = findPartialKey(fundingSources, "Вид бюджета");
                    String municipKey = findPartialKey(fundingSources, "Код территории");
                    String selfFundKey = findPartialKey(fundingSources, "Закупка за счет");
                    String bankSupportKey = findPartialKey(fundingSources, "Информация о банковском");

                    contract.setBudgetName(budgetNameKey != null ? fundingSources.get(budgetNameKey) : null);
                    contract.setBudgetType(budgetTypeKey != null ? fundingSources.get(budgetTypeKey) : null);
                    contract.setMunicipalityCode(municipKey != null ? fundingSources.get(municipKey) : null);
                    contract.setSelfFunded(selfFundKey != null ? fundingSources.get(selfFundKey) : null);
                    contract.setBankingSupportInfo(bankSupportKey != null ? fundingSources.get(bankSupportKey) : null);
                }
                // 5.2. Цена контракта
                String contractPriceKey = findPartialKey(financingData, "Цена контракта");
                if (contractPriceKey != null) {
                    Map<String, String> contractPrice = (Map<String, String>) financingData.get(contractPriceKey);
                    if (contractPrice != null) {
                        contract.setContractRightPrice(
                                parseBigDecimal(findAndGet(contractPrice, "Цена за право заключения")));
                    }
                }

                // 5.2. Цена контракта
                Map<String, String> contractPrice = (Map<String, String>) financingData.get("5.2. Цена контракта");
                if (contractPrice != null) {
                    String priceMethodKey = findPartialKey(contractPrice, "Способ указания цены");
                    String priceKey = findPartialKey(contractPrice, "Цена контракта");
                    String vatKey = findPartialKey(contractPrice, "В том числе НДС");
                    String treasuryKey = findPartialKey(contractPrice, "казначейского обеспечения");
                    String formulaKey = findPartialKey(contractPrice, "Формула цены");
                    String currencyKey = findPartialKey(contractPrice, "Валюта контракта");

                    contract.setPriceIndicationMethod(priceMethodKey != null ? contractPrice.get(priceMethodKey) : null);
                    contract.setContractPrice(priceKey != null ? parseBigDecimal(contractPrice.get(priceKey)) : null);
                    contract.setIncludingVat(vatKey != null ? parseBigDecimal(contractPrice.get(vatKey)) : null);
                    contract.setTreasuryGuaranteeAmount(treasuryKey != null ? parseBigDecimal(contractPrice.get(treasuryKey)) : null);
                    contract.setPriceFormula(formulaKey != null ? contractPrice.get(formulaKey) : null);
                    contract.setCurrency(currencyKey != null ? contractPrice.get(currencyKey) : null);
                }

                // 5.3. Порядок расчетов
                Map<String, String> paymentTerms = (Map<String, String>) financingData.get("5.3. Порядок расчетов");
                if (paymentTerms != null) {
                    String advanceKey = findPartialKey(paymentTerms, "Предусмотрена выплата аванса");
                    String advancePercKey = findPartialKey(paymentTerms, "Размер аванса (%)");
                    String advanceAmtKey = findPartialKey(paymentTerms, "Размер аванса в валюте");
                    String taxDeductKey = findPartialKey(paymentTerms, "Суммы, уплачиваемые заказчиком");
                    String penaltyKey = findPartialKey(paymentTerms, "Предусмотрено удержание");

                    contract.setAdvancePaymentAvailable(advanceKey != null ? paymentTerms.get(advanceKey) : null);
                    contract.setAdvancePercentage(advancePercKey != null ? parseBigDecimal(paymentTerms.get(advancePercKey)) : null);
                    contract.setAdvanceAmount(advanceAmtKey != null ? parseBigDecimal(paymentTerms.get(advanceAmtKey)) : null);
                    contract.setTaxDeductionApplied(taxDeductKey != null ? paymentTerms.get(taxDeductKey) : null);
                    contract.setPenaltyDeductionApplied(penaltyKey != null ? paymentTerms.get(penaltyKey) : null);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при заполнении модели контракта: " + e.getMessage());
            throw new RuntimeException("Не удалось заполнить данные контракта", e);
        }
    }
    private void fillSupplierModel(Supplier supplier, Map<String, Object> contractData) {
        try {
            // 2.2. Информация о поставщике
            Map<String, Object> contractParties = (Map<String, Object>) contractData.get("2. Стороны контракта");
            if (contractParties != null) {
                Map<String, String> supplierInfo = (Map<String, String>) contractParties.get("2.2. Информация о поставщике");
                if (supplierInfo != null) {
                    supplier.setType(supplierInfo.get("Вид"));
                    supplier.setName(supplierInfo.get("Наименование организации (ФИО физического лица)"));

                    // Парсим страну (формат: "Российская Федерация (643)")
                    String countryInfo = supplierInfo.get("Наименование страны, код по ОКСМ");
                    if (countryInfo != null && countryInfo.contains("(")) {
                        supplier.setCountryName(countryInfo.substring(0, countryInfo.indexOf("(")).trim());
                        supplier.setCountryCode(countryInfo.substring(countryInfo.indexOf("(") + 1, countryInfo.indexOf(")")).trim());
                    }

                    supplier.setAddress(supplierInfo.get("Адрес места нахождения (адрес места жительства)"));
                    supplier.setPostalAddress(supplierInfo.get("Почтовый адрес"));
                    supplier.setOgrn(supplierInfo.get("ОГРН (для юридических лиц)"));
                    supplier.setInn(supplierInfo.get("ИНН"));
                    supplier.setKpp(supplierInfo.get("КПП (для юридических лиц)"));
                    supplier.setStatus(supplierInfo.get("Статус"));
                    supplier.setEmail(supplierInfo.get("Электронная почта"));
                    supplier.setPhone(supplierInfo.get("Телефон"));

                    // Парсим информацию о руководителе (если есть)
                    String directorInfo = supplierInfo.get("Руководитель (лицо, имеющее право без доверенности действовать от имени юридического лица)");
                    if (directorInfo != null) {
                        // Можно сохранить дополнительную информацию о руководителе
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при заполнении данных поставщика: " + e.getMessage());
        }
    }
    private BigDecimal parseBigDecimal(String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) return null;
        try {
            return new BigDecimal(numberStr.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга числа: " + numberStr);
            return null;
        }
    }
    private String findPartialKey(Map<String, ?> map, String partialKey) {
        if (map == null) return null;
        for (String key : map.keySet()) {
            if (key.contains(partialKey)) {
                return key;
            }
        }
        return null;
    }
    private String findAndGet(Map<String, String> map, String partialKey) {
        if (map == null) return null;
        String key = findPartialKey(map, partialKey);
        return key != null ? map.get(key) : null;
    }
    private String extractRegNumber(String url) {
        try {
            // Извлекаем номер после regNumber=
            Pattern pattern = Pattern.compile("regNumber=(\\d+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при извлечении номера закупки: " + e.getMessage());
        }
        return null;
    }
    private String parseStateContractId(WebDriver driver) {
        try {
            WebElement contractIdElement = driver.findElement(By.cssSelector("span.cardMainInfo__purchaseLink.distancedText a"));
            String fullText = contractIdElement.getText().trim();
            // Извлекаем часть после "№ " (номер контракта)
            if (fullText.startsWith("№ ")) {
                return fullText.substring(2).trim();
            }
            return fullText;
        } catch (Exception e) {
            System.out.println("Не удалось извлечь stateContractId: " + e.getMessage());
            return null;
        }
    }

    private Map<String, String> parseSimpleTable(WebElement element) {
        Map<String, String> tableData = new LinkedHashMap<>();
        try {
            List<WebElement> tables = element.findElements(By.cssSelector("table.printFormTbl"));
            for (WebElement table : tables) {
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
                for (WebElement row : rows) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (cells.size() == 2) {
                        String key = cells.get(0).getText().trim();
                        String value = cells.get(1).getText().trim();
                        if (!key.isEmpty()) {
                            tableData.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге простой таблицы: " + e.getMessage());
        }
        return tableData;
    }

    private List<Map<String, String>> parseProcurementObjectsTable(WebElement subBlock) {
        List<Map<String, String>> objects = new ArrayList<>();

        try {
            WebElement table = subBlock.findElement(By.cssSelector("table.table-centred-data"));

            // Получаем заголовки из ПЕРВОЙ строки thead (настоящие названия столбцов)
            List<WebElement> headerRows = table.findElements(By.cssSelector("thead tr"));
            if (headerRows.isEmpty()) {
                System.out.println("Не найдены строки заголовков таблицы");
                return objects;
            }

            // Берем первую строку с заголовками (вторая строка с цифрами нам не нужна)
            List<String> headers = headerRows.get(0).findElements(By.tagName("td")).stream()
                    .map(WebElement::getText)
                    .map(text -> text.replaceAll("\\s+", " ").trim())
                    .collect(Collectors.toList());

            // Парсим строки с данными из tbody
            List<WebElement> dataRows = table.findElements(By.cssSelector("tbody tr:not(:last-child)"));
            for (WebElement row : dataRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() >= headers.size()) {
                    Map<String, String> objectData = new LinkedHashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        String cellText = cells.get(i).getText()
                                .replaceAll("\\s+", " ")
                                .trim();
                        objectData.put(headers.get(i), cellText);
                    }
                    objects.add(objectData);
                }
            }

            // Логирование для отладки
            System.out.println("Заголовки таблицы: " + headers);
            System.out.println("Найдено объектов: " + objects.size());
            for (Map<String, String> obj : objects) {
                System.out.println("Объект: " + obj);
            }

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге таблицы объектов закупки: " + e.getMessage());
            e.printStackTrace();
        }

        return objects;
    }

    private Map<String, String> parseDeliveryPlaceTable(WebElement subBlock) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            WebElement table = subBlock.findElement(By.cssSelector("table.printFormTbl"));
            List<WebElement> headers = table.findElements(By.cssSelector("thead tr td"));
            List<WebElement> dataRows = table.findElements(By.cssSelector("tbody tr"));

            if (!dataRows.isEmpty()) {
                List<WebElement> dataCells = dataRows.get(0).findElements(By.tagName("td"));

                for (int i = 0; i < Math.min(headers.size(), dataCells.size()); i++) {
                    String header = headers.get(i).getText().trim();
                    String value = dataCells.get(i).getText().trim();
                    if (!header.isEmpty() && !value.isEmpty()) {
                        result.put(header, value);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге таблицы места поставки: " + e.getMessage());
        }
        return result;
    }
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim().replaceAll("[^\\d.]", ""),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга даты: " + dateStr);
            return null;
        }
    }
}
