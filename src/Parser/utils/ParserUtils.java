package Parser.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim().replaceAll("[^\\d.]", ""),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга даты: " + dateStr);
            return null;
        }
    }
    public static String getValueOrNull(Map<String, String> dataMap, String key) {
        if (dataMap == null || key == null) {
            return null;
        }
        String value = dataMap.get(key);
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }
    // Вспомогательные методы
    public static Map<String, String> getSectionData(Map<String, Object> participantData, String sectionName) {
        Object section = participantData.get(sectionName);
        if (section instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> sectionMap = (Map<String, String>) section;
            return sectionMap;
        }
        return null;
    }

    public static BigDecimal parseBigDecimal(String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) return null;
        try {
            // Удаляем всё, кроме цифр, точек, запятых и пробелов
            String cleaned = numberStr.replaceAll("[^\\d,.]", "").trim();
            // Заменяем запятую на точку и удаляем пробелы (разделители тысяч)
            cleaned = cleaned.replace(",", ".").replace(" ", "");
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            System.out.println("Ошибка парсинга числа: " + numberStr);
            return null;
        }
    }
    public static String findPartialKey(Map<String, ?> map, String partialKey) {
        if (map == null) return null;
        for (String key : map.keySet()) {
            if (key.contains(partialKey)) {
                return key;
            }
        }
        return null;
    }
    public static String findAndGet(Map<String, String> map, String partialKey) {
        if (map == null) return null;
        String key = findPartialKey(map, partialKey);
        return key != null ? map.get(key) : null;
    }
    public static String extractRegNumber(String url) {
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
    public static String parseStateContractId(WebDriver driver) {
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

    public static BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) return null;
        try {
            return new BigDecimal(priceStr.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга цены: " + priceStr);
            return null;
        }
    }

}
