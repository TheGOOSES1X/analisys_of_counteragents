package Parser.implementations.Parser44.Contracts;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public  class WebTableParserContract {

    public static Map<String, String> parseSimpleTable(WebElement element) {
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

    public static List<Map<String, String>> parseProcurementObjectsTable(WebElement subBlock) {
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

    public static Map<String, String> parseDeliveryPlaceTable(WebElement subBlock) {
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

    public static Map<String, String> parseCommonInfoSections(WebElement container) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            List<WebElement> sections = container.findElements(By.cssSelector("section.blockInfo__section"));

            for (WebElement section : sections) {
                try {
                    WebElement titleElement = section.findElement(By.cssSelector("span.section__title"));
                    WebElement infoElement = section.findElement(By.cssSelector("span.section__info"));

                    String title = titleElement.getText().trim();
                    String info = infoElement.getText().trim();

                    if (!title.isEmpty() && !info.isEmpty()) {
                        result.put(title, info);
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге секций common-info: " + e.getMessage());
        }
        return result;
    }
}
