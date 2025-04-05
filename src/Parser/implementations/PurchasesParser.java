package Parser.implementations;

import Parser.interfaces.DriverSetup;
import Parser.interfaces.Parser;
import Parser.interfaces.PurchaseItem;
import Parser.interfaces.ResultsSaver;
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
    private static final String SEARCH_QUERY = "АКЦИОНЕРНОЕ+ОБЩЕСТВО+%22ОНЕЖСКИЙ+СУДОСТРОИТЕЛЬНО-СУДОРЕМОНТНЫЙ+ЗАВОД%22";
    private static final int ITEMS_PER_PAGE = 50;
    private static final int DEFAULT_PAGE_LOAD_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_PAGINATION_DELAY_MS = 3000;
    private static final String OUTPUT_FILENAME_PREFIX = "zakupki_results_";

    public PurchasesParser(DriverSetup driverSetup) {
        this(driverSetup, null);  // По умолчанию без сохранения
    }

    public PurchasesParser(DriverSetup driverSetup, ResultsSaver<PurchaseItem> resultsSaver) {
        this.driverSetup = driverSetup;
        this.resultsSaver = resultsSaver;
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
                    System.out.println("Всего найдено записей: " + totalItems);
                    if (totalItems == 0) break;
                }

                // Получаем все контейнеры закупок
                List<WebElement> itemContainers = driver.findElements(
                        By.cssSelector(".search-registry-entry-block"));

                System.out.printf("Страница %d: найдено %d элементов%n",
                        currentPage, itemContainers.size());

                if (itemContainers.isEmpty()) {
                    hasNextPage = false;
                    continue;
                }

                for (WebElement itemContainer : itemContainers) {
                    try {
                        PurchaseItem item = extractPurchaseData(itemContainer);
                        allPurchases.add(item);
                        System.out.println(item);
                    } catch (Exception e) {
                        System.err.println("Ошибка при обработке элемента: " + e.getMessage());
                    }
                }

                if (currentPage * ITEMS_PER_PAGE >= totalItems) {
                    hasNextPage = false;
                } else {
                    currentPage++;
                    sleep(DEFAULT_PAGINATION_DELAY_MS);
                }
            }

            System.out.println("Всего собрано записей: " + allPurchases.size());
            if (resultsSaver != null) {  // Сохраняем только если передан saver
                resultsSaver.save(allPurchases);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                try {
                    sleep(1000);
                    driver.quit();
                } catch (Exception e) {
                    System.err.println("Ошибка при закрытии драйвера: " + e.getMessage());
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
        return BASE_URL + "?searchString=" + SEARCH_QUERY +
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