package Parser.implementations;

import Parser.implementations.Parser44.PurchaseParser44;
import Parser.interfaces.*;
import Parser.utils.Okpd2Converter;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
public class PurchasesParserHead implements PurchaseListParser {
    public volatile boolean isPaused = false;
    private volatile boolean isStopped = false;
    private final DriverSetup driverSetup;
    private final ResultsSaver<PurchaseItem> resultsSaver;
    private static final String BASE_URL = "https://zakupki.gov.ru/epz/order/extendedsearch/results.html";

    private static final int ITEMS_PER_PAGE = 50;
    private static final String RECORDS_PER_PAGE_VALUE = String.valueOf(ITEMS_PER_PAGE);
    private static final int DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_PAGINATION_DELAY_MS = 3000;
    private final ParserStatusListener statusListener;
    private final Map<String, String> queryParams;

    @Override
    public void pauseParser() {
        isPaused = true;
    }
    @Override
    public void resumeParser() {
        isPaused = false;
    }


    @Override
    public void stopParser() {
        isStopped = true;
    }

    private void checkPaused() {
        while (isPaused && !isStopped) {
            try {
                Thread.sleep(500); // Проверяем каждые 500 мс
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
//    public PurchasesParserHead(DriverSetup driverSetup) {
//        this(driverSetup, null);
//    }


//    public PurchasesParserHead(DriverSetup driverSetup, ResultsSaver<PurchaseItem> resultsSaver) {
//        this(driverSetup, resultsSaver,
//                "АКЦИОНЕРНОЕ+ОБЩЕСТВО+%22ОНЕЖСКИЙ+СУДОСТРОИТЕЛЬНО-СУДОРЕМОНТНЫЙ+ЗАВОД%22", null);
//    }

    public PurchasesParserHead(DriverSetup driverSetup, ResultsSaver<PurchaseItem> resultsSaver,
                               String searchQuery, String okpd2Code, ParserStatusListener statusListener) {
        this(driverSetup, resultsSaver, createDefaultParams(searchQuery, okpd2Code), statusListener);
    }

    public PurchasesParserHead(DriverSetup driverSetup, ResultsSaver<PurchaseItem> resultsSaver,
                               Map<String, String> queryParams, ParserStatusListener statusListener) {
        this.driverSetup = driverSetup;
        this.resultsSaver = resultsSaver;
        this.queryParams = new LinkedHashMap<>(queryParams);

        this.queryParams.put("recordsPerPage", RECORDS_PER_PAGE_VALUE);
        this.statusListener = statusListener;
    }
    private static Map<String, String> createDefaultParams(String searchQuery, String okpd2Code) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("searchString", searchQuery);
        params.put("morphology", "on");
        params.put("search-filter", "Дате размещения");
        params.put("pageNumber", "1");
        params.put("sortDirection", "false");
        params.put("recordsPerPage", RECORDS_PER_PAGE_VALUE);
        params.put("showLotsInfoHidden", "false");
        params.put("sortBy", "UPDATE_DATE");
        params.put("fz44", "on");
        params.put("fz223", "on");
        params.put("af", "on");
        params.put("ca", "on");
        params.put("pc", "on");
        params.put("pa", "on");

        // Установка валюты (1 - рубли)
        params.put("currencyIdGeneral", "1");

        // Удаляем фиктивные даты или устанавливаем реальные
        // params.remove("publishDateFrom");
        // params.remove("publishDateTo");

        // Добавляем параметры цены (пример значений)
        params.put("priceFromGeneral", "0");
        params.put("priceToGeneral", "100000000");

        if (okpd2Code != null && !okpd2Code.isEmpty()) {
            String okpd2Id = Okpd2Converter.getOkpd2Id(okpd2Code);
            if (okpd2Id != null) {
                params.put("okpd2Ids", okpd2Id);
                params.put("okpd2IdsCodes", okpd2Code);
            }
        }
        return params;
    }




    @Override
    public void parse() {
        WebDriver driver = driverSetup.setupDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS));

        try {
            Set<PurchaseItem> allPurchases = parseAllPages(driver, wait);
            saveResults(allPurchases);
            notifyCompletion(allPurchases.size());
        } catch (Exception e) {
            handleError("Ошибка при парсинге: " + e.getMessage(), e);
        } finally {
            closeDriver(driver);
        }
    }


