package Parser.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChromeDriverLocator {

    private static final String CHROMEDRIVER_FOLDER = "chrome-win64/chromedriver-win64";
    private static final String CHROME_FOLDER       = "chrome-win64";   // папка с chrome.exe

    public static String findDriverPath() {
        File driver = resolveFile(CHROMEDRIVER_FOLDER, "chromedriver.exe");

        System.out.println("[ChromeDriver] Ищу chromedriver.exe в: " + driver.getAbsolutePath());

        if (driver.exists()) {
            System.out.println("[ChromeDriver] ✔ chromedriver.exe найден: " + driver.getAbsolutePath());
            System.out.println("[ChromeDriver] Размер файла: " + driver.length() / 1024 + " KB");
            return driver.getAbsolutePath();
        }

        System.err.println("[ChromeDriver] ✘ chromedriver.exe НЕ найден: " + driver.getAbsolutePath());
        throw new RuntimeException(
                "chromedriver.exe не найден. Ожидается: " + driver.getAbsolutePath()
        );
    }

    public static String findChromeBinaryPath() {
        File chrome = resolveFile(CHROME_FOLDER, "chrome.exe");

        System.out.println("[ChromeDriver] Ищу chrome.exe в: " + chrome.getAbsolutePath());

        if (chrome.exists()) {
            System.out.println("[ChromeDriver] ✔ chrome.exe найден: " + chrome.getAbsolutePath());
            return chrome.getAbsolutePath();
        }

        System.out.println("[ChromeDriver] ⚠ chrome.exe не найден — будет использован системный Chrome");
        return null;
    }

    // Ищет файл сначала на уровень выше проекта (MAGA/), потом внутри проекта
    private static File resolveFile(String folder, String filename) {
        Path projectDir = Paths.get(System.getProperty("user.dir"));
        Path parentDir  = projectDir.getParent(); // C:\Users\Sergey\MAGA\

        if (parentDir != null) {
            File f = parentDir.resolve(folder).resolve(filename).toFile();
            if (f.exists()) return f;
        }

        return projectDir.resolve(folder).resolve(filename).toFile();
    }
}