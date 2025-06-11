package Parser.implementations.Parser223.Contracts;


import Parser.Database.models.ProcurementObject;
import Parser.implementations.Parser223.Contracts.Interfaces.IContractSubjectsParser;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.math.BigDecimal;
import java.util.*;

public class ContractSubjectsParser implements IContractSubjectsParser {
    @Override
    public List<Map<String, String>>  parseSubjects(WebDriver driver) {
        List<Map<String, String>> rawData = new ArrayList<>();

        try {
            WebElement container = driver.findElement(By.cssSelector("div.container.card-common"));
            WebElement table = container.findElement(By.cssSelector("table.table"));
            List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

            for (WebElement row : rows) {
                try {
                    List<WebElement> cells = row.findElements(By.cssSelector("td"));
                    if (cells.size() >= 8) {
                        Map<String, String> rowData = new HashMap<>();

                        rowData.put("positionNumber", cells.get(0).getText().trim());
                        rowData.put("name", cells.get(1).getText()
                                .replaceAll("Тип объекта закупки:.*", "")
                                .replaceAll("\\s+", " ").trim());
                        rowData.put("okpd2", cells.get(2).getText().split("\\s+")[0].trim());

                        String[] quantityParts = cells.get(3).getText().trim().split(" ", 2);
                        rowData.put("quantity", quantityParts.length > 0 ? quantityParts[0] : "");
                        rowData.put("unit", quantityParts.length > 1 ? quantityParts[1].trim() : "");

                        rowData.put("pricePerUnit", cells.get(4).getText()
                                .replaceAll("[^\\d,.]", "")
                                .replace(",", ".").trim());

                        rowData.put("countryOfOrigin", cells.get(5).getText().trim());

                        rowData.put("totalAmount", cells.get(7).getText()
                                .replaceAll("[^\\d,.]", "")
                                .replace(",", ".").trim());

                        rawData.add(rowData);
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при сборе данных из строки: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Таблица не найдена: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка при сборе данных таблицы: " + e.getMessage());
        }

        return rawData;
    }
}