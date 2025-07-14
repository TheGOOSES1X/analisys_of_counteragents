package Critical_Criteries;

import Parser.utils.RandomUserAgent;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extrem {
    private static final String DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "Critical_Criteries", "CritExtrem").toString();
    private static final String ExtremUrl = "https://www.fedsfm.ru/documents/terrorists-catalog-portal-act";

//    public static void main(String[] args) {
//        ParseData();
//    }

    public static void ParseData() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = getChromeOptions();
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(ExtremUrl);
            List<WebElement> olElement = driver.findElements(By.className("terrorist-list"));
            List<WebElement> liElementsUL = olElement.get(0).findElements(By.tagName("li"));
            List<WebElement> liElementsFL = olElement.get(1).findElements(By.tagName("li"));
            List<List<String>> dataUL = extractDataToMatrix(liElementsUL, new String[]{"ИНН:"},new String[]{"ОГРН:"});
            List<List<String>> dataFL = extractDataToMatrix(liElementsFL, new String[]{"date"},new String[]{"C.","Г.","П.","СТ.","А.","РАЙОН","С.П.","РЕСПУБЛИКА","ОБЛАСТЬ","Д.","ГОР.","СЕЛО","КРАЙ"});
            String ul_tablename = "criterion_extrem_ul";
            String fl_tablename = "criterion_extrem_fl";

//            System.out.println("Result Matrix:");
//            for (List<String> row : dataFL) {
//                System.out.println(row);
//            }

            String CreateULSQL = "CREATE TABLE " + ul_tablename + " (" +
                    "ul_name VARCHAR, " +
                    "inn VARCHAR, " +
                    "ogrn VARCHAR)";

            String InsertULSQL = "INSERT INTO " + ul_tablename + " VALUES (?, ?, ?)";

            String CreateFLSQL = "CREATE TABLE " + fl_tablename + " (" +
                    "fl_fio VARCHAR, " +
                    "date_of_birth DATE, " +
                    "place_of_living VARCHAR)";

            String InsertFLSQL = "INSERT INTO " + fl_tablename + " VALUES (?, ?, ?)";

            WriteToPostgres(dataUL, ul_tablename, CreateULSQL, InsertULSQL, null);
            WriteToPostgres(dataFL, fl_tablename, CreateFLSQL, InsertFLSQL, 2);
        } finally {
            driver.quit();
        }
    }

    public static void WriteToPostgres(List<List<String>> data, String tablename, String CreateTableSQL, String InsertTableSQL, Integer dateIndex) {
        String url = "jdbc:postgresql://10.210.12.212:5432/global_module";
        String user = "postgres";
        String password = "postgres";

        Connection conn = null;
        Statement stmt = null;

        try {

            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();

            if (tableExists(conn, tablename)) {
                stmt.executeUpdate("DROP TABLE " + tablename);
                System.out.println(tablename + " удалена.");
            }

            stmt.executeUpdate(CreateTableSQL);

            try (PreparedStatement pstmt = conn.prepareStatement(InsertTableSQL)) {
                Set<Integer> dateFieldIndexes = dateIndex != null
                        ? Set.of(dateIndex)
                        : Collections.emptySet();

                for (int i = 0; i < data.size(); i++) {
                    List<String> row = data.get(i);
                    for (int j = 0; j < row.size(); j++) {
                        int sqlParamIndex = j + 1;

                        if (dateFieldIndexes.contains(sqlParamIndex)) {
                            String dateStr = row.get(j);
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                                pstmt.setDate(sqlParamIndex, Date.valueOf(localDate));
                            } catch (Exception e) {
                                pstmt.setNull(sqlParamIndex, Types.DATE);
                            }
                        } else {
                            pstmt.setString(sqlParamIndex, row.get(j));
                        }
                    }
                    pstmt.executeUpdate();
                }
            }

            System.out.println(tablename + " успешно загружена");

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате: " + ex.getMessage());
            }
            System.err.println("Ошибка SQL: " + e.getMessage());
        } catch (NumberFormatException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате: " + ex.getMessage());
            }
            System.err.println("Ошибка преобразования числа: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии: " + e.getMessage());
            }
        }
    }

    private static List<List<String>> extractDataToMatrix(List<WebElement> liElements, String[] key1, String[] key2) {
        List<List<String>> matrix = new ArrayList<>();

        for (WebElement li : liElements) {
            String textContent = li.getAttribute("textContent");
            String[] parts = textContent.split(",");
            List<String> row = new ArrayList<>(3);

            String firstPart = parts.length > 0 ? cleanFirstPart(parts[0]) : "";
            row.add(firstPart);

            String value2;
            if (containsDateKey(key1)) {
                value2 = extractFirstDate(textContent);
            } else {
                value2 = findAnyValue(parts, key1);
            }
            row.add(value2);

            String value3;
            if (containsDateKey(key2)) {
                value3 = extractFirstDate(textContent);
            } else {
                value3 = findAnyValue(parts, key2);
            }
            row.add(value3);

            matrix.add(row);
        }

        return matrix;
    }

    private static boolean containsDateKey(String[] keys) {
        for (String key : keys) {
            if ("date".equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    private static String cleanFirstPart(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String cleaned = text.replaceAll("^[\\d.]+\\s*", "");
        cleaned = cleaned.replaceAll("[^a-zA-Zа-яА-Я0-9\\s]", "");

        return cleaned.trim();
    }

    private static String extractFirstDate(String text) {
        Pattern pattern = Pattern.compile("(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[0-2])\\.(\\d{4})");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : "";
    }

    private static String findAnyValue(String[] parts, String[] keys) {
        for (String part : parts) {
            String trimmedPart = part.trim();
            for (String key : keys) {
                if (trimmedPart.contains(key)) {
                    if (key.equals("ИНН:") || key.equals("ОГРН:")) {
                        trimmedPart = trimmedPart.replace(key, "").trim();
                    }
                    trimmedPart = trimmedPart.replace(";", "").trim();
                    return trimmedPart;
                }
            }
        }
        return "";
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(
                null, null, tableName, null)) {
            return rs.next();
        }
    }

    public static List<List<String>> readXlsx(String filePath) {
        List<List<String>> table = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    rowData.add(cell.toString());
                }
                table.add(rowData);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        table.subList(0, 2).clear();

        return table;
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
