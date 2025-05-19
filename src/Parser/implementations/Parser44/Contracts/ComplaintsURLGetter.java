package Parser.implementations.Parser44.Contracts;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static Parser.implementations.Parser44.Contracts.ParserUtils.extractRegNumber;

public class ComplaintsURLGetter {

        public String findComplaintUrlByRegNumber(String originalUrl, WebDriver driver, WebDriverWait wait) {
            String regNumber = extractRegNumber(originalUrl);
            if (regNumber == null) {
                System.out.println("Не удалось извлечь номер закупки из URL: " + originalUrl);
                return null;
            }
            try {
                // Формируем URL для поиска жалоб
                String searchUrl = "https://zakupki.gov.ru/epz/complaint/search/search_eis.html?" +
                        "searchString=" + regNumber +
                        "&strictEqual=on&fz94=on&cancelled=on&considered=on&regarded=on";

                // Открываем страницу поиска жалоб
                driver.get(searchUrl);

                // Ждем появления блока с количеством результатов
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.search-results__total")));

                // Проверяем количество записей
                int totalItems = extractTotalItems(driver);
                if (totalItems == 0) {
                    System.out.println("Жалобы по номеру " + regNumber + " не найдены");
                    return null;
                }

                // Ждем появления блока с жалобой
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.registry-entry__header")));

                // Ищем ссылку на жалобу в новом формате
                WebElement complaintLink = driver.findElement(
                        By.cssSelector("div.registry-entry__header-mid a[href*='/epz/complaint/card/complaint-information.html']"));

                String href = complaintLink.getAttribute("href");
                System.out.println("Найдена ссылка на жалобу: " + href);
                return href;

            } catch (TimeoutException e) {
                System.out.println("Таймаут при поиске жалобы по номеру: " + regNumber);
                return null;
            } catch (NoSuchElementException e) {
                System.out.println("Элемент не найден: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.out.println("Ошибка при поиске жалобы: " + e.getMessage());
                return null;
            }
        }

    public int countComplaintsByRegNumber(String originalUrl, WebDriver driver, WebDriverWait wait) {
        String originalPage = driver.getCurrentUrl();
        try {
            // Основная логика
            String regNumber = extractRegNumber(originalUrl);
            if (regNumber == null) return 0;

            String searchUrl = "https://zakupki.gov.ru/epz/complaint/search/search_eis.html?" +
                    "searchString=" + regNumber + "&strictEqual=on&fz94=on&cancelled=on&considered=on&regarded=on";

            driver.get(searchUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.search-results__total")));

            return extractTotalItems(driver);
        } finally {
            // Всегда возвращаемся на исходную страницу
            try {
                driver.get(originalPage);
            } catch (Exception e) {
                System.err.println("Error returning to original page: " + e.getMessage());
            }
        }
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


}
