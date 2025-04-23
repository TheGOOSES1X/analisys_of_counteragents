package Parser.utils;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

public class Okpd2Converter {
    private static Map<String, Okpd2Entry> okpd2Data = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    // Загрузка данных из JSON файла
    public static void loadFromJson(String filePath) throws IOException {
        okpd2Data = mapper.readValue(
                new File(filePath),
                new TypeReference<Map<String, Okpd2Entry>>(){}
        );
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

    // Получение полного названия по коду
    public static String getOkpd2Title(String code) {
        return okpd2Data.containsKey(code) ? okpd2Data.get(code).getTitle() : null;
    }
}