    private Set<PurchaseItem> parseAllPages(WebDriver driver, WebDriverWait wait) {
        Set<PurchaseItem> uniquePurchases = new LinkedHashSet<>();
        int currentPage = 1;
        int totalItems = 0;
        boolean hasNextPage = true;

        while (hasNextPage && !isStopped) {
            checkPaused();
            navigateToPage(driver, wait, currentPage);

            if (currentPage == 1) {
                totalItems = getTotalItemsCount(driver);
                if (totalItems == 0) break;
            }

            List<WebElement> itemContainers = getItemContainers(driver);
            if (itemContainers.isEmpty()) {
                hasNextPage = false;
                continue;
            }
            for (WebElement itemContainer : itemContainers) {
                PurchaseItem item = extractPurchaseData(itemContainer);
                if (!uniquePurchases.add(item)) { // <- Автоматическая проверка дубликатов!
                    System.out.println("Дубликат: " + item.getUrl());
                }
            }
            processPageItems(itemContainers, uniquePurchases,totalItems);
            hasNextPage = shouldContinueToNextPage(currentPage, totalItems);
            currentPage++;
        }
        if (currentPage % 50 == 0) {
            driver.manage().deleteAllCookies();
        }

        return uniquePurchases;
    }

    private boolean navigateToPage(WebDriver driver, WebDriverWait wait, int pageNumber) {
        try {
            driver.get(buildPaginatedUrl(pageNumber));
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".registry-entry__header-mid__number")));
            return true; // Успешно загрузилось
        } catch (TimeoutException e) {
            System.err.println("Элемент не найден на странице " + pageNumber + ". Пропускаем...");
            return false; // Не удалось загрузить
        }
    }

    private int getTotalItemsCount(WebDriver driver) {
        int totalItems = extractTotalItems(driver);
        if (statusListener != null) {
            statusListener.updateTotalRecords(totalItems);
            statusListener.updateStatus("Найдено записей: " + totalItems);
        }
        return totalItems;
    }

    private List<WebElement> getItemContainers(WebDriver driver) {
        return driver.findElements(By.cssSelector(".search-registry-entry-block"));
    }

    private void processPageItems(List<WebElement> itemContainers, Set<PurchaseItem> allPurchases, int totalItems) {
        for (WebElement itemContainer : itemContainers) {
            if (isStopped) break; // Проверка на остановку

            try {
                PurchaseItem item = extractPurchaseData(itemContainer);
                allPurchases.add(item);
                notifyItemProcessed(allPurchases.size(), item, totalItems); // Передаем totalItems
            } catch (Exception e) {
                handleItemError("Ошибка при обработке элемента: " + e.getMessage(), e);
            }
        }
        sleep(DEFAULT_PAGINATION_DELAY_MS);
    }

    private boolean shouldContinueToNextPage(int currentPage, int totalItems) {
        return currentPage * ITEMS_PER_PAGE < totalItems;
    }

    private void notifyItemProcessed(int processedCount, PurchaseItem item, int totalItems) {
        if (statusListener != null) {
            statusListener.updateCurrentRecords(processedCount);
            statusListener.addPurchaseToTable(item,totalItems);
        }
    }

    private void saveResults(Set<PurchaseItem> items) {
        if (resultsSaver != null) {
            resultsSaver.save(items);
        }
    }

    private void notifyCompletion(int totalProcessed) {
        if (statusListener != null) {
            statusListener.updateStatus("Парсинг завершен! Обработано: " + totalProcessed);
        }
    }

    private void handleError(String message, Exception e) {
        if (statusListener != null) {
            statusListener.updateStatus(message);
        }
        System.err.println(message);
        e.printStackTrace();
    }

    private void handleItemError(String message, Exception e) {
        if (statusListener != null) {
            statusListener.updateStatus(message);
        }
        System.err.println(message);
    }

    private void closeDriver(WebDriver driver) {
        if (driver != null) {
            try {
                sleep(1000);
                driver.quit(); // Корректное закрытие драйвера
            } catch (Exception e) {
                handleError("Ошибка при закрытии драйвера: " + e.getMessage(), e);
            } finally {
                killChromeProcesses(); // Принудительное завершение процессов
            }
        }
    }

    private PurchaseItem extractPurchaseData(WebElement itemContainer) {
        String number = extractTextFromElement(itemContainer,
                ".registry-entry__header-mid__number a", "Не указан");
        String href = extractAttributeFromElement(itemContainer,
                ".registry-entry__header-mid__number a", "href", "");
        String purchaseObject = extractPurchaseObject(itemContainer);
        String customer = extractCustomer(itemContainer);

        return new PurchaseItemImpl(number, href, purchaseObject, customer);
    }

    private String extractPurchaseObject(WebElement itemContainer) {
        return extractTextFromXPath(itemContainer,
                ".//div[contains(@class, 'registry-entry__body-block')]" +
                        "[.//div[contains(@class, 'registry-entry__body-title')]" +
                        "[contains(., 'Объект закупки')]]" +
                        "/div[contains(@class, 'registry-entry__body-value')]",
                "Не указан");
    }

    private String extractCustomer(WebElement itemContainer) {
        // Пробуем найти заказчика через ссылку (44-ФЗ)
        String customer = extractTextFromXPath(itemContainer,
                ".//div[contains(@class, 'registry-entry__body-block')]" +
                        "[.//div[contains(@class, 'registry-entry__body-title')]" +
                        "[contains(., 'Заказчик')]]" +
                        "//a[contains(@href, '/epz/organization/view/') or contains(@href, 'organizationId=')]",
                null);

        // Если не нашли через ссылку, пробуем через текст (223-ФЗ)
        if (customer == null) {
            customer = extractTextFromXPath(itemContainer,
                    ".//div[contains(@class, 'registry-entry__body-block')]" +
                            "[.//div[contains(@class, 'registry-entry__body-title')]" +
                            "[contains(., 'Заказчик')]]" +
                            "//div[contains(@class, 'registry-entry__body-value') or contains(@class, 'registry-entry__body-href')]",
                    "Не указан");
        }

        return customer != null ? customer.trim() : "Не указан";
    }

    private String extractTextFromElement(WebElement parent, String cssSelector, String defaultValue) {
        try {
            return parent.findElement(By.cssSelector(cssSelector)).getText().trim();
        } catch (Exception e) {
            System.err.println("Не удалось извлечь текст из элемента: " + e.getMessage());
            return defaultValue;
        }
    }

    private String extractAttributeFromElement(WebElement parent, String cssSelector,
                                               String attribute, String defaultValue) {
        try {
            return parent.findElement(By.cssSelector(cssSelector)).getAttribute(attribute);
        } catch (Exception e) {
            System.err.println("Не удалось извлечь атрибут из элемента: " + e.getMessage());
            return defaultValue;
        }
    }

    private String extractTextFromXPath(WebElement parent, String xpath, String defaultValue) {
        try {
            List<WebElement> elements = parent.findElements(By.xpath(xpath));
            if (elements.isEmpty()) {
                return defaultValue;
            }
            return elements.get(0).getText().trim();
        } catch (Exception e) {
            System.err.println("Ошибка при извлечении текста по XPath: " + xpath);
            return defaultValue;
        }
    }

    private String buildPaginatedUrl(int pageNumber) {
        Map<String, String> params = new LinkedHashMap<>(this.queryParams);
        params.put("pageNumber", String.valueOf(pageNumber));

        return BASE_URL + "?" + params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    private int extractTotalItems(WebDriver driver) {
        try {
            // Используем getAttribute("textContent") вместо getText()
            String totalText = driver.findElement(By.cssSelector(".search-results__total"))
                    .getAttribute("textContent")
                    .trim();

            // Удаляем всё, кроме цифр
            String cleanText = totalText.replaceAll("\\D+", "");

            return cleanText.isEmpty() ? 0 : Integer.parseInt(cleanText);
        } catch (Exception e) {
            System.err.println("Ошибка при получении количества записей: " + e.getMessage());
            return 0;
        }
    }
    private void killChromeProcesses() {
        try {
            // Для Windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe /T");
                Runtime.getRuntime().exec("taskkill /F /IM chrome.exe /T");
            }
            // Для Linux/macOS
            else {
                Runtime.getRuntime().exec("pkill -f chromedriver");
                Runtime.getRuntime().exec("pkill -f chrome");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при завершении процессов Chrome: " + e.getMessage());
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Поток был прерван", e);
        }
    }
}