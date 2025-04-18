package Parser.utils;

import Parser.Database.hooks.HibernateUtil;
import Parser.implementations.ChromeDriverSetup;
import Parser.implementations.PurchaseParser44;
import Parser.implementations.PurchasesParserHead;
import Parser.implementations.TextFileResultsSaver;
import Parser.interfaces.DriverSetup;
import Parser.interfaces.Parser;
import Parser.interfaces.PurchaseItem;
import Parser.interfaces.ResultsSaver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser44Application {

    public static void main(String[] args) {
        List<String> selectedUrls = new ArrayList<>(Arrays.asList(
                "https://zakupki.gov.ru/epz/order/notice/ok20/view/common-info.html?regNumber=1200700002725000001"
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
        ResultsSaver<PurchaseItem> saver = new TextFileResultsSaver();
        DriverSetup chromeSetup = new ChromeDriverSetup();
        PurchaseParser44 parser = new PurchaseParser44(chromeSetup);
        parser.parseUrlsParallel(
                new ArrayList<>(selectedUrls),
                result -> handleParseResult(result),
                3
        );
        parser.parse();




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
