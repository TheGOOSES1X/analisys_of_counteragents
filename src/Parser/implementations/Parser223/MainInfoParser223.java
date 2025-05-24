

package Parser.implementations.Parser223;

import Parser.Database.models.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.*;

public class MainInfoParser223 {
    private final ContractParser223 contractParser223;

    private String currentUrl;
    private String contractUrl;

    public MainInfoParser223() {
        this.contractParser223 = new ContractParser223();
    }

    public Purchase parsePurchasePage(String url, WebDriver driver, WebDriverWait wait) {
        driver.get(url);
        this.currentUrl = url;
        WebElement purchaseCard = driver.findElement(By.cssSelector(".search-results.item"));
        Map<String, String> data = collectCommonTextData(driver);
        String customerUrl = data.get("Сведения о заказчике > Ссылка на заказчика");
        System.out.println(customerUrl);
        Map<String, String> purchaseData = parsePurchaseCard(purchaseCard);
        Purchase purchase = new Purchase();
        this.contractUrl = purchaseData.get("contractLink");
        contractParser223.parseContractInfo( this.contractUrl, driver,wait);

        return purchase;
    }

    private Map<String, String> parsePurchaseCard(WebElement cardElement) {
        Map<String, String> result = new HashMap<>();

        try {
            // Основные данные из карточки
            result.put("purchaseType", cardElement.findElement(By.cssSelector(".registry-entry__header-top__title"))
                    .getText().trim());

            result.put("purchaseNumber", cardElement.findElement(By.cssSelector(".registry-entry__header-mid__number a"))
                    .getText().replace("№", "").trim());

            result.put("purchaseStatus", cardElement.findElement(By.cssSelector(".registry-entry__header-mid__title"))
                    .getText().trim());

            result.put("purchaseObject", cardElement.findElement(By.xpath(".//div[contains(@class, 'registry-entry__body-title') and contains(text(), 'Объект закупки')]/following-sibling::div"))
                    .getText().trim());

            result.put("customer", cardElement.findElement(By.xpath(".//div[contains(@class, 'registry-entry__body-title') and contains(text(), 'Заказчик')]/following-sibling::div/a"))
                    .getText().trim());

            result.put("initialPrice", cardElement.findElement(By.cssSelector(".price-block__value"))
                    .getText().replaceAll("[^\\d,]", "").replace(",", ".").trim());

            result.put("publicationDate", cardElement.findElement(By.xpath(".//div[contains(@class, 'data-block__title') and contains(text(), 'Размещено')]/following-sibling::div"))
                    .getText().trim());

            result.put("updateDate", cardElement.findElement(By.xpath(".//div[contains(@class, 'data-block__title') and contains(text(), 'Обновлено')]/following-sibling::div"))
                    .getText().trim());

            // Парсим дату окончания подачи заявок (если элемент существует)
            try {
                String applicationEndDate = cardElement.findElement(By.xpath(
                                ".//div[contains(@class, 'data-block__title') and contains(text(), 'Окончание подачи заявок')]/following-sibling::div"))
                        .getText().trim();
                result.put("applicationEndDate", applicationEndDate);
            } catch (NoSuchElementException e) {
                System.out.println("Элемент 'Окончание подачи заявок' не найден");
                result.put("applicationEndDate", null);
            }
            // Парсим ссылки (контракт, план закупки, жалоба)
            try {
                WebElement hrefBlock = cardElement.findElement(By.cssSelector(".href-block"));
                List<WebElement> links = hrefBlock.findElements(By.tagName("a"));

                if (!links.isEmpty()) {
                    // Ссылка на контракт (первая ссылка в блоке)
                    String contractLink = links.get(0).getAttribute("href");
                    result.put("contractLink", contractLink);

                    // Ссылка на план закупки (вторая ссылка)
                    String purchasePlanLink = links.get(1).getAttribute("href");
                    result.put("purchasePlanLink", purchasePlanLink);

                    // Ссылка на жалобы (третья ссылка)
                    String complaintLink = links.get(2).getAttribute("href");
                    result.put("complaintLink", complaintLink);
                }
            } catch (NoSuchElementException e) {
                System.out.println("Блок с ссылками не найден");
                result.put("contractLink", null);
                result.put("purchasePlanLink", null);
                result.put("complaintLink", null);
            }

            // Для отладки можно вывести результаты
            System.out.println("=== Парсинг карточки закупки ===");
            result.forEach((key, value) -> System.out.println(key + ": " + value));
            System.out.println("===============================");

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге карточки закупки: " + e.getMessage());
            throw e;
        }

        return result;
    }


