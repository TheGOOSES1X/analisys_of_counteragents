package Parser.utils;

import Parser.implementations.ChromeDriverSetup;
import Parser.implementations.Parser44.LitigationParser;
import Parser.implementations.Parser44.PurchaseParser44;
import Parser.implementations.TextFileResultsSaver;
import Parser.interfaces.DriverSetup;
import Parser.interfaces.PurchaseItem;
import Parser.interfaces.ResultsSaver;
import org.openqa.selenium.WebDriver;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser44Application {

    public static void main(String[] args) {
        List<String> selectedUrls = new ArrayList<>(Arrays.asList(
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18342761",
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18344947",
//"https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18345067"
                    "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0330300051225000044"
//"https://zakupki.gov.ru/epz/order/notice/ezt20/view/common-info.html?regNumber=0372100054625000335"
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0329400001725000037",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000031",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000022"
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=1200700002724000032",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0172200002523000160",
//                "https://zakupki.gov.ru/epz/order/notice/ea20/view/common-info.html?regNumber=0122300017023000012"
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=17984400",
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18049823",
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18015273",
//                "https://zakupki.gov.ru/epz/order/notice/notice223/common-info.html?noticeInfoId=18169394"

        ));

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
        String userAgent = RandomUserAgent.getRandomUserAgent();
        DriverSetup chromeSetup = new ChromeDriverSetup(userAgent);
        PurchaseParser44 parser = new PurchaseParser44(chromeSetup);

        parser.parseUrlsParallel(
                new ArrayList<>(selectedUrls),
                result -> handleParseResult(result),
                4,null
        );
//        parser.parseSupplierStatuses();
//        parser.parseSupplierLitigations();
//        parser.cleanupDownloadDirectory();

    }

    private static void handleParseResult(PurchaseParser44.ParseResult result) {
        SwingUtilities.invokeLater(() -> {
            if (result.error != null) {
                System.err.println("Ошибка при парсинге URL: " + result.url);
                result.error.printStackTrace();

                JOptionPane.showMessageDialog(null,
                        "Ошибка при парсинге: " + result.error.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            } else if (result.purchaseData != null) {
                System.out.println("Успешно распарсено: " + result.purchaseData);
            }
        });
    }
}
