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
        try {
            String contractDraftUrl = urlExtractor.findContractDraftUrl(originalUrl, driver, wait);
            String contractInfoUrl = urlExtractor.findContractUrlByRegNumber(originalUrl, driver, wait);
            if (contractDraftUrl == null) {
                System.out.println("Не удалось найти URL контракта");
                return null;
            }

            String currentUrl = driver.getCurrentUrl();
            Set<Cookie> cookies = driver.manage().getCookies();

            try {
                Map<String, Object> contractDetails = new LinkedHashMap<>();
                Map<String, Object> generalData = dataExtractor.parseAndPrintGeneralContractData(contractInfoUrl, driver, wait);
                contractDetails.putAll(generalData);

                if (contractDraftUrl != null) {
                    if (contractDraftUrl.contains("contract-draft.html")) {
                        Map<String, Object> draftData = dataExtractor.parseContractDraft(contractDraftUrl, driver, wait);
                        contractDetails.putAll(draftData);
                    } else if (contractDraftUrl.contains("common-info.html")) {
                        Map<String, Object> commonInfoData = dataExtractor.parseCommonInfoContract(contractDraftUrl, driver, wait);
                        contractDetails.putAll(commonInfoData);
                    } else {
                        System.out.println("Неизвестный тип страницы контракта: " + contractDraftUrl);
                    }
                }

                Contract contract = new Contract();
                Supplier supplier = new Supplier();

                if (contractDraftUrl.contains("contract-draft.html")) {
                    modelFiller.fillContractModel(contract, contractDetails);
                    modelFiller.fillSupplierModel(supplier, contractDetails);
                }
                if (contractDraftUrl.contains("common-info.html")) {
                    modelFiller.fillContractModelFromCommonInfo(contract, contractDetails);
                    modelFiller.fillSupplierModelFromCommonInfo(supplier, contractDetails);
                }

//                modelFiller.fillSupplierModel(supplier, contractDetails);
                contract.setSupplier(supplier);

                return contract;
            } finally {
                // Восстановление состояния браузера
                try {
                    driver.get(currentUrl);
                    cookies.forEach(cookie -> {
                        try {
                            driver.manage().addCookie(cookie);
                        } catch (Exception e) {
                            System.out.println("Ошибка при восстановлении куки: " + e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Ошибка при восстановлении состояния браузера: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге контракта: " + e.getMessage());
            return null;
        }
    }

}
