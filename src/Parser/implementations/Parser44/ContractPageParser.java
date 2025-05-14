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
                Map<String, Object> suppliersData = dataExtractor.parseSuppliersInfo(contractInfoUrl,driver, wait);
                modelFiller.fillSupplierModel(supplier, suppliersData);
                if (contractDraftUrl.contains("contract-draft.html")) {
                    modelFiller.fillContractModel(contract, contractDetails,suppliersData);
//                    modelFiller.fillSupplierModel(supplier, contractDetails);
                }
                if (contractDraftUrl.contains("common-info.html")) {
                    modelFiller.fillContractModelFromCommonInfo(contract, contractDetails,suppliersData);
//                    modelFiller.fillSupplierModelFromCommonInfo(supplier, contractDetails);
                }

//                fillSupplierModelFromParticipantData(supplier, suppliersData);

                // 2. Сохраняем всех поставщиков в базу данных
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

    public void fillSupplierModelFromParticipantData(Supplier supplier, Map<String, Object> participantData) {
        try {
            // Основная информация
            supplier.setName(getStringValue(participantData, "Организация"));
            supplier.setAddress(getStringValue(participantData, "Адрес места нахождения"));
            supplier.setPostalAddress(getStringValue(participantData, "Почтовый адрес"));

            // Контактная информация
            supplier.setPhone(getStringValue(participantData, "Телефон"));
            supplier.setEmail(getStringValue(participantData, "Email"));

            // Реквизиты
            supplier.setInn(getStringValue(participantData, "ИНН"));

            // Страна (используем значения из данных или по умолчанию для России)
            supplier.setCountryName(getStringValue(participantData, "Страна", "Российская Федерация"));
            supplier.setCountryCode(getStringValue(participantData, "Код страны", "643"));

            // Определяем тип поставщика (юр. лицо или ИП)
            String name = supplier.getName();
            if (name != null && (name.contains("Индивидуальный предприниматель") || name.contains("ИП"))) {
                supplier.setType("Индивидуальный предприниматель");
            } else {
                supplier.setType("Юридическое лицо");
            }

            // Статус по умолчанию
            supplier.setStatus("Активен");

            // Обработка почтового индекса из адреса
            processPostalCode(supplier);

        } catch (Exception e) {
            System.err.println("Ошибка при заполнении модели поставщика: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Вспомогательные методы
    private String getStringValue(Map<String, Object> map, String key) {
        return map.containsKey(key) ? map.get(key).toString() : null;
    }

    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        return map.containsKey(key) ? map.get(key).toString() : defaultValue;
    }

    private void processPostalCode(Supplier supplier) {
        if (supplier.getPostalAddress() == null || supplier.getPostalAddress().isEmpty()) {
            String address = supplier.getAddress();
            if (address != null && address.matches("^\\d{6}.*")) {
                supplier.setPostalAddress(address.substring(0, 6));
            }
        }
    }
}
