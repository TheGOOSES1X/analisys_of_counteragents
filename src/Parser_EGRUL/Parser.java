package Parser_EGRUL;
import Parser.implementations.Parser44.DatabaseService;
import Parser.implementations.ChromeDriverSetup;
import Parser.utils.RandomUserAgent;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Parser {
    private static final Path DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "src", "Parser_EGRUL");
    private static final int TIMEOUT_SECONDS = 20;

    public static void main(String[] args) {
        DatabaseService ds_obj = new DatabaseService();
        List<String> INN_list = ds_obj.getAllSupplierInns();

        String url = "https://egrul.nalog.ru/index.html";
        String InputId = "query";
        String searchButtonId = "btnSearch";
        String endButtonId = "btnReference";

        //Инициализация Chrome Driver
        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");

        String userAgent = RandomUserAgent.getRandomUserAgent();
        if (userAgent != null && !userAgent.isEmpty()) {
            options.addArguments("user-agent=" + userAgent);
        }

        String downloadPath = Paths.get(System.getProperty("user.dir"), "src", "Parser_EGRUL").toString();
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", downloadPath);
        chromePrefs.put("download.prompt_for_download", false);
        chromePrefs.put("download.directory_upgrade", true);
        chromePrefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", chromePrefs);

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        for (String key : INN_list) {
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
                        WebElement getInfoFileButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//div[@id='resultContent']/div[@class='res-row']/div[@class='res-line']/button")
                        ));

                        if (getInfoFileButton == null) {
                            throw new RuntimeException("Элемент getInfoFileButton не найден на странице");
                        }

                        getInfoFileButton = wait.until(ExpectedConditions.elementToBeClickable(getInfoFileButton));
                        getInfoFileButton.click();

                        Thread.sleep(10000);
                        System.out.println("Успешно обработан " + key);
                    } catch (TimeoutException ex) {
                        System.out.println("Ни одна из кнопок не появилась в течение " + TIMEOUT_SECONDS + " секунд для ключа: " + key);
                    }
                }
            } catch (Exception e) {
                System.out.println("Ошибка при обработке " + key + ": " + e.toString());
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }
    }
    //Нет смысла искать в названии совпадение, так как не все названия сделаны таким образом, некоторые просто случайные
//    private static boolean checkDownloadedFiles(String fileName){
//        try {
//            List<Path> matchingFiles = Files.list(DOWNLOAD_DIR)
//                    .filter(path -> {
//                        String name = path.getFileName().toString();
//                        return name.contains(fileName) && name.endsWith(".pdf");
//                    })
//                    .collect(Collectors.toList());
//
//            if (!matchingFiles.isEmpty()) {
//                Path source = matchingFiles.get(0);
//                Path target = DOWNLOAD_DIR.resolve(fileName + ".pdf"); // Добавляем .pdf
//                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("Файл " + target.getFileName() + " успешно загружен и переименован");
//                return true;
//            }
//            else {
//                return false;
//            }
//        } catch (IOException e) {
//            System.err.println("Ошибка при поиске файлов: " + e.getMessage());
//            return false;
//        }
//    }
}