package Parser_EGRUL;
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

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class Parser {
    private static final Path DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "src", "Parser_EGRUL", "PDF_files");
    private static final int TIMEOUT_SECONDS = 30;
    private static final int THREAD_POOL_SIZE = 6; // Количество потоков
    private static final String url = "https://egrul.nalog.ru/index.html";
    private static final String InputId = "query";
    private static final String searchButtonId = "btnSearch";
    private static final String endButtonId = "btnReference";
    private static final Logger log = LogManager.getLogger(Parser.class);
    private static final String CSV_FILE = "./src/Parser_EGRUL/INN_list.csv";
    private static final Object fileLock = new Object(); // Общий объект для синхронизации

    public interface ProgressUpdater {
        void incrementProgress();
        void updateStatus(String text);
        void defineBarMaximum(int fileNumber);
    }

    public static void asyncEGRULParse(List<String> list, ChromeOptions options, ProgressUpdater updater, AtomicBoolean isStopped) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<?>> futures = new ArrayList<>();

        for (String key : list) {
            if (isStopped.get()) {  // Проверяем флаг
                break;
            }  // Проверяем флаг
            futures.add(executor.submit(() -> {
                try {
                    innEGRULParse(key, options, updater, isStopped);
                } catch (InterruptedException e) {
                    log.error("e: ", e);
                    throw new RuntimeException(e);
                }
            }));
        }

        for (Future<?> future : futures) {
            if (isStopped.get()) {
                break;
            }
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Ошибка в потоке: " + e.getMessage());
            }

        }

        executor.shutdown();


        if (!isStopped.get() && countRemainingUrls() > 1){
            list = Files.readAllLines(Paths.get(CSV_FILE));
            System.out.println("Ссылки ещё остались, продолжаем");
            asyncEGRULParse(list, options, updater, isStopped);
        }

    }

    private static void innEGRULParse(String key, ChromeOptions options, ProgressUpdater updater, AtomicBoolean isStopped) throws InterruptedException {
        if (isStopped.get()) {
            //System.out.println("Остановлено по запросу внутри самого парсинга");
            return;
        }

        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);

            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Внутри WebDriver остановлен");
                throw new InterruptedException();
            }

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

                safeSleep(5000);
                System.out.println("Успешно обработан " + key);

                removeUrlFromCSV(key);
                updater.incrementProgress();
                updater.updateStatus("Осталось: " + countRemainingUrls());
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
                        safeSleep(5000);
                        System.out.println("Успешно обработан: " + key);  // key можно заменить на что-то осмысленное
                        removeUrlFromCSV(key);
                        updater.incrementProgress();
                        updater.updateStatus("Осталось: " + countRemainingUrls());
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
                safeSleep(3000);
            }
        }
    }

    public static void StartParsingEGRUL (AtomicBoolean isStopped, ProgressUpdater updater) throws IOException {
        List<String> INN_list;

        // Проверяем существование файла
        if (Files.exists(Paths.get(CSV_FILE))) {
            try {
                INN_list = Files.readAllLines(Paths.get(CSV_FILE));
                // Если файл пуст, вызываем функцию для получения данных
                if (INN_list.isEmpty()) {
                    System.out.println("Файл существует, но пуст, вызываем функцию...");

                    INN_list = ConnectToDB_Start.CollectINNFromDB();

                    // Записываем полученные данные в файл
                    Files.write(Paths.get(CSV_FILE), INN_list);
                    System.out.println("Данные записаны в существующий файл.");
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
                INN_list = new ArrayList<>(); // Создаём пустой список в случае ошибки
            }
        } else {
            System.out.println("Файл не существует, вызываем функцию...");

            INN_list = ConnectToDB_Start.CollectINNFromDB();

            try {
                // Создаём файл и записываем в него данные
                Files.write(Paths.get(CSV_FILE), INN_list);
                System.out.println("Файл создан и данные записаны.");
            } catch (IOException e) {
                System.err.println("Ошибка при создании файла: " + e.getMessage());
            }
        }

        updater.defineBarMaximum(INN_list.size());
        updater.updateStatus("Осталось: " + INN_list.size());

        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = getChromeOptions();

        asyncEGRULParse(INN_list, options, updater, isStopped);

        System.out.println("Сбор PDF файлов по ИНН завершён");
    }

    private static void removeUrlFromCSV(String url) {
        synchronized (fileLock) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(CSV_FILE))
                        .stream()
                        .filter(line -> !line.trim().equals(url))
                        .toList();
                Files.write(Paths.get(CSV_FILE), lines);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при удалении URL", e);
            }
        }
    }

    public static int countRemainingUrls() {
        try {
            return (int) Files.readAllLines(Paths.get(CSV_FILE))
                    .stream()
                    .filter(line -> !line.trim().isEmpty())
                    .count();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при подсчете URL", e);
        }
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
    public static void safeSleep(long millis) throws InterruptedException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;  // Пробрасываем исключение дальше
        }
    }
}