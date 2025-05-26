package Parser.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

public class Okpd2Converter {
    private static Map<String, Okpd2Entry> okpd2Data = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    // Загрузка данных из JSON файла
    public static void loadFromJson(String resourcePath) throws IOException {
        try (InputStream inputStream = Okpd2Converter.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            okpd2Data = mapper.readValue(inputStream, new TypeReference<Map<String, Okpd2Entry>>(){});
        }
    }

    // Получение ID по коду ОКПД2
    public static String getOkpd2Id(String code) {
        // 1. Сначала проверяем точное совпадение
        if (okpd2Data.containsKey(code)) {
            return okpd2Data.get(code).getId();
        }

        // 2. Ищем код в конце строки (для случаев с префиксом "C.")
        for (Map.Entry<String, Okpd2Entry> entry : okpd2Data.entrySet()) {
            if (entry.getKey().endsWith("." + code) || entry.getKey().equals(code)) {
                return entry.getValue().getId();
            }
        }

        // 3. Ищем частичное совпадение
        for (String fullCode : okpd2Data.keySet()) {
            if (fullCode.contains(code)) {
                return okpd2Data.get(fullCode).getId();
            }
        }

        return null;
    }
    public static void fillComboBoxWithCurrencies(JComboBox<String> comboBox, String resourcePath) {
        try {
            comboBox.removeAllItems();

            try (InputStream inputStream = Okpd2Converter.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    throw new IOException("Resource not found: " + resourcePath);
                }
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                JsonArray currencies = JsonParser.parseString(content).getAsJsonArray();

                for (JsonElement element : currencies) {
                    JsonObject currency = element.getAsJsonObject();
                    String name = currency.get("name").getAsString();
                    comboBox.addItem(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Ошибка при загрузке данных о валютах: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String getCurrencyIdByName(String currencyName) {
        try (InputStream inputStream = Okpd2Converter.class.getClassLoader().getResourceAsStream("resources/currency.json")) {
            if (inputStream == null) {
                return null;
            }
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonArray currencies = JsonParser.parseString(content).getAsJsonArray();

            for (JsonElement element : currencies) {
                JsonObject currency = element.getAsJsonObject();
                if (currency.get("name").getAsString().equals(currencyName)) {
                    return currency.get("id").getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    // Получение полного названия по коду
    public static String getOkpd2Title(String code) {
        return okpd2Data.containsKey(code) ? okpd2Data.get(code).getTitle() : null;
    }
}