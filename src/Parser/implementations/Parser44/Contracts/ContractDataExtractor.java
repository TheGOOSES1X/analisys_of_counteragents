package Parser.implementations.Parser44.Contracts;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Parser.implementations.Parser44.Contracts.ParserUtils.parseStateContractId;
import static Parser.implementations.Parser44.Contracts.WebTableParserContract.*;

public class ContractDataExtractor {
    public Map<String, Object> parseContractDraft(String contractDraftUrl, WebDriver driver, WebDriverWait wait) {
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

    public Map<String, Object> parseCommonInfoContract(String url, WebDriver driver, WebDriverWait wait) {
        Map<String, Object> contractData = new LinkedHashMap<>();

        String stateContractId = parseStateContractId(driver);
        if (stateContractId != null) {
            contractData.put("state_contract_id", stateContractId);
        }
        try {
            driver.get(url);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.container")));
            // Получаем все блоки с информацией
            List<WebElement> infoBlocks = driver.findElements(By.cssSelector("div.blockInfo"));
            for (WebElement block : infoBlocks) {
                // Извлекаем заголовок блока (например "Информация о контракте")
                String blockTitle = getElementTextSafely(block, "h2.blockInfo__title");

                // Парсим все секции в блоке
                List<WebElement> sections = block.findElements(By.cssSelector("section.blockInfo__section"));
                Map<String, String> sectionData = new LinkedHashMap<>();

                for (WebElement section : sections) {
                    String title = getElementTextSafely(section, "span.section__title");
                    String value = getElementTextSafely(section, "span.section__info")
                            .replace("&nbsp;", " ") // Заменяем HTML-пробелы
                            .trim();

                    if (!title.isEmpty() && !value.isEmpty()) {
                        sectionData.put(title, value);
                    }
                }
                // Добавляем данные блока в общий результат
                if (!sectionData.isEmpty()) {
                    if (blockTitle != null && !blockTitle.isEmpty()) {
                        contractData.put(blockTitle, sectionData);
                    } else {
                        contractData.putAll(sectionData);
                    }
                }
            }

//            // Дополнительно парсим таблицы, если они есть
//            Map<String, String> tablesData = WebTableParserContract.parseSimpleTable(driver);
//            contractData.putAll(tablesData);

            // Дополнительно парсим специальные блоки (место поставки и т.д.)
            try {
                WebElement deliveryPlaceBlock = driver.findElement(By.xpath("//*[contains(text(),'Место поставки')]/ancestor::div[contains(@class,'blockInfo')]"));
                Map<String, String> deliveryPlace = WebTableParserContract.parseDeliveryPlaceTable(deliveryPlaceBlock);
                contractData.put("Место поставки", deliveryPlace);
            } catch (Exception e) {
                System.out.println("Блок места поставки не найден: " + e.getMessage());
            }

//            // Вывод для отладки
//            System.out.println("=== Парсинг common-info.html завершен ===");
//            System.out.println("Извлечено полей: " + contractData.size());
//            contractData.forEach((k, v) -> System.out.println(k + ": " + v));

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге common-info.html: " + e.getMessage());
            throw new RuntimeException("Не удалось распарсить страницу common-info.html", e);
        }

        return contractData;
    }


    public Map<String, Object> parseAndPrintGeneralContractData(String contractDraftUrl,WebDriver driver, WebDriverWait wait) {
        Map<String, Object> contractData = new LinkedHashMap<>();

        try {
            driver.get(contractDraftUrl);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.container")));

            // Получаем все блоки с информацией
            List<WebElement> infoBlocks = driver.findElements(By.cssSelector("div.blockInfo"));

            for (WebElement block : infoBlocks) {
                // Извлекаем заголовок блока
                String blockTitle = getElementTextSafely(block, "h2.blockInfo__title, h2.blockInfo__title_sub");

                // Парсим все секции в блоке
                List<WebElement> sections = block.findElements(By.cssSelector("section.blockInfo__section"));
                Map<String, String> sectionData = new LinkedHashMap<>();

                for (WebElement section : sections) {
                    String title = getElementTextSafely(section, "span.section__title");
                    String value = getElementTextSafely(section, "span.section__info")
                            .replace("&nbsp;", " ") // Заменяем HTML-пробелы
                            .trim();

                    if (!title.isEmpty() && !value.isEmpty()) {
                        sectionData.put(title, value);
                    } else if (title.isEmpty() && !value.isEmpty()) {
                        // Для секций без заголовка (как в блоке национального режима)
                        sectionData.put("Информация", value);
                    }
                }

                // Обработка таблиц внутри блоков
                try {
                    WebElement table = block.findElement(By.cssSelector("table.blockInfo__table"));
                    Map<String, String> tableData = parseSimpleTable(table);
                    sectionData.putAll(tableData);
                } catch (NoSuchElementException e) {
                    // Таблица не найдена - это нормально
                }

                // Добавляем данные блока в общий результат
                if (!sectionData.isEmpty()) {
                    if (blockTitle != null && !blockTitle.isEmpty()) {
                        contractData.put(blockTitle, sectionData);
                    } else {
                        contractData.putAll(sectionData);
                    }
                }
            }

            // Вывод результата в консоль
//            System.out.println("=== Результат парсинга общих данных контракта ===");
//            for (Map.Entry<String, Object> entry : contractData.entrySet()) {
//                System.out.println("\n" + entry.getKey() + ":");
//                if (entry.getValue() instanceof Map) {
//                    Map<?, ?> subMap = (Map<?, ?>) entry.getValue();
//                    subMap.forEach((k, v) -> System.out.println("  " + k + ": " + v));
//                } else {
//                    System.out.println("  " + entry.getValue());
//                }
//            }

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге общих данных контракта: " + e.getMessage());
        }

        return contractData;
    }

    public Map<String, Object> parseSuppliersInfo(String url, WebDriver driver, WebDriverWait wait) {
        Map<String, Object> suppliersData = new LinkedHashMap<>();
        driver.get(url);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h2[contains(text(), 'Информация о поставщиках')]")));

            List<WebElement> tables = driver.findElements(
                    By.xpath("//h2[contains(text(), 'Информация о поставщиках')]/following::table[contains(@class, 'blockInfo__table')]"));

            List<Map<String, String>> suppliersList = new ArrayList<>();

            for (WebElement table : tables) {
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr.tableBlock__row"));

                for (WebElement row : rows) {
                    Map<String, String> supplierInfo = new LinkedHashMap<>();

                    // Получаем все ячейки в строке (6 колонок, последняя пустая)
                    List<WebElement> cells = row.findElements(By.cssSelector("td"));

                    // 1. Организация и ИНН (первая колонка)
                    WebElement orgCell = cells.get(0);
                    String orgText = orgCell.getText();
                    String[] orgLines = orgText.split("\n");

                    supplierInfo.put("Организация", orgLines[0].trim());

                    // ИНН
                    try {
                        String inn = orgCell.findElement(By.xpath(".//span[contains(@class,'grey-main-light') and contains(text(),'ИНН:')]/following-sibling::span"))
                                .getText().trim();
                        supplierInfo.put("ИНН", inn);
                    } catch (NoSuchElementException e) {
                        supplierInfo.put("ИНН", null);
                    }

                    // КПП
                    try {
                        String kpp = orgCell.findElement(By.xpath(".//span[contains(@class,'grey-main-light') and contains(text(),'КПП:')]/following-sibling::span"))
                                .getText().trim();
                        supplierInfo.put("КПП", kpp);
                    } catch (NoSuchElementException e) {
                        supplierInfo.put("КПП", null);
                    }

                    // 2. Страна и код страны (вторая колонка)
                    if (cells.size() > 1) {
                        String countryText = cells.get(1).getText().trim();
                        String[] countryParts = countryText.split("\n");
                        supplierInfo.put("Страна", countryParts[0].trim());
                        if (countryParts.length > 1) {
                            supplierInfo.put("Код страны", countryParts[1].trim());
                        }
                    }

                    // 3. Адрес места нахождения (третья колонка)
                    if (cells.size() > 2) {
                        supplierInfo.put("Адрес места нахождения", cells.get(2).getText().trim());
                    }

                    // 4. Почтовый адрес (четвертая колонка)
                    if (cells.size() > 3) {
                        supplierInfo.put("Почтовый адрес", cells.get(3).getText().trim());
                    }

                    // 5. Контактная информация (пятая колонка)
                    if (cells.size() > 4) {
                        String contactsText = cells.get(4).getText().trim();
                        String[] contacts = contactsText.split("\n");
                        if (contacts.length > 0) {
                            supplierInfo.put("Телефон", contacts[0].trim());
                        }
                        if (contacts.length > 1) {
                            supplierInfo.put("Email", contacts[1].trim());
                        }
                    }

                    suppliersList.add(supplierInfo);
                }
            }

            suppliersData.put("Поставщики", suppliersList);

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге информации о поставщиках: " + e.getMessage());
            e.printStackTrace();
        }

        return suppliersData;
    }

    // Вспомогательный метод для безопасного получения текста элемента
    private String getElementTextSafely(WebElement parent, String cssSelector) {
        try {
            return parent.findElement(By.cssSelector(cssSelector)).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}
