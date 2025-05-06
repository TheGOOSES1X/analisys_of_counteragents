package Parser_EGRUL;
import Parser.implementations.Parser44.DatabaseService;
import Parser.utils.RandomUserAgent;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;


public class Parser {
    private static final Path DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "src", "Parser_EGRUL");
    private static final int TIMEOUT_SECONDS = 20;
    private static final int THREAD_POOL_SIZE = 3; // Количество потоков
    private static final String url = "https://egrul.nalog.ru/index.html";
    private static final String InputId = "query";
    private static final String searchButtonId = "btnSearch";
    private static final String endButtonId = "btnReference";
    private static final Logger log = LogManager.getLogger(Parser.class);

    public static void asyncEGRULParse(List<String> list, ChromeOptions options) {
        // Создаем пул потоков
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        // Создаем список Future для отслеживания результатов
        List<Future<?>> futures = new ArrayList<>();
        // Разделяем список на части для каждого потока
        for (String key : list) {
            futures.add(executor.submit(() -> {
                try {
                    innEGRULParse(key, options);
                } catch (InterruptedException e) {
                    log.error("e: ", e);
                    throw new RuntimeException(e);
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Ошибка в потоке: " + e.getMessage());
            }
        }

        executor.shutdown();
    }

    private static void innEGRULParse(String key, ChromeOptions options) throws InterruptedException {
        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));

            driver.get(url);

            WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(InputId)));
            input.clear();
            input.sendKeys(key);

            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(searchButtonId)));
            searchButton.click();

            try {
                WebElement endButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(endButtonId)));
                if (endButton == null) {
                    throw new RuntimeException("Элемент endButton не найден на странице");
                }
                endButton = wait.until(ExpectedConditions.elementToBeClickable(endButton));
                endButton.click();
                Thread.sleep(10000);
                System.out.println("Успешно обработан " + key);
            } catch (TimeoutException e) {
                try {
                    List<WebElement> infoFileButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//div[@id='resultContent']/div[@class='res-row']/div[@class='res-line']/button")
                    ));

                    if (infoFileButtons.isEmpty()) {
                        throw new RuntimeException("getInfoFileButton не найдены на странице");
                    }

                    for (WebElement button : infoFileButtons) {
                        WebElement clickableButton = wait.until(ExpectedConditions.elementToBeClickable(button));
                        clickableButton.click();
                        Thread.sleep(5000);
                        System.out.println("Успешно обработан: " + key);  // key можно заменить на что-то осмысленное
                    }
                } catch (TimeoutException ex) {
                    System.out.println("Ни одна из кнопок не появилась в течение " + TIMEOUT_SECONDS + " секунд для: " + key);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обработке " + key + ": " + e);
        } finally {
            if (driver != null) {
                driver.quit();
                Thread.sleep(3000);
            }
        }
    }

    public static void main(String[] args) {
        DatabaseService ds_obj = new DatabaseService();
        List<String> INN_list = ds_obj.getAllSupplierInns();

        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = getChromeOptions();

        asyncEGRULParse(INN_list, options);
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-features=ChromeWhatsNewUI");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--safebrowsing-disable-download-protection");
        options.addArguments("--safebrowsing-disable-extension-blacklist");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");

        String userAgent = RandomUserAgent.getRandomUserAgent();
        if (userAgent != null && !userAgent.isEmpty()) {
            options.addArguments("user-agent=" + userAgent);
        }

        String downloadPath = DOWNLOAD_DIR.toString();
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", downloadPath);
        chromePrefs.put("download.prompt_for_download", false);
        chromePrefs.put("download.directory_upgrade", true);
        options.setExperimentalOption("prefs", chromePrefs);

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        return options;
    }
}