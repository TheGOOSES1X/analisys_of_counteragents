

package Parser.implementations.Parser223;

import Parser.Database.models.*;
import Parser.implementations.Parser223.Contracts.Parser223;
import Parser.implementations.Parser44.CustomerPageParser;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.*;
import java.util.*;

import static Parser.utils.ParserUtils.parseDate;
import static Parser.utils.ParserUtils.parsePrice;

public class MainInfoParser223 {
    private final Parser223 contractParser;
    private final CustomerPageParser customerPageParser;


    public MainInfoParser223() {
        this.customerPageParser = new CustomerPageParser();
        this.contractParser = new Parser223();
    }
    public Purchase parsePurchaseMainInfo(String url, WebDriver driver) {
        driver.get(url);

        WebElement purchaseCard = driver.findElement(By.cssSelector(".search-results.item"));

        Map<String, String> commonData = collectCommonTextData(driver);
        Map<String, String> purchaseData = parsePurchaseCard(purchaseCard);

        return parsePurchasePageFromDataFiller(purchaseData, commonData);

    }
    public Contract parsePurchaseContract(String url, WebDriver driver) {
        driver.get(url);
        WebElement purchaseCard = driver.findElement(By.cssSelector(".search-results.item"));
        Map<String, String> purchaseData = parsePurchaseCard(purchaseCard);

        if (purchaseData.containsKey("contractLink")) {
            return contractParser.parseContract(purchaseData.get("contractLink"),driver);
        }
        return null;
    }
    public List<ProcurementObject> parsePurchaseSubjects(String url, WebDriver driver) {
        driver.get(url);
        WebElement purchaseCard = driver.findElement(By.cssSelector(".search-results.item"));
        Map<String, String> purchaseData = parsePurchaseCard(purchaseCard);

        if (purchaseData.containsKey("contractLink")) {
            return contractParser.parseContractSubjects(purchaseData.get("contractLink"), driver);
        }
        return Collections.emptyList();
    }
    public Customer parsePurchaseCustomer(String url, WebDriver driver) {
        driver.get(url);
        Map<String, String> commonData = collectCommonTextData(driver);
        String customerUrl = commonData.get("Сведения о заказчике > Ссылка на заказчика");

        if (customerUrl != null && !customerUrl.isEmpty()) {
            driver.get(customerUrl);
            return customerPageParser.parseCustomerPage(customerUrl, driver);
        }
        return null;
    }

//    public Purchase parsePurchasePage(String url, WebDriver driver, WebDriverWait wait) {
//        driver.get(url);
//        //Закупка
//        Purchase purchase = new Purchase();
//        this.currentUrl = url;
//        WebElement purchaseCard = driver.findElement(By.cssSelector(".search-results.item"));
//        Map<String, String> data = collectCommonTextData(driver);
//        String customerUrl = data.get("Сведения о заказчике > Ссылка на заказчика");
//        System.out.println(customerUrl);
//        Map<String, String> purchaseData = parsePurchaseCard(purchaseCard);
//
//
//        parsePurchasePageFromData(purchase, purchaseData,data);
//        //Контракты
//        this.contractUrl = purchaseData.get("contractLink");
//        contractParser223.parseContractInfo( this.contractUrl, driver,wait);
//        //Предмет закупки
//        contractParser223.parseContractSubjects( this.contractUrl, driver,wait);
//        //Заказчик
//        driver.get(customerUrl);
//        Customer customer = customerPageParser.parseCustomerPage(customerUrl, driver);
//
//        return purchase;
//    }

    public Purchase parsePurchasePageFromDataFiller(Map<String, String> commonData, Map<String, String> purchaseData) {
        try {
            Purchase purchase = new Purchase();

            // Заполняем данные из purchaseData (парсинг карточки закупки)
            if (commonData.containsKey("updateDate")) {
                purchase.setUpdateDate(parseDate(commonData.get("updateDate")));
            }
            if (commonData.containsKey("currency")) {
                purchase.setCurrency(commonData.get("currency"));
            }
            if (commonData.containsKey("initialPrice")) {
                purchase.setInitialMaxPrice(parsePrice(commonData.get("initialPrice")));
            }
            if (commonData.containsKey("applicationEndDate") && commonData.get("applicationEndDate") != null) {
                purchase.setApplicationEndDate(parseDate(commonData.get("applicationEndDate")));
            }
            if (commonData.containsKey("purchaseStatus")) {
                purchase.setProcurementStage(commonData.get("purchaseStatus"));
            }

            if (commonData.containsKey("purchaseObject")) {
                purchase.setPurchaseObject(commonData.get("purchaseObject"));
            }
            if (commonData.containsKey("publicationDate")) {
                purchase.setPublicationDate(parseDate(commonData.get("publicationDate")));
            }
            if (commonData.containsKey("purchaseType")) {
                purchase.setLaw(commonData.get("purchaseType"));
            }
            if (commonData.containsKey("purchaseNumber")) {
                purchase.setPurchaseNumber(commonData.get("purchaseNumber"));
            }
            if (purchaseData.containsKey("Сведения о закупке > Способ осуществления закупки")) {
                purchase.setProcurementMethod(purchaseData.get("Сведения о закупке > Способ осуществления закупки"));
            }


            // Если есть данные об объектах закупки, можно их добавит

            return purchase;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга данных закупки: " + e.getMessage(), e);
        }
    }

