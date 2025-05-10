package Parser.implementations.Parser223;


import Parser.Database.models.*;

import Parser.implementations.Parser44.DatabaseService;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DocumentParser {
    private final DocumentDraftUrlFinder urlExtractor;
    private final ExtractInfoFromDocument dataExtractor;
    private final SupplierModelFiller modelFiller;
    private final DatabaseService databaseService;

    public DocumentParser() {
        this.urlExtractor = new DocumentDraftUrlFinder();
        this.dataExtractor = new ExtractInfoFromDocument();
        this.modelFiller = new SupplierModelFiller();
        this.databaseService = new DatabaseService();
    }

    public void parseDocumentInfo(String originalUrl, WebDriver driver, WebDriverWait wait) {

        try {
            // 1. Находим URL страницы с документами
            String documentsUrl = urlExtractor.findContractDraftUrl(originalUrl, driver, wait);
            if (documentsUrl == null) {
                System.out.println("Не удалось найти URL страницы с документами");

            }

            // 2. Находим URL протокола
            String protocolUrl = urlExtractor.findProtocolUrl(driver, wait);
            if (protocolUrl == null) {
                System.out.println("Не удалось найти URL протокола");

            }

            // 3. Скачиваем документ протокола
            String fileName = "protocol_" + System.currentTimeMillis() + ".docx";
            Path documentPath;
            try {
                documentPath = urlExtractor.downloadDocument(protocolUrl, fileName);
            } catch (IOException e) {
                System.out.println("Ошибка при скачивании документа: " + e.getMessage());

            }

            // 4. Извлекаем информацию о участниках из документа
            Map<String, Map<String, Object>> participantsData = dataExtractor.extractParticipantsFromAllDocuments();
            if (participantsData.isEmpty()) {
                System.out.println("Не удалось извлечь информацию о участниках из документа");

            }
            List<Supplier> suppliers = convertParticipantsToSuppliers(participantsData);

            // 2. Сохраняем всех поставщиков в базу данных
            databaseService.saveSuppliers(suppliers);

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге информации из документа: " + e.getMessage());

        }
    }

    private List<Supplier> convertParticipantsToSuppliers(Map<String, Map<String, Object>> participants) {
        return participants.values().stream()
                .map(participantData -> {
                    Supplier supplier = new Supplier();
                    modelFiller.fillSupplierModelFromParticipantData(supplier, participantData);
                    modelFiller.fillSupplierModelWithBidInfo(supplier, participantData);
                    return supplier;
                })
                .collect(Collectors.toList());
    }
}