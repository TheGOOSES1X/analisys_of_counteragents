package Parser.implementations.Parser44.Contracts;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static Parser.utils.ParserUtils.extractRegNumber;


public class ContractDraftUrlFinder {

    public String findContractDraftUrl(String originalUrl, WebDriver driver, WebDriverWait wait) {
        try {
            // 1. Извлекаем номер закупки из оригинального URL
            String regNumber = extractRegNumber(originalUrl);
            if (regNumber == null) {
                System.out.println("Не удалось извлечь номер закупки из URL: " + originalUrl);
                return null;
            }

            // 2. Формируем URL контракта напрямую (первый вариант - contract-draft.html)
            String draftContractUrl = originalUrl.replaceAll(
                    "/epz/order/notice/\\w+/view/\\w+-info\\.html\\?regNumber=\\d+",
                    "/epz/order/notice/rpec/contract-draft.html?regNumber=" + regNumber + "0001"
            );

            // 3. Проверяем страницу contract-draft.html
            driver.get(draftContractUrl);

            // Проверяем наличие контейнера draftPF (основной контент страницы)
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#draftPF")));

                // Если контейнер есть, проверяем что он не пустой
                WebElement draftContainer = driver.findElement(By.cssSelector("div#draftPF"));
                if (draftContainer.findElements(By.cssSelector("*")).size() > 0) {
                    System.out.println("Найден черновик контракта (contract-draft.html): " + draftContractUrl);
                    return draftContractUrl;
                } else {
                    System.out.println("Контейнер draftPF пустой, пробуем common-info.html");
                    // Переходим к проверке common-info.html
                }
            } catch (TimeoutException e) {
                System.out.println("Контейнер draftPF не найден, пробуем common-info.html");
                // Переходим к проверке common-info.html
            }

            // 4. Пробуем второй вариант URL (common-info.html)
            String commonInfoUrl = originalUrl.replaceAll(
                    "/epz/order/notice/\\w+/view/\\w+-info\\.html\\?regNumber=\\d+",
                    "/epz/order/notice/rpec/common-info.html?regNumber=" + regNumber + "0001"
            );

            driver.get(commonInfoUrl);

            // Проверяем наличие данных контракта на второй странице
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.blockInfo")));
                System.out.println("Найден черновик контракта (common-info.html): " + commonInfoUrl);
                return commonInfoUrl;
            } catch (TimeoutException e) {
                System.out.println("Не удалось найти данные контракта на странице: " + commonInfoUrl);
                return null;
            }

        } catch (Exception e) {
            System.out.println("Ошибка при поиске контракта: " + e.getMessage());
            return null;
        }


    }

    public String findContractUrlByRegNumber(String originalUrl, WebDriver driver, WebDriverWait wait) {

        String regNumber = extractRegNumber(originalUrl);
        if (regNumber == null) {
            System.out.println("Не удалось извлечь номер закупки из URL: " + originalUrl);
            return null;
        }
        try {
            // Формируем URL для поиска контракта
            String searchUrl = "https://zakupki.gov.ru/epz/contract/search/results.html?" +
                    "searchString=&orderNumber=" + regNumber +
                    "&openMode=USE_DEFAULT_PARAMS&fz44=on&priceFrom=0&priceTo=200000000000" +
                    "&contractStageList=0%2C1%2C2%2C3&budgetaryFunds=on&extraBudgetaryFunds=on";

            // Открываем страницу поиска
            driver.get(searchUrl);

            // Ждем появления блока с результатами
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.search-registry-entrys-block")));

            // Ищем блок с контрактом
            WebElement contractBlock = driver.findElement(
                    By.cssSelector("div.search-registry-entry-block"));

            // Извлекаем ссылку на контракт
            WebElement contractLink = contractBlock.findElement(
                    By.cssSelector("a[href*='/epz/contract/contractCard/common-info.html']"));

            String href = contractLink.getAttribute("href");
            System.out.println("Найдена ссылка на контракт: " + href);
            return href;

        } catch (TimeoutException e) {
            System.out.println("Не удалось найти блок с контрактом по номеру: " + regNumber);
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("Не удалось найти ссылку на контракт: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Ошибка при поиске контракта: " + e.getMessage());
            return null;
        }
    }

}
