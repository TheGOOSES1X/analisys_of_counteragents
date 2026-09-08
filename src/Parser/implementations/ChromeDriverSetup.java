package Parser.implementations;
import Parser.interfaces.DriverSetup;
import Parser.utils.ChromeDriverLocator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ChromeDriverSetup implements DriverSetup {
    private final String userAgent;

    public ChromeDriverSetup(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public WebDriver setupDriver() {
        System.out.println("[ChromeDriver] ========== Инициализация ChromeDriver ==========");
        System.out.println("[ChromeDriver] Рабочая директория: " + System.getProperty("user.dir"));

        System.setProperty("webdriver.chrome.silentOutput", "true");

        String driverPath = ChromeDriverLocator.findDriverPath();
        System.setProperty("webdriver.chrome.driver", driverPath);
        System.out.println("[ChromeDriver] webdriver.chrome.driver → " + driverPath);

        ChromeOptions options = new ChromeOptions();

        String chromeBinary = ChromeDriverLocator.findChromeBinaryPath();
        if (chromeBinary != null) {
            options.setBinary(chromeBinary);
            System.out.println("[ChromeDriver] Бинарник Chrome → " + chromeBinary);
        }

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--headless");
        options.setAcceptInsecureCerts(true);
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.addArguments("--allow-insecure-localhost");
        options.addArguments("--window-size=1920,1080");
        options.setExperimentalOption("detach", false);

        if (userAgent != null && !userAgent.isEmpty()) {
            options.addArguments("user-agent=" + userAgent);
            System.out.println("[ChromeDriver] User-Agent → " + userAgent);
        }

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        System.out.println("[ChromeDriver] Запускаю браузер...");
        WebDriver driver = new ChromeDriver(options);
        System.out.println("[ChromeDriver] ✔ Браузер успешно запущен");
        System.out.println("[ChromeDriver] ================================================");

        return driver;
    }
}