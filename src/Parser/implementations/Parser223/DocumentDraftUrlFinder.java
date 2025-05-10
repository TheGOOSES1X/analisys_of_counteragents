package Parser.implementations.Parser223;

import Parser.Database.models.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class DocumentDraftUrlFinder {
    private static final String DOWNLOAD_DIR = "downloads";
    private static final String PROTOCOL_SECTION = "Протоколы работы комиссии";

    public String findContractDraftUrl(String originalUrl, WebDriver driver, WebDriverWait wait) {
        try {
            String documentsUrl = originalUrl.replace(
                    "common-info.html",
                    "documents.html"
            );
            driver.get(documentsUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.card-attachments-container")));
            return documentsUrl;
        } catch (Exception e) {
            System.out.println("Ошибка при переходе на страницу документов: " + e.getMessage());
            return null;
        }
    }
    public String findDocumentUrl(WebDriver driver, WebDriverWait wait, String sectionName) {
        try {
            WebElement section = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath(String.format("//div[contains(@class, 'card-attachments-container')]//div[contains(text(), '%s')]", sectionName))));

            WebElement downloadLink = section.findElement(By.xpath(
                    ".//following-sibling::div//a[contains(@href, '/download/download.html')]"));

            return downloadLink.getAttribute("href");
        } catch (Exception e) {
            System.out.println("Ошибка при поиске документа в разделе '" + sectionName + "': " + e.getMessage());
            return null;
        }
    }

    public String findProtocolUrl(WebDriver driver, WebDriverWait wait) {
        return findDocumentUrl(driver, wait, PROTOCOL_SECTION);
    }

    public Path downloadDocument(String fileUrl, String fileName) throws IOException {
        createDownloadDirectory();

        URL url = new URL(fileUrl);
        String safeFileName = sanitizeFileName(fileName);
        Path outputPath = Paths.get(DOWNLOAD_DIR, safeFileName);

        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }

        return outputPath;
    }

    private void createDownloadDirectory() throws IOException {
        Path path = Paths.get(DOWNLOAD_DIR);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }

    private static String extractnoticeInfoId(String url) {
        try {
            // Извлекаем номер после regNumber=
            Pattern pattern = Pattern.compile("noticeInfoId=(\\d+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при извлечении номера закупки: " + e.getMessage());
        }
        return null;
    }
}
