package Parser.implementations.Parser44;

import Parser.Database.models.JudicialProceeding;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;

public class LitigationParser {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private JudicialProceeding currentProceeding;

    public LitigationParser(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void searchByInn(String inn) {
        try {
            driver.get("https://kad.arbitr.ru/");

            WebElement input = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("textarea[placeholder='название, ИНН или ОГРН']")
                    )
            );
            input.clear();
            input.sendKeys(inn);

            WebElement searchButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("#b-form-submit button")
                    )
            );
            searchButton.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#b-cases tbody tr")
            ));
            parseCases();

        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Элемент не найден: " + e.getMessage());
        }
    }

    public List<JudicialProceeding> parseCases() {
        List<JudicialProceeding> proceedings = new ArrayList<>();
        String mainWindow = driver.getWindowHandle();
        try {
            List<WebElement> rows = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#b-cases tbody tr"))
            );

            for (WebElement row : rows) {
                currentProceeding = new JudicialProceeding();

                // 1. Номер дела и ссылка
                WebElement numCell = row.findElement(By.cssSelector("td.num"));
                String caseNumber = numCell.findElement(By.cssSelector("a.num_case")).getText().trim();
                String caseLink = numCell.findElement(By.cssSelector("a.num_case")).getAttribute("href");
                String startDateStr = numCell.findElement(By.cssSelector("div.civil span, div.civil_simple span")).getText().trim();

                // 2. Суд и судья
                WebElement courtCell = row.findElement(By.cssSelector("td.court"));
                String judge = "";
                String court = "";

                try {
                    judge = courtCell.findElement(By.cssSelector("div.b-container > div.judge")).getText().trim();
                } catch (NoSuchElementException e) {
                    System.out.println("Информация о судье не найдена");
                }

                try {
                    court = courtCell.findElement(By.cssSelector("div.b-container > div:not(.judge)")).getText().trim();
                } catch (NoSuchElementException e) {
                    System.out.println("Информация о суде не найдена");
                }

                // 3. Истец
                WebElement plaintiffCell = row.findElement(By.cssSelector("td.plaintiff"));
                String plaintiff = plaintiffCell.findElement(By.cssSelector("span.js-rollover")).getText().trim();

                // 4. Ответчик
                WebElement respondentCell = row.findElement(By.cssSelector("td.respondent"));
                String defendant = respondentCell.findElement(By.cssSelector("span.js-rollover")).getText().trim();

                // Заполняем объект данными из таблицы
                currentProceeding.setCaseNumber(caseNumber);
                currentProceeding.setJudge(judge);
                currentProceeding.setCurrentInstance(court);
                currentProceeding.setPlaintiff(plaintiff);
                currentProceeding.setDefendant(defendant);

                // Парсим дату начала дела
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate startDate = LocalDate.parse(startDateStr, formatter);
                    currentProceeding.setStartDate(startDate);
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга даты: " + startDateStr);
                }

                // Переходим на страницу дела для получения дополнительных данных
                ((JavascriptExecutor)driver).executeScript("window.open('" + caseLink + "')");
                String originalWindow = driver.getWindowHandle();

                for (String windowHandle : driver.getWindowHandles()) {
                    if (!originalWindow.contentEquals(windowHandle)) {
                        driver.switchTo().window(windowHandle);
                        break;
                    }
                }

                // Парсим дополнительные данные
                parseCaseDetails();

                // Закрываем вкладку и возвращаемся
                driver.close();
                driver.switchTo().window(originalWindow);

                // Добавляем дело в список
                proceedings.add(currentProceeding);
            }

            return proceedings;

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге: " + e.getMessage());
            return proceedings;
        }
    }

    private void parseCaseDetails() {
        try {
            // 1. Тип дела (description)
            String description = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("dl.b-iblock_lightgrey dt span")
                    )
            ).getText().replaceAll("\n", " ").trim();
            currentProceeding.setDescription(description);

            // 2. Статус дела (status)
            String status = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("div.b-case-header-desc")
                    )
            ).getText().trim();
            currentProceeding.setStatus(status);

            // 3. Длительность дела (duration)
            String caseDuration = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("ul.b-case-overview li.case-dur")
                    )
            ).getText().replaceAll("\n", " ").trim();
            currentProceeding.setStatus(caseDuration);

            // 4. Хронология дела (outcome)
            try {
                WebElement firstChrono = wait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.cssSelector("div.b-case-chrono-content > div.b-chrono-item-header:first-child")
                        )
                );
                String outcome = firstChrono.findElement(
                        By.cssSelector("h2.b-case-result a")
                ).getText().replaceAll("\\s+", " ").trim();
                currentProceeding.setOutcome(outcome);
            } catch (NoSuchElementException e) {
                System.out.println("Хронология дела не найдена");
            }

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге деталей дела: " + e.getMessage());
        }
    }
}