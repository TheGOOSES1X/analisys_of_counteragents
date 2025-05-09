package Parser.implementations.Parser44.Contracts;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // Вспомогательный метод для безопасного получения текста элемента
    private String getElementTextSafely(WebElement parent, String cssSelector) {
        try {
            return parent.findElement(By.cssSelector(cssSelector)).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }
}