    private Map<String, String> parsePurchaseCard(WebElement cardElement) {
        Map<String, String> result = new HashMap<>();

        try {
            // Основные данные из карточки
            result.put("purchaseType", safeFindElementText(cardElement, By.cssSelector(".registry-entry__header-top__title")));
            result.put("purchaseNumber", safeFindElementText(cardElement, By.cssSelector(".registry-entry__header-mid__number a"))
                    .replace("№", "").trim());
            result.put("purchaseStatus", safeFindElementText(cardElement, By.cssSelector(".registry-entry__header-mid__title")));
            result.put("purchaseObject", safeFindElementText(cardElement,
                    By.xpath(".//div[contains(@class, 'registry-entry__body-title') and contains(text(), 'Объект закупки')]/following-sibling::div")));
            result.put("customer", safeFindElementText(cardElement,
                    By.xpath(".//div[contains(@class, 'registry-entry__body-title') and contains(text(), 'Заказчик')]/following-sibling::div/a")));

            // Обработка цены
            try {
                WebElement priceElement = cardElement.findElement(By.cssSelector(".price-block__value"));
                String priceText = priceElement.getText().trim();
                String initialPrice = priceText.replaceAll("[^\\d,]", "").replace(",", ".").trim();
                result.put("initialPrice", initialPrice);

                String[] priceParts = priceText.split("\\s+");
                String currency = priceParts.length > 0 ? priceParts[priceParts.length - 1] : "";
                result.put("currency", currency);
            } catch (NoSuchElementException e) {
                System.out.println("Элемент цены не найден");
                result.put("initialPrice", null);
                result.put("currency", null);
            }

            result.put("publicationDate", safeFindElementText(cardElement,
                    By.xpath(".//div[contains(@class, 'data-block__title') and contains(text(), 'Размещено')]/following-sibling::div")));
            result.put("updateDate", safeFindElementText(cardElement,
                    By.xpath(".//div[contains(@class, 'data-block__title') and contains(text(), 'Обновлено')]/following-sibling::div")));

            // Парсим дату окончания подачи заявок
            result.put("applicationEndDate", safeFindElementText(cardElement,
                    By.xpath(".//div[contains(@class, 'data-block__title') and contains(text(), 'Окончание подачи заявок')]/following-sibling::div")));

            // Парсим ссылки (контракт, план закупки, жалоба)
            try {
                WebElement hrefBlock = cardElement.findElement(By.cssSelector(".href-block"));
                List<WebElement> links = hrefBlock.findElements(By.tagName("a"));

                result.put("contractLink", links.size() > 0 ? links.get(0).getAttribute("href") : null);
                result.put("purchasePlanLink", links.size() > 1 ? links.get(1).getAttribute("href") : null);
                result.put("complaintLink", links.size() > 2 ? links.get(2).getAttribute("href") : null);
            } catch (NoSuchElementException e) {
                System.out.println("Блок с ссылками не найден");
                result.put("contractLink", null);
                result.put("purchasePlanLink", null);
                result.put("complaintLink", null);
            }

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге карточки закупки: " + e.getMessage());
            // Можно либо вернуть пустой Map, либо заполнить его null значениями
            // В данном случае просто продолжаем с уже собранными данными
        }

        return result;
    }

    // Вспомогательный метод для безопасного поиска текста элемента
    private String safeFindElementText(WebElement parent, By locator) {
        try {
            return parent.findElement(locator).getText().trim();
        } catch (NoSuchElementException e) {
            System.out.println("Элемент не найден: " + locator);
            return null;
        }
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
//
//            System.out.println("\n=== ИТОГОВЫЕ ДАННЫЕ ===");
//            result.forEach((key, value) -> System.out.printf("%-50s: %s%n", key, value));
//            System.out.println("=======================");

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

}