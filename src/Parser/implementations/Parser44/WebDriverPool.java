package Parser.implementations.Parser44;

import Parser.interfaces.DriverSetup;
import org.openqa.selenium.WebDriver;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class WebDriverPool {
    private final BlockingQueue<WebDriver> availableDrivers;
    private final DriverSetup driverSetup;
    private final int maxDrivers;
    private final Set<WebDriver> allDrivers;

    public WebDriverPool(DriverSetup driverSetup, int maxDrivers) {
        this.driverSetup = driverSetup;
        this.maxDrivers = maxDrivers;
        this.availableDrivers = new LinkedBlockingQueue<>(maxDrivers);
        this.allDrivers = ConcurrentHashMap.newKeySet();
    }

    public WebDriver borrowDriver() throws InterruptedException {
        WebDriver driver = availableDrivers.poll();
        if (driver == null && allDrivers.size() < maxDrivers) {
            driver = driverSetup.setupDriver();
            allDrivers.add(driver);
        } else if (driver == null) {
            driver = availableDrivers.take(); // Блокируем, пока не освободится
        }
        return driver;
    }

    public void returnDriver(WebDriver driver) {
        if (driver != null) {
            driver.manage().deleteAllCookies();
            availableDrivers.offer(driver);
        }
    }

    public void closeAll() {
        allDrivers.forEach(driver -> {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            }
        });
        allDrivers.clear();
        availableDrivers.clear();
    }
}