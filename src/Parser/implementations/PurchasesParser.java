package Parser.implementations;

import Parser.interfaces.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurchasesParser implements Parser {
    private final DriverSetup driverSetup;
    private final ResultsSaver<PurchaseItem> resultsSaver;
    private static final String BASE_URL = "https://zakupki.gov.ru/epz/order/extendedsearch/results.html";
    private final String searchQuery;
//    private static final String SEARCH_QUERY = "АКЦИОНЕРНОЕ+ОБЩЕСТВО+%22ОНЕЖСКИЙ+СУДОСТРОИТЕЛЬНО-СУДОРЕМОНТНЫЙ+ЗАВОД%22";
    private static final int ITEMS_PER_PAGE = 50;
    private static final int DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_PAGINATION_DELAY_MS = 3000;
    private static final String OUTPUT_FILENAME_PREFIX = "zakupki_results_";
    private final ParserStatusListener statusListener;

    public PurchasesParser(DriverSetup driverSetup) {
        this(driverSetup, null);
    }

    // Существующий конструктор - использует значение по умолчанию
    public PurchasesParser(DriverSetup driverSetup, ResultsSaver<PurchaseItem> resultsSaver) {
        this(driverSetup, resultsSaver,
                "АКЦИОНЕРНОЕ+ОБЩЕСТВО+%22ОНЕЖСКИЙ+СУДОСТРОИТЕЛЬНО-СУДОРЕМОНТНЫЙ+ЗАВОД%22",null);
    }

    public PurchasesParser(DriverSetup driverSetup, ResultsSaver<PurchaseItem> resultsSaver,
                           String searchQuery, ParserStatusListener statusListener) {
        this.driverSetup = driverSetup;
        this.resultsSaver = resultsSaver;
        this.searchQuery = searchQuery;
        this.statusListener = statusListener;
    }

    @Override
    public void parse() {
        WebDriver driver = driverSetup.setupDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS));

        try {
            List<PurchaseItem> allPurchases = new ArrayList<>();
            int currentPage = 1;
            int totalItems = 0;
            boolean hasNextPage = true;

            while (hasNextPage) {
                String paginatedUrl = buildPaginatedUrl(currentPage);
                driver.get(paginatedUrl);

                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".registry-entry__header-mid__number")));

                if (currentPage == 1) {
                    totalItems = extractTotalItems(driver);
                    if (statusListener != null) {
                        statusListener.updateTotalRecords(totalItems);
                        statusListener.updateStatus("Найдено записей: " + totalItems);
                    }
                    if (totalItems == 0) break;
                }

                List<WebElement> itemContainers = driver.findElements(
                        By.cssSelector(".search-registry-entry-block"));

                if (itemContainers.isEmpty()) {
                    hasNextPage = false;
                    continue;
                }

                for (WebElement itemContainer : itemContainers) {
                    try {
                        PurchaseItem item = extractPurchaseData(itemContainer);
                        allPurchases.add(item);

                        if (statusListener != null) {
                            statusListener.updateCurrentRecords(allPurchases.size());
                            statusListener.addPurchaseToTable(item);
                        }
                    } catch (Exception e) {
                        String errorMsg = "Ошибка при обработке элемента: " + e.getMessage();
                        if (statusListener != null) {
                            statusListener.updateStatus(errorMsg);
                        }
                        System.err.println(errorMsg);
                    }
                }

                if (currentPage * ITEMS_PER_PAGE >= totalItems) {
                    hasNextPage = false;
                } else {
                    currentPage++;
                    sleep(DEFAULT_PAGINATION_DELAY_MS);
                }
            }

            if (resultsSaver != null) {
                resultsSaver.save(allPurchases);
            }
            if (statusListener != null) {
                statusListener.updateStatus("Парсинг завершен! Обработано: " + allPurchases.size());
            }
        } catch (Exception e) {
            String errorMsg = "Ошибка при парсинге: " + e.getMessage();
            if (statusListener != null) {
                statusListener.updateStatus(errorMsg);
            }
            System.err.println(errorMsg);
            e.printStackTrace();
        } finally {
            if (driver != null) {
                try {
                    sleep(1000);
                    driver.quit();
                } catch (Exception e) {
                    String errorMsg = "Ошибка при закрытии драйвера: " + e.getMessage();
                    if (statusListener != null) {
                        statusListener.updateStatus(errorMsg);
                    }
                    System.err.println(errorMsg);
                }
            }
        }
    }

    private PurchaseItem extractPurchaseData(WebElement itemContainer) {
        // Извлекаем номер и ссылку
        WebElement numberElement = itemContainer.findElement(
                By.cssSelector(".registry-entry__header-mid__number a"));
        String number = numberElement.getText().trim();
        String href = numberElement.getAttribute("href");

        // Извлекаем объект закупки
        String purchaseObject = "Не указан";
        try {
            purchaseObject = itemContainer.findElement(By.xpath(
                    ".//div[contains(@class, 'registry-entry__body-block')]" +
                            "[.//div[contains(@class, 'registry-entry__body-title')]" +
                            "[contains(., 'Объект закупки')]]" +
                            "/div[contains(@class, 'registry-entry__body-value')]"
            )).getText().trim();
        } catch (Exception e) {
            System.err.println("Не удалось извлечь объект закупки: " + e.getMessage());
        }

        // Извлекаем заказчика
        String customer = "Не указан";
        try {
            customer = itemContainer.findElement(By.xpath(
                    ".//div[contains(@class, 'registry-entry__body-block')]" +
                            "[.//div[contains(@class, 'registry-entry__body-title')]" +
                            "[contains(., 'Заказчик')]]" +
                            "//span[contains(@class, 'highlightColor')]"
            )).getText().trim();
        } catch (Exception e) {
            System.err.println("Не удалось извлечь заказчика: " + e.getMessage());
        }

        return new PurchaseItemImpl(number, href, purchaseObject, customer);
    }

    private String buildPaginatedUrl(int pageNumber) {
        return BASE_URL + "?searchString=" + searchQuery +
                "&pageNumber=" + pageNumber +
                "&recordsPerPage=" + ITEMS_PER_PAGE;
    }


    private int extractTotalItems(WebDriver driver) {
        try {
            WebElement totalElement = driver.findElement(
                    By.cssSelector(".search-results__total"));
            String totalText = totalElement.getText();
            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(totalText);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            System.err.println("Не удалось извлечь общее количество записей: " + e.getMessage());
        }
        return 0;
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