package Parser.implementations.Parser223;

import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class ExtractInfoFromDocument {
    private static final String DOWNLOADS_DIR = "downloads";
    private static final Pattern INN_PATTERN = Pattern.compile("ИНН (\\d{10,12})");
    private static final Pattern KPP_PATTERN = Pattern.compile("КПП (\\d{9})");
    private static final Pattern OGRN_PATTERN = Pattern.compile("ОГРН (\\d{13})");

    public Map<String, Map<String, Object>> extractParticipantsFromAllDocuments() {
        Map<String, Map<String, Object>> allParticipants = new LinkedHashMap<>();
        try {
            Files.list(Paths.get(DOWNLOADS_DIR))
                    .filter(path -> path.toString().endsWith(".docx"))
                    .forEach(path -> {
                        Map<String, Map<String, Object>> docParticipants = extractParticipantsFromDocument(path);
                        docParticipants.forEach((key, value) -> {
                            // Проверка на уникальность по ИНН или другому ключу
                            if (!allParticipants.containsKey(key)) {
                                allParticipants.put(key, value);
                            }
                        });
                    });
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файлов из папки downloads: " + e.getMessage());
        }
        return allParticipants;
    }

    public Map<String, Map<String, Object>> extractParticipantsFromDocument(Path documentPath) {
        Map<String, Map<String, Object>> participants = new LinkedHashMap<>();
        try (InputStream is = Files.newInputStream(documentPath);
             XWPFDocument document = new XWPFDocument(is)) {

            for (XWPFTable table : document.getTables()) {
                processTable(table, participants);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении документа " + documentPath + ": " + e.getMessage());
        }
        return participants;
    }

    private void processTable(XWPFTable table, Map<String, Map<String, Object>> participants) {
        List<String> headers = new ArrayList<>();
        boolean headersFound = false;

        for (XWPFTableRow row : table.getRows()) {
            if (!headersFound) {
                headers = extractHeaders(row);
                // Проверяем, есть ли в заголовках ключевые слова
                if (headers.stream().anyMatch(h -> h.toLowerCase().contains("наименование") ||
                        h.toLowerCase().contains("участник"))) {
                    headersFound = true;
                }
                continue;
            }

            Map<String, Object> participantData = extractParticipantData(row, headers);
            if (participantData != null && participantData.containsKey("inn")) {
                String key = (String) participantData.get("inn");
                participants.put(key, participantData);
            }
        }
    }

    private List<String> extractHeaders(XWPFTableRow row) {
        List<String> headers = new ArrayList<>();
        for (XWPFTableCell cell : row.getTableCells()) {
            headers.add(cell.getText().trim());
        }
        return headers;
    }

    private Map<String, Object> extractParticipantData(XWPFTableRow row, List<String> headers) {
        Map<String, Object> participantData = new HashMap<>();
        List<XWPFTableCell> cells = row.getTableCells();

        try {
            for (int i = 0; i < headers.size() && i < cells.size(); i++) {
                String header = headers.get(i).toLowerCase().trim();
                String value = cells.get(i).getText().trim();

                // Более гибкое сопоставление заголовков
                if (header.contains("номер") || header.contains("№")) {
                    participantData.put("bidNumber", value);
                }
                else if (header.contains("наименование") || header.contains("участник")) {
                    // Разделяем название компании и адрес
                    String[] parts = value.split(",", 2);
                    participantData.put("name", parts[0].trim());
                    if (parts.length > 1) {
                        participantData.put("address", extractAddress(parts[1].trim()));
                    }
                    extractOrganizationDetails(value, participantData);
                }
                else if (header.contains("без ндс") || header.contains("ценовое предложение без")) {
                    participantData.put("priceWithoutVAT", parsePrice(value));
                }
                else if (header.contains("с ндс") || header.contains("ценовое предложение с")) {
                    participantData.put("priceWithVAT", parsePrice(value));
                }
                else if (header.contains("ставка") || header.contains("ндс")) {
                    participantData.put("vatRate", value);
                }
                else if (header.contains("дата") || header.contains("время") || header.contains("регистрации")) {
                    participantData.put("registrationDate", value);
                }
                else if (header.contains("решение") || header.contains("допуск")) {
                    participantData.put("admissionDecision", value);
                }
                else if (header.contains("основание")) {
                    participantData.put("decisionBasis", value);
                }
            }

            // Дополнительная проверка: если не нашли ИНН в ячейках, попробуем найти во всем тексте строки
            if (!participantData.containsKey("inn")) {
                String fullRowText = row.getTableCells().stream()
                        .map(XWPFTableCell::getText)
                        .collect(Collectors.joining(" "));
                extractOrganizationDetails(fullRowText, participantData);
            }

            return participantData;
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге участника: " + e.getMessage());
            return null;
        }
    }
    private String extractAddress(String text) {
        // Удаляем ИНН/КПП/ОГРН из адреса, если они есть
        text = text.replaceAll("ИНН \\d{10,12}", "")
                .replaceAll("КПП \\d{9}", "")
                .replaceAll("ОГРН \\d{13}", "")
                .trim();

        // Удаляем возможные двойные запятые и пробелы
        return text.replaceAll(",+", ",")
                .replaceAll("\\s+", " ")
                .trim();
    }
    private void extractOrganizationDetails(String text, Map<String, Object> participantData) {
        Matcher innMatcher = INN_PATTERN.matcher(text);
        if (innMatcher.find()) {
            participantData.put("inn", innMatcher.group(1));
        }

        Matcher kppMatcher = KPP_PATTERN.matcher(text);
        if (kppMatcher.find()) {
            participantData.put("kpp", kppMatcher.group(1));
        }

        Matcher ogrnMatcher = OGRN_PATTERN.matcher(text);
        if (ogrnMatcher.find()) {
            participantData.put("ogrn", ogrnMatcher.group(1));
        }
    }

    private double parsePrice(String priceStr) {
        try {
            return Double.parseDouble(priceStr.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}