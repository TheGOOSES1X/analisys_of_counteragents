package Parser.utils;


import Parser.implementations.ChromeDriverSetup;
import Parser.implementations.PurchasesParserHead;
import Parser.implementations.TextFileResultsSaver;
import Parser.interfaces.DriverSetup;
import Parser.interfaces.Parser;
import Parser.interfaces.PurchaseItem;
import Parser.interfaces.ResultsSaver;

public class ParserApplication {
    public static void main(String[] args) {
        // Простая версия парсера
        ResultsSaver<PurchaseItem> saver = new TextFileResultsSaver();

//        DriverSetup chromeSetup = new ChromeDriverSetup();
//        Parser parser = new PurchasesParserHead(chromeSetup, saver);
//        Parser parser = new PurchasesParser(chromeSetup);
//        parser.parse();


    }
}