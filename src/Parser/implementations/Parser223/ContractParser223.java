package Parser.implementations.Parser223;


import Parser.Database.models.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

import static Parser.utils.ParserUtils.parseBigDecimal;
import static Parser.utils.ParserUtils.parseDate;

public class ContractParser223 {


    public Contract parseContractInfo(String originalUrl, WebDriver driver, WebDriverWait wait) {
        try {
            Contract contract = new Contract();
            String contractUrl = findContractUrl(originalUrl, driver);
            System.out.println("Finding contract URL: " + contractUrl);

            if (contractUrl != null) {
                driver.get(contractUrl);
                Map<String, String> contractDetails = parseContractDetails(driver, wait);
                Map<String, String>  mainInfo = parseGeneralInfo(driver, wait);
                fillContractModel(contract, contractDetails,mainInfo);
                parseGeneralInfo(driver, wait);
            }
            return contract;
        } catch (Exception e) {
            System.err.println("Error parsing contract info: " + e.getMessage());
            return null;
        }
    }

    public List<ProcurementObject> parseContractSubjects(String originalUrl, WebDriver driver, WebDriverWait wait) {
        try {
            String contractUrl = findContractUrl(originalUrl, driver);
            if (contractUrl == null) {
                return Collections.emptyList();
            }

            String subjectUrl = convertContractInfoUrlToSubjectUrl(contractUrl);
            if (subjectUrl == null) {
                return Collections.emptyList();
            }

            driver.get(subjectUrl);
            System.out.println("Navigated to subject page: " + subjectUrl);
            return parseSubjectTable(driver);
        } catch (Exception e) {
            System.err.println("Error parsing contract subjects: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    public void fillContractModel(Contract contract, Map<String, String> contractDetails,Map<String, String> mainInfo) {
        try {
            // Заполняем основные поля контракта
            contract.setRegistryNumber(contractDetails.get("Номер договора (заголовок)"));
            contract.setProcurementIdentificationCode(contractDetails.get("Номер договора"));
            contract.setStatus(contractDetails.get("Статус контракта"));
            contract.setSoleSupplierDocumentDetails(contractDetails.get("Заказчик"));
            contract.setContractPrice(parseBigDecimal(contractDetails.get("Цена договора")));
            contract.setStartDate(parseDate(contractDetails.get("Дата заключения")));
            contract.setEndDate(parseDate(contractDetails.get("Срок исполнения (окончание)")));
            contract.setStartDate(parseDate(contractDetails.get("Дата обновления")));

            // Заполняем данные из блока "Общая информация"

            contract.setSubject(mainInfo.get("Предмет договора"));
            contract.setBankingTreasurySupportInfo(mainInfo.get("Способ закупки"));



            // Установка валюты (предполагаем рубль, если не указано иное)
            contract.setCurrency("₽");

        } catch (Exception e) {
            throw new RuntimeException("Ошибка заполнения модели контракта: " + e.getMessage(), e);
        }
    }


    public String convertContractInfoUrlToSubjectUrl(String contractInfoUrl) {
        try {
            if (contractInfoUrl == null || contractInfoUrl.isEmpty()) {
                System.err.println("URL не может быть null или пустым");
                return null;
            }

            if (!contractInfoUrl.startsWith("https://zakupki.gov.ru")) {
                System.err.println("URL должен начинаться с https://zakupki.gov.ru");
                return null;
            }

            if (!contractInfoUrl.contains("/epz/contractfz223/card/contract-info.html?id=")) {
                System.err.println("URL должен содержать '/epz/contractfz223/card/contract-info.html?id='");
                return null;
            }

            return contractInfoUrl.replace("/contract-info.html", "/contract-subject.html");

        } catch (Exception e) {
            System.err.println("Ошибка при преобразовании URL: " + e.getMessage());
            return null;
        }
    }

    public String findContractUrl( String url, WebDriver driver) {
        // Создаём новый wait с таймаутом 5 секунд для этого метода
        driver.get(url);
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            // Быстрая проверка наличия таблицы с контрактами
            List<WebElement> tables = shortWait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.xpath("//div[@id='inner-html']//table")));

            if (!tables.isEmpty()) {
                // Быстрая проверка наличия ссылки на контракт
                WebElement contractLink = shortWait.until(ExpectedConditions
                        .presenceOfElementLocated(By.xpath("//div[@id='inner-html']//table//a[contains(@href, '/epz/contractfz223/card/contract-info.html')]")));

                String relativeUrl = contractLink.getAttribute("href");
                return relativeUrl.startsWith("http") ? relativeUrl : "https://zakupki.gov.ru" + relativeUrl;
            }

            // Если таблицы нет, проверяем сообщение об отсутствии данных (быстрая проверка)
            List<WebElement> noDataMessages = driver.findElements(By.xpath("//div[contains(@class, 'section__title') and contains(text(), 'Сведения отсутствуют')]"));
            if (!noDataMessages.isEmpty()) {
                return null;
            }

            return null;

        } catch (TimeoutException e) {
            return null;
        } catch (Exception e) {
            System.err.println("Error finding contract URL: " + e.getMessage());
            return null;
        }

    }

    public Map<String, String> parseContractDetails(WebDriver driver, WebDriverWait wait) {
        Map<String, String> contractData = new HashMap<>();
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            // Парсим основную информацию о контракте
            WebElement mainInfo = shortWait.until(ExpectedConditions
                    .presenceOfElementLocated(By.cssSelector("div.cardMainInfo")));

            // Номер договора (из заголовка)
            String contractNumber = mainInfo.findElement(By.cssSelector("span.cardMainInfo__purchaseLink a"))
                    .getText().trim();
            contractData.put("Номер договора (заголовок)", contractNumber);
            System.out.println("Номер договора (заголовок): " + contractNumber);

            // Статус контракта
            String contractStatus = mainInfo.findElement(By.cssSelector("span.cardMainInfo__state"))
                    .getText().trim();
            contractData.put("Статус контракта", contractStatus);
            System.out.println("Статус контракта: " + contractStatus);

            // Номер договора (в теле)
            String contractNumberBody = mainInfo.findElement(By.xpath(".//span[contains(text(),'Номер договора')]/following-sibling::span"))
                    .getText().trim();
            contractData.put("Номер договора", contractNumberBody);
            System.out.println("Номер договора: " + contractNumberBody);

            // Заказчик
            String customer = mainInfo.findElement(By.xpath(".//span[contains(text(),'Заказчик')]/following-sibling::span/a"))
                    .getText().trim();
            contractData.put("Заказчик", customer);
            System.out.println("Заказчик: " + customer);

            // Цена договора
            String price = mainInfo.findElement(By.cssSelector("div.rightBlock__price"))
                    .getText().replace("&nbsp;", " ").trim();
            contractData.put("Цена договора", price);
            System.out.println("Цена договора: " + price);

            // Дата заключения
            String conclusionDate = mainInfo.findElement(By.xpath(".//div[contains(text(),'Заключение договора')]/following-sibling::div"))
                    .getText().trim();
            contractData.put("Дата заключения", conclusionDate);
            System.out.println("Дата заключения: " + conclusionDate);

            // Срок исполнения
            String executionPeriodFull = mainInfo.findElement(By.xpath(".//div[contains(text(),'Срок исполнения')]/following-sibling::div"))
                    .getText().replace("&nbsp;", " ").trim();

            // Разделяем даты по дефису и берём последнюю часть
            String[] dates = executionPeriodFull.split("—");
            String endDate = dates.length > 1 ? dates[dates.length - 1].trim() : executionPeriodFull;

            contractData.put("Срок исполнения (окончание)", endDate);
            System.out.println("Срок исполнения: " + endDate);

            // Даты размещения и обновления
            List<WebElement> dateElements = mainInfo.findElements(By.cssSelector("div.rightBlock__text"));
            if (dateElements.size() >= 3) {
                String placementDate = dateElements.get(dateElements.size() - 2).getText().trim();
                String updateDate = dateElements.get(dateElements.size() - 1).getText().trim();

                contractData.put("Дата размещения", placementDate);
                contractData.put("Дата обновления", updateDate);
                System.out.println("Дата размещения: " + placementDate);
                System.out.println("Дата обновления: " + updateDate);
            }

            return contractData;

        } catch (TimeoutException e) {
            System.err.println("Не удалось найти блок с информацией о контракте: " + e.getMessage());
            return contractData;
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге данных контракта: " + e.getMessage());
            return contractData;
        }
    }

    public List<ProcurementObject> parseSubjectTable(WebDriver driver) {
        List<ProcurementObject> procurementObjects = new ArrayList<>();
        System.out.println("\n=== ПАРСИНГ ТАБЛИЦЫ ПРЕДМЕТА ДОГОВОРА ===");

        try {
            WebElement container = driver.findElement(By.cssSelector("div.container.card-common"));
            WebElement table = container.findElement(By.cssSelector("table.table"));
            List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

            System.out.println("Найдено строк в таблице: " + rows.size());
            System.out.println("------------------------------------------------------------------------------------------------------------------------");
            System.out.printf("%-3s | %-60s | %-20s | %-15s | %-20s | %-25s | %-25s | %-15s%n",
                    "№", "Наименование", "ОКПД2", "Количество", "Цена за ед.",
                    "Страна происхождения", "Страна производителя", "Сумма");
            System.out.println("------------------------------------------------------------------------------------------------------------------------");

            for (WebElement row : rows) {
                try {
                    ProcurementObject obj = new ProcurementObject();
                    List<WebElement> cells = row.findElements(By.cssSelector("td"));

                    if (cells.size() >= 8) {
                        // № позиции
                        String positionNumber = cells.get(0).getText().trim();

                        // Наименование
                        String name = cells.get(1).getText()
                                .replaceAll("Тип объекта закупки:.*", "")
                                .replaceAll("\\s+", " ").trim();
                        obj.setName(name);

                        // ОКПД2
                        String okpd2 = cells.get(2).getText().split("\\s+")[0].trim();
                        obj.setKtruOkpd2Codes(okpd2);

                        // Количество и единица измерения
                        String[] quantityParts = cells.get(3).getText().trim().split(" ", 2);
                        if (quantityParts.length > 0) {
                            try {
                                obj.setQuantity(new BigDecimal(quantityParts[0].replace(",", ".")));
                            } catch (NumberFormatException e) {
                                System.out.println("Ошибка парсинга количества: " + cells.get(3).getText());
                            }
                        }
                        if (quantityParts.length > 1) {
                            obj.setUnit(quantityParts[1].trim());
                        }

                        // Цена за единицу
                        try {
                            String priceStr = cells.get(4).getText()
                                    .replaceAll("[^\\d,.]", "")
                                    .replace(",", ".").trim();
                            obj.setPricePerUnit(new BigDecimal(priceStr));
                        } catch (NumberFormatException e) {
                            System.out.println("Ошибка парсинга цены: " + cells.get(4).getText());
                        }

                        // Страны
                        obj.setCountryOfOrigin(cells.get(5).getText().trim());

                        // Объем финансирования (используем totalAmount)
                        try {
                            String amountStr = cells.get(7).getText()
                                    .replaceAll("[^\\d,.]", "")
                                    .replace(",", ".").trim();
                            obj.setTotalAmount(new BigDecimal(amountStr.isEmpty() ? "0" : amountStr));
                        } catch (NumberFormatException e) {
                            System.out.println("Ошибка парсинга суммы: " + cells.get(7).getText());
                        }

                        // Вывод в консоль
                        System.out.printf("%-3s | %-60s | %-20s | %-15s | %-20s | %-25s | %-25s | %-15s%n",
                                positionNumber,
                                name.length() > 60 ? name.substring(0, 57) + "..." : name,
                                okpd2,
                                obj.getQuantity() != null ? obj.getQuantity().toString() : "",
                                obj.getPricePerUnit() != null ? obj.getPricePerUnit().toString() : "",
                                obj.getCountryOfOrigin(),
                                "", // Страна производителя не парсится в вашей модели
                                obj.getTotalAmount() != null ? obj.getTotalAmount().toString() : "");

                        procurementObjects.add(obj);
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при парсинге строки: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Парсим итоговую сумму
            try {
                WebElement totalElement = container.findElement(By.xpath(".//div[contains(text(), 'Итого за счет бюджетных средств')]/following-sibling::div"));
                String totalAmount = totalElement.getText()
                        .replaceAll("[^\\d,.]", "")
                        .replace(",", ".").trim();
                System.out.println("------------------------------------------------------------------------------------------------------------------------");
                System.out.printf("%-3s | %-60s | %-20s | %-15s | %-20s | %-25s | %-25s | %-15s%n",
                        "", "ИТОГО:", "", "", "", "", "", totalAmount);
                System.out.println("------------------------------------------------------------------------------------------------------------------------");
            } catch (Exception e) {
                System.out.println("Не удалось найти итоговую сумму: " + e.getMessage());
            }

            System.out.println("=== ПАРСИНГ ЗАВЕРШЁН. НАЙДЕНО ОБЪЕКТОВ: " + procurementObjects.size() + " ===");

        } catch (NoSuchElementException e) {
            System.out.println("Таблица предмета договора не найдена на странице");
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге таблицы предмета договора: " + e.getMessage());
            e.printStackTrace();
        }

        return procurementObjects;
    }

    public Map<String, String> parseGeneralInfo(WebDriver driver, WebDriverWait wait) {
        Map<String, String> generalInfo = new HashMap<>();
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        System.out.println("\n=== ПАРСИНГ БЛОКА 'ОБЩАЯ ИНФОРМАЦИЯ' ===");

        try {
            List<WebElement> infoBlocks = shortWait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'container')]//div[contains(@class, 'blockInfo')]")));

            boolean found = false;

            for (WebElement block : infoBlocks) {
                try {
                    WebElement title = block.findElement(By.xpath(".//h2[contains(@class, 'blockInfo__title')]"));
                    if (!title.getText().trim().equals("Общая информация")) {
                        continue;
                    }

                    found = true;
                    System.out.println("\nНайден блок 'Общая информация':");
                    System.out.println("--------------------------------");

                    List<WebElement> sections = block.findElements(By.xpath(".//section[contains(@class, 'section')]"));
                    for (WebElement section : sections) {
                        try {
                            String key = section.findElement(By.xpath(".//span[contains(@class, 'section__title')]"))
                                    .getText().trim();

                            String value;
                            try {
                                WebElement link = section.findElement(By.xpath(".//span[contains(@class, 'section__info')]//a"));
                                value = link.getText().trim();
                                generalInfo.put(key, value);
                                System.out.printf("%-30s: %s (ссылка)%n", key, value);
                            } catch (NoSuchElementException e) {
                                value = section.findElement(By.xpath(".//span[contains(@class, 'section__info')]"))
                                        .getText().trim();
                                generalInfo.put(key, value);
                                System.out.printf("%-30s: %s%n", key, value);
                            }
                        } catch (Exception e) {
                            System.err.println("Ошибка при парсинге секции: " + e.getMessage());
                        }
                    }
                    break;

                } catch (NoSuchElementException e) {
                    continue;
                }
            }

            if (!found) {
                System.out.println("Блок 'Общая информация' не найден на странице");
            }

            System.out.println("--------------------------------");
            System.out.println("Всего извлечено полей: " + generalInfo.size());
            System.out.println("=== ПАРСИНГ ЗАВЕРШЁН ===\n");

        } catch (TimeoutException e) {
            System.err.println("Таймаут при поиске блоков информации: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Критическая ошибка при парсинге: " + e.getMessage());
        }

        return generalInfo;
    }


}