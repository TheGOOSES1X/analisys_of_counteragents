package Critical_Criteries;

import Parser.utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InAgent {
    private static final String DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "src", "Critical_Criteries", "CritInAgent").toString();
    private static final String InAgentUrl = "https://www.minjust.gov.ru/ru/pages/reestr-inostryannykh-agentov/";
    private static final String targetId = "registry_download_xls";
    private static final String filePath = DOWNLOAD_DIR + "/export.xlsx";

    public static void ParserInAgent() {
        ParseData();
        WriteInAgentToPostgres();
    }

    public static void ParseData() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = getChromeOptions();
        WebDriver driver = new ChromeDriver(options);

        File inagent_file = new File(filePath);
        if (inagent_file.exists()) {
            if (inagent_file.delete()) {
            } else {
            }
        }

        try {
            driver.get(InAgentUrl);
            driver.findElement(By.id(targetId)).click();

            Thread.sleep(5000);
            System.out.println("Файл данных успешно загружен в: " + DOWNLOAD_DIR);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public static void WriteInAgentToPostgres() {
        String url = "jdbc:postgresql://192.168.234.237:5432/global_module_238";
        String user = "postgres";
        String password = "globalA17P14";

        List<List<String>> data = readXlsx(filePath);
        Connection conn = null;
        Statement stmt = null;

        try {

            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();

            if (tableExists(conn, "criterion_inagent")) {
                stmt.executeUpdate("DROP TABLE criterion_inagent");
                System.out.println("criterion_inagent успешно удалена.");
            }

            stmt.executeUpdate(
                    "CREATE TABLE criterion_inagent (" +
                            "id VARCHAR, " +
                            "agent_name VARCHAR, " +
                            "low_article VARCHAR, " +
                            "insertion_date DATE, " +
                            "exclusion_date DATE, " +
                            "resources_urls VARCHAR, " +
                            "type_of_agent VARCHAR, " +
                            "registration_number VARCHAR, " +
                            "inn VARCHAR, " +
                            "ogrn VARCHAR, " +
                            "snils VARCHAR, " +
                            "birth_date DATE, " +
                            "participants_names VARCHAR, " +
                            "address VARCHAR, " +
                            "publication_of_insertion_date DATE, " +
                            "special_account_number VARCHAR, " +
                            "bank_name_and_address VARCHAR, " +
                            "authorized_bank_code VARCHAR, " +
                            "authorized_bank_correspondent_account_number VARCHAR, " +
                            "special_account_opening_date DATE, " +
                            "bank_account_agreement_date DATE)");

            String insertSQL = "INSERT INTO criterion_inagent VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                Set<Integer> dateFieldIndexes = Set.of(4, 5, 12, 15, 20, 21);

                for (int i = 1; i < data.size(); i++) {
                    List<String> row = data.get(i);
                    for (int j = 0; j < row.size(); j++) {
                        int sqlParamIndex = j + 1;

                        if (dateFieldIndexes.contains(sqlParamIndex)) {
                            String dateStr = row.get(j);
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                                pstmt.setDate(sqlParamIndex, java.sql.Date.valueOf(localDate));
                            } catch (Exception e) {
                                pstmt.setNull(sqlParamIndex, Types.DATE);
                            }
                        } else {
                            pstmt.setString(sqlParamIndex, row.get(j));
                        }
                    }
                    pstmt.executeUpdate();
                }
                System.out.println("criterion_inagent успешно загружена!");
            }

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
