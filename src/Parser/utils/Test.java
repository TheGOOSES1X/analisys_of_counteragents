package Parser.utils;



import Parser.implementations.ChromeDriverSetup;
import Parser.implementations.Parser223.DocumentDraftUrlFinder;
import Parser.implementations.Parser223.ExtractInfoFromDocument;
import Parser.implementations.Parser223.MainInfoParser223;
import Parser.implementations.Parser44.Contracts.ComplaintsURLGetter;
import Parser.implementations.Parser44.Contracts.ContractDataExtractor;
import Parser.implementations.Parser44.Contracts.ContractDraftUrlFinder;

import Parser.implementations.Parser44.LitigationParser;
import Parser.implementations.Parser44.PurchaseParser44;
import Parser.implementations.TextFileResultsSaver;
import Parser.interfaces.DriverSetup;
import Parser.interfaces.PurchaseItem;
import Parser.interfaces.ResultsSaver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
//        String url1 = "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18049823";
//        String url2 = "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18015273";
//
        String userAgent = RandomUserAgent.getRandomUserAgent();
        DriverSetup chromeSetup = new ChromeDriverSetup(userAgent);
        WebDriver  driver = chromeSetup.setupDriver();
        DocumentDraftUrlFinder finder = new DocumentDraftUrlFinder();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//        ContractDataExtractor extractor = new ContractDataExtractor();
//        extractor.parseAndPrintGeneralContractData("https://zakupki.gov.ru/epz/contract/contractCard/common-info.html?reestrNumber=4780551456224000016",
//                driver,wait);
        MainInfoParser223 purchaseParser223 = new MainInfoParser223();
        purchaseParser223.parsePurchasePage("https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18345067",driver,wait);


//        ComplaintsURLGetter getter = new ComplaintsURLGetter();
//        int result = getter.countComplaintsByRegNumber( "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000022",
//                driver,wait);


//        System.out.println(" поиске жалобы: " + result);
//        try {
//            // 3. Тестирование первого URL
//            testUrl(finder, driver, wait, url1);
//
//            // 4. Тестирование второго URL
//            testUrl(finder, driver, wait, url2);
//
//        } finally {
//            // 5. Закрытие драйвера
//            driver.quit();
//        }

//        ExtractInfoFromDocument extractor = new ExtractInfoFromDocument();
//        Map<String, Map<String, Object>> participantsData = extractor.extractParticipantsFromAllDocuments();
//        printParticipantsData(participantsData);




// Вывод результатов

    }

    private static void printParticipantsData(Map<String, Map<String, Object>> participantsData) {
        if (participantsData.isEmpty()) {
            System.out.println("Не найдено данных об участниках.");
            return;
        }

        System.out.println("=== Найдено участников: " + participantsData.size() + " ===");
        System.out.println();

        for (Map.Entry<String, Map<String, Object>> entry : participantsData.entrySet()) {
            String inn = entry.getKey();
            Map<String, Object> participant = entry.getValue();

            System.out.println("Участник (ИНН: " + inn + "):");

            // Вывод всех полей участника
            for (Map.Entry<String, Object> field : participant.entrySet()) {
                System.out.printf("  %-25s: %s%n", field.getKey(), field.getValue());
            }

            System.out.println("----------------------------------------");
        }
    }

    private static void testUrl(DocumentDraftUrlFinder finder, WebDriver driver, WebDriverWait wait, String url) {
        System.out.println("\nТестирование URL: " + url);

        try {
            // Шаг 1: Получаем URL документов
            String documentsUrl = finder.findContractDraftUrl(url, driver, wait);
            System.out.println("URL документов: " + documentsUrl);

            if (documentsUrl != null) {
                // Шаг 2: Ищем протокол
                String protocolUrl = finder.findProtocolUrl(driver, wait);
                System.out.println("URL протокола: " + protocolUrl);

                if (protocolUrl != null) {
                    // Шаг 3: Скачиваем документ
                    String noticeId = url.substring(url.lastIndexOf('=') + 1); // Получаем ID после '='
                    String fileName = "Протокол_" + noticeId + ".docx";
                    try {
                        Path downloadedFile = finder.downloadDocument(protocolUrl, fileName);
                        System.out.println("Файл сохранен: " + downloadedFile.toAbsolutePath());
                    } catch (IOException e) {
                        System.out.println("Ошибка при скачивании: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при тестировании: " + e.getMessage());
            e.printStackTrace();
        }
    }

//        List<String> selectedUrls = new ArrayList<>(Arrays.asList(
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000031",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000022",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000032",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0172200002523000160",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0122300017023000012"
//
//        ));

//        try {
//        HibernateUtil.initialize("src/config.json");
//        if (HibernateUtil.testConnection()) {
//            System.out.println("Database connection is working");
//        }
//            HibernateUtil.printDatabaseSchema();
//            HibernateUtil.initializeTestData();
//        } catch (Exception e) {
//            System.err.println("Database connection failed: " + e.getMessage());
//            System.exit(1);
//        }
//        ResultsSaver<PurchaseItem> saver = new TextFileResultsSaver();
//        String userAgent = RandomUserAgent.getRandomUserAgent();
//        DriverSetup chromeSetup = new ChromeDriverSetup(userAgent);
//        PurchaseParser44 parser = new PurchaseParser44(chromeSetup);
//
//        parser.parseUrlsParallel(
//                new ArrayList<>(selectedUrls),
//                result -> handleParseResult(result),
//                3,null
//        );
//        parser.parseSupplierStatuses();
//        parser.parseSupplierLitigations();





    }
//
//    private static void handleParseResult(PurchaseParser44.ParseResult result) {
//        SwingUtilities.invokeLater(() -> {
//            if (result.error != null) {
//                System.err.println("Ошибка при парсинге URL: " + result.url);
//                result.error.printStackTrace();
//
//                JOptionPane.showMessageDialog(null,
//                        "Ошибка при парсинге: " + result.error.getMessage(),
//                        "Ошибка",
//                        JOptionPane.ERROR_MESSAGE);
//            } else if (result.purchaseData != null) {
//                System.out.println("Успешно распарсено: " + result.purchaseData);
//            }
//        });
//    }