    private Map<String, String> collectCommonTextData(WebDriver driver) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 1. Парсим основные блоки с common-text
            List<WebElement> containers = wait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.cssSelector("div.container")));

            System.out.println("Найдено контейнеров: " + containers.size());

            for (WebElement container : containers) {
                try {
                    String sectionCaption = "";
                    try {
                        sectionCaption = container.findElement(By.cssSelector("div.common-text__caption"))
                                .getText().trim() + " > ";
                    } catch (NoSuchElementException e) {}

                    List<WebElement> dataBlocks = container.findElements(By.cssSelector("div.col-9.mr-auto, div.col-6.mr-auto"));

                    for (WebElement block : dataBlocks) {
                        try {
                            // Оригинальная логика парсинга common-text блоков
                            String title = "";
                            String value = "";

                            try {
                                title = block.findElement(By.cssSelector("div.common-text__title")).getText().trim();
                                try {
                                    // Особый случай для названия организации (может содержать ссылку)
                                    if (title.equals("Наименование организации")) {
                                        try {
                                            WebElement link = block.findElement(By.cssSelector("div.common-text__value a"));
                                            String customerUrl = link.getAttribute("href");
                                            String customerName = link.getText().trim();

                                            // Сохраняем полный URL
                                            result.put(sectionCaption + "Ссылка на заказчика",customerUrl);
                                            result.put(sectionCaption + title, customerName);
                                            continue; // Переходим к следующему блоку
                                        } catch (NoSuchElementException e) {
                                            // Если ссылки нет, продолжаем как обычно
                                        }
                                    }

                                    // Обычный случай
                                    value = block.findElement(By.cssSelector("div.common-text__value:not(.common-text__value--gray)"))
                                            .getText().replaceAll("\\s+", " ").trim();
                                } catch (NoSuchElementException e) {
                                    value = block.findElement(By.cssSelector("div.common-text__value--gray + div"))
                                            .getText().trim();
                                }

                                if (!title.isEmpty()) {
                                    result.put(sectionCaption + title, value);
                                }
                            } catch (NoSuchElementException e) {}

                            // Обработка серых блоков (ИНН/КПП/ОГРН)
                            try {
                                List<WebElement> grayBlocks = block.findElements(By.cssSelector("div.common-text__value--gray"));
                                List<WebElement> valueBlocks = block.findElements(By.cssSelector("div.common-text__value:not(.common-text__value--gray)"));

                                if (grayBlocks.size() == valueBlocks.size()) {
                                    for (int i = 0; i < grayBlocks.size(); i++) {
                                        String grayTitle = grayBlocks.get(i).getText().trim();
                                        String grayValue = valueBlocks.get(i).getText().trim();

                                        if (!grayTitle.isEmpty()) {
                                            result.put(sectionCaption + grayTitle, grayValue);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Ошибка при обработке серых блоков: " + e.getMessage());
                            }
                        } catch (Exception e) {
                            System.out.println("Ошибка при обработке блока данных: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при обработке контейнера: " + e.getMessage());
                }
            }

            System.out.println("\n=== ИТОГОВЫЕ ДАННЫЕ ===");
            result.forEach((key, value) -> System.out.printf("%-50s: %s%n", key, value));
            System.out.println("=======================");

        } catch (Exception e) {
            System.out.println("Ошибка при сборе данных: " + e.getMessage());
        }

        return result;
    }

    private Map<String, String> collectDateInfo(WebDriver driver) {
        return null;
    }

    private List<ProcurementObject> parseProcurementObjectsTable(WebDriver driver) {
        return null;
    }

    private boolean isValidProcurementObject(ProcurementObject obj) {
        return false;
    }

    private LocalDate parseDate(String dateStr) {
        return null;
    }

    private BigDecimal parsePrice(String priceStr) {
        return null;
    }
}