package Parser.implementations.Parser223.Contracts;

import Parser.implementations.Parser223.Contracts.Interfaces.IUrlNavigator;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class UrlNavigator implements IUrlNavigator {
    @Override
    public String findContractUrl(String url, WebDriver driver) {
        driver.get(url);
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            // Быстрая проверка наличия таблицы с контрактами
            List<WebElement> tables = shortWait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.xpath("//div[@id='inner-html']//table")));

            if (!tables.isEmpty()) {
                // Быстрая проверка наличия ссылки на контракт
                WebElement contractLink = shortWait.until(ExpectedConditions
                        .presenceOfElementLocated(By.xpath("//div[@id='inner-html']//table//a[contains(@href, '/epz/contractfz223/card/contract-info.html')]")));

                String relativeUrl = contractLink.getAttribute("href");
                return relativeUrl.startsWith("http") ? relativeUrl : "https://zakupki.gov.ru" + relativeUrl;
            }

            // Если таблицы нет, проверяем сообщение об отсутствии данных (быстрая проверка)
            List<WebElement> noDataMessages = driver.findElements(By.xpath("//div[contains(@class, 'section__title') and contains(text(), 'Сведения отсутствуют')]"));
            if (!noDataMessages.isEmpty()) {
                return null;
            }

            return null;

        } catch (TimeoutException e) {
            return null;
        } catch (Exception e) {
            System.err.println("Error finding contract URL: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String convertToSubjectUrl(String contractInfoUrl) {
        try {
            if (contractInfoUrl == null || contractInfoUrl.isEmpty()) {
                System.err.println("URL не может быть null или пустым");
                return null;
            }

            if (!contractInfoUrl.startsWith("https://zakupki.gov.ru")) {
                System.err.println("URL должен начинаться с https://zakupki.gov.ru");
                return null;
            }

            if (!contractInfoUrl.contains("/epz/contractfz223/card/contract-info.html?id=")) {
                System.err.println("URL должен содержать '/epz/contractfz223/card/contract-info.html?id='");
                return null;
            }

            return contractInfoUrl.replace("/contract-info.html", "/contract-subject.html");

        } catch (Exception e) {
            System.err.println("Ошибка при преобразовании URL: " + e.getMessage());
            return null;
        }
    }
}