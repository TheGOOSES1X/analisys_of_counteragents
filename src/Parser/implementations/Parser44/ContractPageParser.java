package Parser.implementations.Parser44;
import Parser.Database.models.*;
import Parser.implementations.Parser44.Contracts.ContractDataExtractor;
import Parser.implementations.Parser44.Contracts.ContractDraftUrlFinder;
import Parser.implementations.Parser44.Contracts.ContractModelFiller;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContractPageParser {
    private final ContractDraftUrlFinder urlExtractor;
    private final ContractDataExtractor dataExtractor;
    private final ContractModelFiller modelFiller;
    public ContractPageParser() {
        this.urlExtractor = new ContractDraftUrlFinder();
        this.dataExtractor = new ContractDataExtractor();
        this.modelFiller = new ContractModelFiller();
    }
    public Contract parseContractInfo(String originalUrl, WebDriver driver, WebDriverWait wait) {
        String originalPage = driver.getCurrentUrl();
        try {
            // Сохраняем текущее состояние

            Set<Cookie> cookies = driver.manage().getCookies();

            try {
                // Основная логика парсинга
                String contractInfoUrl = urlExtractor.findContractUrlByRegNumber(originalUrl, driver, wait);
                if (contractInfoUrl == null) return null;

                Map<String, Object> contractDetails = dataExtractor.parseAndPrintGeneralContractData(contractInfoUrl, driver, wait);
                Contract contract = new Contract();
                Supplier supplier = new Supplier();

                Map<String, Object> suppliersData = dataExtractor.parseSuppliersInfo(contractInfoUrl, driver, wait);
                modelFiller.fillSupplierModel(supplier, suppliersData);
                modelFiller.fillContractModel(contract, contractDetails);
                contract.setSupplier(supplier);

                return contract;
            } finally {
                // Всегда возвращаемся на исходную страницу
                try {
                    driver.get(originalPage);
                    cookies.forEach(cookie -> {
                        try {
                            driver.manage().addCookie(cookie);
                        } catch (Exception e) {
                            System.err.println("Error restoring cookie: " + cookie.getName());
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error restoring original page state: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Contract parsing error: " + e.getMessage());
            return null;
        }
    }

}
