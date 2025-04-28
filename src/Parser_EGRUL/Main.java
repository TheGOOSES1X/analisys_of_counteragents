package Parser_EGRUL;
import Parser.implementations.Parser44.DatabaseService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {
    private static final String DOWNLOAD_DIR = System.getProperty(".");
    private static final int TIMEOUT_SECONDS = 45;

    public static void main(String[] args) {
        DatabaseService ds_obj = new DatabaseService();
        List<String> INN_list = ds_obj.getAllSupplierInns();
        String test_INN = INN_list.get(0);

        System.out.println(test_INN);

        // Настройка параметров загрузки для Chrome
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", DOWNLOAD_DIR);
        chromePrefs.put("plugins.always_open_pdf_externally", true);

        String test_irl = "https://egrul.nalog.ru/index.html";
        String InputId = "query";
        String searchButtonId = "btnSearch";
        String endButtonId = "btnReference";

        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));

        try {
            driver.get(test_irl);
            String key = test_INN;
            WebElement input = driver.findElement(By.id(InputId));
            input.sendKeys(key);

            WebElement searchButton = driver.findElement(By.id(searchButtonId));
            searchButton.click();

            WebElement endButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(endButtonId)));
            boolean isVisible = isElementTrulyVisible(driver, endButtonId);

            if (isVisible) {
                endButton.click();
                waitForFileDownload(key + ".pdf");

            } else {
                try {
                    WebElement getInfoFileButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[@id='resultContent']/div[@class='res-row']/div[@class='res-line']/button")
                    ));
                    getInfoFileButton.click();
                    waitForFileDownload(key + ".pdf");
                } catch (Exception e) {
                    System.out.println("Кнопка getInfoFileButton не появилась в течение " + TIMEOUT_SECONDS + " секунд");
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            driver.quit();
        }
    }

    // Метод для ожидания загрузки файла
    private static void waitForFileDownload(String fileName) throws InterruptedException {
        Path filePath = Paths.get(DOWNLOAD_DIR, fileName);
        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            if (Files.exists(filePath)) {
                System.out.println("Файл " + fileName + " успешно загружен");
                return;
            }
            Thread.sleep(1000);
            attempts++;
        }

        throw new RuntimeException("Файл " + fileName + " не был загружен в течение " + maxAttempts + " секунд");
    }

    public static boolean isElementTrulyVisible(WebDriver driver, String elementId) {
        try {
            WebElement element = driver.findElement(By.id(elementId));

            // 1. Проверяем, что сам элемент видим
            if (!element.isDisplayed()) {
                return false;
            }

            // 2. Проверяем opacity (если 0 — элемент невидим)
            String opacity = element.getCssValue("opacity");
            if (opacity != null && opacity.equals("0")) {
                return false;
            }

            // 3. Проверяем всех родителей
            WebElement parent = element;
            while (parent != null) {
                String display = parent.getCssValue("display");
                String visibility = parent.getCssValue("visibility");

                if ("none".equals(display) || "hidden".equals(visibility)) {
                    return false;
                }

                try {
                    parent = parent.findElement(By.xpath("..")); // Переход к родителю
                } catch (Exception e) {
                    parent = null; // Достигли корня DOM
                }
            }

            return true;
        } catch (Exception e) {
            return false; // Элемент не найден
        }
    }
}