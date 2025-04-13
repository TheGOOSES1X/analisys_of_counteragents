package Parser.implementations;
import Parser.interfaces.*;

import Parser.Database.models.Purchase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseParser44 implements Parser {
    private final DriverSetup driverSetup;
    private ExecutorService executor;
    private volatile boolean isStopped;

    @Override
    public void parse() {
        // Реализация для одиночного парсинга, если нужна
    }

    public static class ParseResult {
        public final String url;
        public final Purchase purchaseData;
        public final Exception error;

        public ParseResult(String url, Purchase purchaseData, Exception error) {
            this.url = url;
            this.purchaseData = purchaseData;
            this.error = error;
        }
    }

    public PurchaseParser44(DriverSetup driverSetup) {
        this.driverSetup = driverSetup;
    }

    public void parseUrlsParallel(List<String> urls,
                                  Consumer<ParseResult> callback,
                                  int threadCount) {
        isStopped = false;
        executor = Executors.newFixedThreadPool(threadCount);

        CompletionService<ParseResult> completionService =
                new ExecutorCompletionService<>(executor);

        for (String url : urls) {
            completionService.submit(() -> parseSingleUrl(url));
        }

        for (int i = 0; i < urls.size(); i++) {
            if (isStopped) break;

            try {
                Future<ParseResult> future = completionService.take();
                ParseResult result = future.get();
                callback.accept(result);
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                callback.accept(new ParseResult(null, null, e));
            }
        }

        shutdown();
    }

    private ParseResult parseSingleUrl(String url) {
        WebDriver driver = null;
        try {
            if (isStopped) return new ParseResult(url, null, null);

            driver = driverSetup.setupDriver();
            driver.get(url);

            // Ожидаем загрузки основных элементов
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Парсим закон (44-ФЗ)
            WebElement lawElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("div.cardMainInfo__title.d-flex.text-truncate")
                    )
            );
            String law = lawElement.getText().trim();

            // Парсим номер закупки
            WebElement purchaseNumberElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("span.cardMainInfo__purchaseLink a")
                    )
            );
            String purchaseNumber = purchaseNumberElement.getText().trim();

            // Парсим статус закупки
            WebElement statusElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("span.cardMainInfo__state.distancedText")
                    )
            );
            String procurementStage = statusElement.getText().trim();

            // Создаем объект Purchase и заполняем данные
            Purchase purchase = new Purchase();


            // Здесь можно добавить парсинг других полей по аналогии

            return new ParseResult(url, purchase, null);
        } catch (Exception e) {
            return new ParseResult(url, null, e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    public void stopParsing() {
        isStopped = true;
        shutdown();
    }

    private void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}