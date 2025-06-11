package Parser.implementations.Parser223.Contracts;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.*;

public class ContractDetailsParser {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ContractDetailsParser(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public Map<String, String> parseContractDetails() {
        Map<String, String> contractData = new HashMap<>();
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            // Парсим основную информацию о контракте
            WebElement mainInfo = shortWait.until(ExpectedConditions
                    .presenceOfElementLocated(By.cssSelector("div.cardMainInfo")));

            // Номер договора (из заголовка)
            String contractNumber = mainInfo.findElement(By.cssSelector("span.cardMainInfo__purchaseLink a"))
                    .getText().trim();
            contractData.put("Номер договора (заголовок)", contractNumber);
//            System.out.println("Номер договора (заголовок): " + contractNumber);

            // Статус контракта
            String contractStatus = mainInfo.findElement(By.cssSelector("span.cardMainInfo__state"))
                    .getText().trim();
            contractData.put("Статус контракта", contractStatus);
//            System.out.println("Статус контракта: " + contractStatus);

            // Номер договора (в теле)
            String contractNumberBody = mainInfo.findElement(By.xpath(".//span[contains(text(),'Номер договора')]/following-sibling::span"))
                    .getText().trim();
            contractData.put("Номер договора", contractNumberBody);
//            System.out.println("Номер договора: " + contractNumberBody);

            // Заказчик
            String customer = mainInfo.findElement(By.xpath(".//span[contains(text(),'Заказчик')]/following-sibling::span/a"))
                    .getText().trim();
            contractData.put("Заказчик", customer);
//            System.out.println("Заказчик: " + customer);

            // Цена договора
            String price = mainInfo.findElement(By.cssSelector("div.rightBlock__price"))
                    .getText().replace("&nbsp;", " ").trim();
            contractData.put("Цена договора", price);
//            System.out.println("Цена договора: " + price);

            // Дата заключения
            String conclusionDate = mainInfo.findElement(By.xpath(".//div[contains(text(),'Заключение договора')]/following-sibling::div"))
                    .getText().trim();
            contractData.put("Дата заключения", conclusionDate);
//            System.out.println("Дата заключения: " + conclusionDate);

            // Срок исполнения
            String executionPeriodFull = mainInfo.findElement(By.xpath(".//div[contains(text(),'Срок исполнения')]/following-sibling::div"))
                    .getText().replace("&nbsp;", " ").trim();

            // Разделяем даты по дефису и берём последнюю часть
            String[] dates = executionPeriodFull.split("—");
            String endDate = dates.length > 1 ? dates[dates.length - 1].trim() : executionPeriodFull;

            contractData.put("Срок исполнения (окончание)", endDate);
//            System.out.println("Срок исполнения: " + endDate);

            // Даты размещения и обновления
            List<WebElement> dateElements = mainInfo.findElements(By.cssSelector("div.rightBlock__text"));
            if (dateElements.size() >= 3) {
                String placementDate = dateElements.get(dateElements.size() - 2).getText().trim();
                String updateDate = dateElements.get(dateElements.size() - 1).getText().trim();

                contractData.put("Дата размещения", placementDate);
                contractData.put("Дата обновления", updateDate);
//                System.out.println("Дата размещения: " + placementDate);
//                System.out.println("Дата обновления: " + updateDate);
            }

            return contractData;

        } catch (TimeoutException e) {
            System.err.println("Не удалось найти блок с информацией о контракте: " + e.getMessage());
            return contractData;
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге данных контракта: " + e.getMessage());
            return contractData;
        }
    }

    public Map<String, String> parseGeneralInfo() {
        Map<String, String> generalInfo = new HashMap<>();
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        System.out.println("\n=== ПАРСИНГ БЛОКА 'ОБЩАЯ ИНФОРМАЦИЯ' ===");

        try {
            List<WebElement> infoBlocks = shortWait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'container')]//div[contains(@class, 'blockInfo')]")));

            boolean found = false;

            for (WebElement block : infoBlocks) {
                try {
                    WebElement title = block.findElement(By.xpath(".//h2[contains(@class, 'blockInfo__title')]"));
                    if (!title.getText().trim().equals("Общая информация")) {
                        continue;
                    }

                    found = true;
//                    System.out.println("\nНайден блок 'Общая информация':");
//                    System.out.println("--------------------------------");

                    List<WebElement> sections = block.findElements(By.xpath(".//section[contains(@class, 'section')]"));
                    for (WebElement section : sections) {
                        try {
                            String key = section.findElement(By.xpath(".//span[contains(@class, 'section__title')]"))
                                    .getText().trim();

                            String value;
                            try {
                                WebElement link = section.findElement(By.xpath(".//span[contains(@class, 'section__info')]//a"));
                                value = link.getText().trim();
                                generalInfo.put(key, value);
//                                System.out.printf("%-30s: %s (ссылка)%n", key, value);
                            } catch (NoSuchElementException e) {
                                value = section.findElement(By.xpath(".//span[contains(@class, 'section__info')]"))
                                        .getText().trim();
                                generalInfo.put(key, value);
//                                System.out.printf("%-30s: %s%n", key, value);
                            }
                        } catch (Exception e) {
                            System.err.println("Ошибка при парсинге секции: " + e.getMessage());
                        }
                    }
                    break;

                } catch (NoSuchElementException e) {
                    continue;
                }
            }

            if (!found) {
                System.out.println("Блок 'Общая информация' не найден на странице");
            }

//            System.out.println("--------------------------------");
//            System.out.println("Всего извлечено полей: " + generalInfo.size());
//            System.out.println("=== ПАРСИНГ ЗАВЕРШЁН ===\n");

        } catch (TimeoutException e) {
            System.err.println("Таймаут при поиске блоков информации: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Критическая ошибка при парсинге: " + e.getMessage());
        }

        return generalInfo;
    }
}