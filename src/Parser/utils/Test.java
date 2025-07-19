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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

public class Test {

    public static void main(String[] args) {
//        String url1 = "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18049823";
//        String url2 = "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18015273";
//
//        String userAgent = RandomUserAgent.getRandomUserAgent();
//        DriverSetup chromeSetup = new ChromeDriverSetup(userAgent);
//        WebDriver  driver = chromeSetup.setupDriver();
//        DocumentDraftUrlFinder finder = new DocumentDraftUrlFinder();
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//        ContractDataExtractor extractor = new ContractDataExtractor();
//        extractor.parseAndPrintGeneralContractData("https://zakupki.gov.ru/epz/contract/contractCard/common-info.html?reestrNumber=4780551456224000016",
//                driver,wait);
//        MainInfoParser223 purchaseParser223 = new MainInfoParser223();
//        purchaseParser223.parsePurchasePage("https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18345067",driver,wait);


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
//        String filePath = "All_links.txt";
//        try {
//            // Читаем все строки из файлаs
//            List<String> lines = Files.readAllLines(Paths.get(filePath));
//
//            // Создаем Set для хранения уникальных ссылок
//            Set<String> uniqueLinks = new HashSet<>();
//
//            // Добавляем все ссылки в Set (дубликаты будут автоматически игнорироваться)
//            uniqueLinks.addAll(lines);
//
//            // Выводим результаты
//            System.out.println("Всего ссылок в файле: " + lines.size());
//            System.out.println("Уникальных ссылок: " + uniqueLinks.size());
//
//            if (lines.size() > uniqueLinks.size()) {
//                System.out.println("Найдены дубликаты! Количество дубликатов: " +
//                        (lines.size() - uniqueLinks.size()));
//
//                // Если нужно вывести дубликаты
//                findAndPrintDuplicates(lines);
//            } else {
//                System.out.println("Дубликатов не найдено.");
//            }
//
//        } catch (IOException e) {
//            System.err.println("Ошибка при чтении файла: " + e.getMessage());
//        }
//        String outputFilePath = "unique_links.txt";
//        try {
//            // Читаем все строки из файла
//            List<String> allLinks = Files.readAllLines(Paths.get(filePath));
//
//            // Используем Set для автоматического удаления дубликатов
//            Set<String> uniqueLinks = new HashSet<>(allLinks);
//
//            // Сохраняем уникальные ссылки в новый файл
//            Files.write(Path.of(outputFilePath), uniqueLinks);
//
//            // Выводим отчет
//            System.out.println("✅ Готово!");
//            System.out.println("Всего ссылок в исходном файле: " + allLinks.size());
//            System.out.println("Уникальных ссылок (без дубликатов): " + uniqueLinks.size());
//            System.out.println("Сохранено в файл: " + outputFilePath);
//
//        } catch (IOException e) {
//            System.err.println("❌ Ошибка: " + e.getMessage());
//        }
        String raw =

                       "№0352100012725000033\n" +
                               "№0352100012725000035\n" +
                               "№0301300247625000589\n" +
                               "№0357300040425000001\n" +
                               "№0869200000225006221\n" +
                               "№0318300165725000399\n" +
                               "№0301100000425000048\n" +
                               "№0174500001125003857\n" +
                               "№0358200011325000018\n" +
                               "№0369100038425000060\n" +
                               "№0190200000325008370\n" +
                               "№0834500000125000010\n" +
                               "№0324100004425000103\n" +
                               "№0372200141825000044\n" +
                               "№0320100018725000190\n" +
                               "№0369300199325000012\n" +
                               "№0362300376325000008\n" +
                               "№0318200067725000001\n" +
                               "№0387200011125000021\n" +
                               "№0145200000425001096\n" +
                               "№0111200000125000014\n" +
                               "№0817600001225000002\n" +
                               "№0131200001025005185\n" +
                               "№0301300247625000533\n" +
                               "№0321100017225000063\n" +
                               "№0307300005225000356\n" +
                               "№0123300004625000081\n" +
                               "№0119300000125000240\n" +
                               "№0356300207625000004\n" +
                               "№0151100008425000025\n" +
                               "№0335300053925000042\n" +
                               "№0131200001025005012\n" +
                               "№0848300057125000063\n" +
                               "№0138200003725000024\n" +
                               "№0301100000425000045\n" +
                               "№0339300289625000030\n" +
                               "№0112200000825003180\n" +
                               "№0190200000325007779\n" +
                               "№0372200141825000040\n" +
                               "№0372200141825000038\n" +
                               "№0372200141825000039\n" +
                               "№0372200141825000042\n" +
                               "№0351300298325000146\n" +
                               "№0169300028125000166\n" +
                               "№0372200271025000061\n" +
                               "№0372200141825000036\n" +
                               "№0372200271025000062\n" +
                               "№0372500009325000025\n" +
                               "№0119200000125011763\n" +
                               "№0815500000525008196\n" +
                               "№0372200078925000054\n" +
                               "№0123300004625000078\n" +
                               "№0142300006225000081\n" +
                               "№0372200084225000024\n" +
                               "№0162300005325001153\n" +
                               "№0372200084225000023\n" +
                               "№0372500009325000020\n" +
                               "№0122300013825000047\n" +
                               "№0119200000125011262\n" +
                               "№0348200077025000023\n" +
                               "№0375400000925000049\n" +
                               "№0387200011125000020\n" +
                               "№0166300024725000424\n" +
                               "№0318300165725000286\n" +
                               "№0306100005925000013\n" +
                               "№0372200084225000018\n" +
                               "№0372100027325000224\n" +
                               "№0333200014025000033\n" +
                               "№0131200001025004235\n" +
                               "№0838200000225000010\n" +
                               "№0163600007425000012\n" +
                               "№0372500009325000018\n" +
                               "№0818500000825003851\n" +
                               "№0816500000625009084\n" +
                               "№0816500000625009115\n" +
                               "№0190300000725000419\n" +
                               "№0387200015025000012\n" +
                               "№0332100025625000014\n" +
                               "№0151100008425000024\n" +
                               "№0123200000325001105\n" +
                               "№0120100008425000038\n" +
                               "№0120100008425000039\n" +
                               "№0338100004625000034\n" +
                               "№0319300008025000007\n" +
                               "№0184200000625000685\n" +
                               "№0152300044225000021\n" +
                               "№0375400000925000042\n" +
                               "№0122300013825000042\n" +
                               "№0142300006225000072\n" +
                               "№0137200001225003242\n" +
                               "№0818300019925000161\n" +
                               "№0320100018325000009\n" +
                               "№0145200000425000756\n" +
                               "№0372200141825000026\n" +
                               "№0346300130525000009\n" +
                               "№0372200141825000025\n" +
                               "№0187300006525000777\n" +
                               "№0332100025625000013\n" +
                               "№0372100054625000371\n" +
                               "№0373400009625000150\n" +
                               "№0346300130525000007\n" +
                               "№0346300130525000006\n" +
                               "№0346300130525000008\n" +
                               "№0319300325525000249\n" +
                               "№0356500001425004169\n" +
                               "№0335100016125000118\n" +
                               "№0334100004725000002\n" +
                               "№0107300010225000041\n" +
                               "№0173100005025000013\n" +
                               "№0351300298325000110\n" +
                               "№0329400001725000037\n" +
                               "№0372200141825000023\n" +
                               "№0119200000125009364\n" +
                               "№0164300016525000428\n" +
                               "№0372200141825000020\n" +
                               "№0372100027325000135\n" +
                               "№0333400000325000026\n" +
                               "№0190200000325006095\n" +
                               "№0151100008425000019\n" +
                               "№0123300004625000062\n" +
                               "№0372200084225000012\n" +
                               "№0375400000925000037\n" +
                               "№0142300006225000066\n" +
                               "№0348200085625000008\n" +
                               "№0372100054625000335\n" +
                               "№0845500001025000042\n" +
                               "№0112200000825002420\n" +
                               "№0153100006325000026\n" +
                               "№0387200010725000002\n" +
                               "№0187300006525000647\n" +
                               "№0372200236125000037\n" +
                               "№0136500001125002947\n" +
                               "№0145200000425000608\n" +
                               "№0354200017425000003\n" +
                               "№0848300058125000126\n" +
                               "№0329100005925000090\n" +
                               "№0169300008225000266\n" +
                               "№0372200236125000031\n" +
                               "№0320100003925000004\n" +
                               "№0375100001225000253\n" +
                               "№0373400009625000118\n" +
                               "№0348200077025000010\n" +
                               "№0372500009325000008\n" +
                               "№0335100016125000092\n" +
                               "№0372500009325000010\n" +
                               "№0329200062225003939\n" +
                               "№0123200000325000861\n" +
                               "№0329200062225003889\n" +
                               "№0348200077025000009\n" +
                               "№0322100014325000007\n" +
                               "№0351400002125000056\n" +
                               "№0301300247625000338\n" +
                               "№0332200039325000001\n" +
                               "№0338100004825000016\n" +
                               "№0118300013325000668\n" +
                               "№0173100005025000009\n" +
                               "№0818500000825002876\n" +
                               "№0387200011125000011\n" +
                               "№0112200000825002195\n" +
                               "№0358200011325000008\n" +
                               "№0358200011325000007\n" +
                               "№0358200011325000006\n" +
                               "№0848300047225000349\n" +
                               "№0372100054725000035\n" +
                               "№0356500001425003294\n" +
                               "№0330300051225000044\n" +
                               "№0145200000425000439\n" +
                               "№0318200048525000017\n" +
                               "№0338100004825000014\n" +
                               "№0354200017425000001\n" +
                               "№0342300194425000014\n" +
                               "№0166300024725000297\n" +
                               "№0859200001125004111\n" +
                               "№0372100054325000019\n" +
                               "№0140300024825000010\n" +
                               "№0348100026525000110\n" +
                               "№0358200005425000001\n" +
                               "№0348100008825000022\n" +
                               "№0372100053425000004\n" +
                               "№0319100001325000037\n" +
                               "№0131200001025002707\n" +
                               "№0869200000225003335\n" +
                               "№0372100027325000084\n" +
                               "№0136500001125002285\n" +
                               "№0119200000125006292\n" +
                               "№0818500000825002462\n" +
                               "№0301200077625000032\n" +
                               "№0342400001725000002\n" +
                               "№0319300325525000165\n" +
                               "№0187500000425000065\n" +
                               "№0860200000825002601\n" +
                               "№0358200011325000005\n" +
                               "№0134300044425000014\n" +
                               "№0187500000425000064\n" +
                               "№0342300194425000013\n" +
                               "№0375200056725000018\n" +
                               "№0373400006725000044\n" +
                               "№0816600003725000019\n" +
                               "№0163200000325001831\n" +
                               "№0130300016825000003\n" +
                               "№0131200001025002520\n" +
                               "№0372200141825000012\n" +
                               "№0131200001025002492\n" +
                               "№0815500000525004542\n" +
                               "№0888500000225000136\n" +
                               "№0860200000825002413\n" +
                               "№0372200045925000005\n" +
                               "№0130600040525000061\n" +
                               "№0348200080425000058\n" +
                               "№0849400000225000067\n" +
                               "№0187300006525000385\n" +
                               "№0358100009325000006\n" +
                               "№0373400009625000057\n" +
                               "№0372200243825000007\n" +
                               "№0130600040525000046\n" +
                               "№0320100011225000138\n" +
                               "№0153300007625000035\n" +
                               "№0318300119425000468\n" +
                               "№0112200000825001574\n" +
                               "№0325100011425000005\n" +
                               "№0848300049025000250\n" +
                               "№0358100002225000040\n" +
                               "№0360300052825000349\n" +
                               "№0346300130525000005\n" +
                               "№0358200054225000004\n" +
                               "№0358200054225000005\n" +
                               "№0301200077625000030\n" +
                               "№0372100006525000003\n" +
                               "№0358200046825000017\n" +
                               "№0361300021425000007\n" +
                               "№0325100002425000004\n" +
                               "№0372200141825000011\n" +
                               "№0325100002425000006\n" +
                               "№0171200001925000877\n" +
                               "№0373100089325000018\n" +
                               "№0358200054225000003\n" +
                               "№0301100021425000011\n" +
                               "№0372100027325000037\n" +
                               "№0815500000525003398\n" +
                               "№0818500000825001773\n" +
                               "№0387200021325000007\n" +
                               "№0348100011225000015\n" +
                               "№0108500000425001065\n" +
                               "№0320100011025000021\n" +
                               "№0865200000325000224\n" +
                               "№0306100001225000004\n" +
                               "№0346300130525000004\n" +
                               "№0318200030225000002\n" +
                               "№0171200001925000722\n" +
                               "№0346300130525000003\n" +
                               "№0373400006725000026\n" +
                               "№0318200030225000001\n" +
                               "№0891200000625002178\n" +
                               "№0108500000425000970\n" +
                               "№0860200000825001495\n" +
                               "№0848600002725000108\n" +
                               "№0162300015625000001\n" +
                               "№0848600002725000109\n" +
                               "№0848600002725000101\n" +
                               "№0163300029425000230\n" +
                               "№0848600002725000100\n" +
                               "№0137200001225001431\n" +
                               "№0849400000225000048\n" +
                               "№0358300445725000011\n" +
                               "№0108500000425000888\n" +
                               "№0849400000225000042\n" +
                               "№0364300108425000011\n" +
                               "№0339300289625000009\n" +
                               "№0339300289625000010\n" +
                               "№0358200054425000004\n" +
                               "№0318100060325000004\n" +
                               "№0860200000825001084\n" +
                               "№0169300000325000410\n" +
                               "№0319300325525000115\n" +
                               "№0860200000825001048\n" +
                               "№0342300062525000001\n" +
                               "№0169300000325000385\n" +
                               "№0373100089325000006\n" +
                               "№0358200054225000002\n" +
                               "№0373400009625000015\n" +
                               "№0358200054425000003\n" +
                               "№0358200020425000005\n" +
                               "№0319100038625000015\n" +
                               "№0358300445725000010\n" +
                               "№0832200006625000139\n" +
                               "№0358200054225000001\n" +
                               "№0816500000625002427\n" +
                               "№0346300130525000002\n" +
                               "№0353300009925000013\n" +
                               "№0860200000825000640\n" +
                               "№0358200020425000001\n" +
                               "№0375400000925000014\n" +
                               "№0112200000825000736\n" +
                               "№0190200000325001423\n" +
                               "№0380400000225000018\n" +
                               "№0346300130525000001\n" +
                               "№0358200054425000002\n" +
                               "№0301200077625000010\n" +
                               "№0319300325525000089\n" +
                               "№0358200011325000003\n" +
                               "№0358200054425000001\n" +
                               "№0358200011325000002\n" +
                               "№0124200000625000279\n" +
                               "№0324400000825000003\n" +
                               "№0162300009425000008\n" +
                               "№0333400000325000005\n" +
                               "№0322400004025000005\n" +
                               "№0860200000825000126\n" +
                               "№0324100004125000001\n" +
                               "№0319300003425000027\n" +
                               "№0372200271025000007\n" +
                               "№0108500000425000045\n" +
                               "№0832200006625000006\n" +
                               "№0380400000225000003\n"
                ;

//        String[] lines = raw.split("\\n");
//
//        String baseUrl = "https://zakupki.gov.ru/epz/order/notice/zk20/view/common-info.html?regNumber=";
//
//        for (String line : lines) {
//            // Удаляем лишние символы: кавычки и №
//            String number = line.replace("№", "").replace("\"", "").trim();
//            if (!number.isEmpty()) {
//                System.out.println("\"" + baseUrl + number + "\",");
//            }
//        }





        String[] lines = raw.split("\\n");

        for (int i = 0; i < lines.length; i++) {
            // Удаляем символ № и лишние пробелы
            String number = lines[i].replace("№", "").trim();
            // Выводим номер в кавычках
            System.out.print("\"" + number + "\"");
            // Добавляем запятую, если это не последний элемент
            if (i < lines.length - 1) {
                System.out.print(", ");
            }
        }


// Вывод результатов

    }
    private static void findAndPrintDuplicates(List<String> lines) {
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();

        for (String link : lines) {
            if (!seen.add(link)) {
                duplicates.add(link);
            }
        }

        if (!duplicates.isEmpty()) {
            System.out.println("\nСписок дублирующихся ссылок:");
            duplicates.forEach(System.out::println);
        }
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

