package Parser_EGRUL;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Data_Extractor {

    private static final Path DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "Parser_EGRUL", "PDF_files");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public interface ProgressUpdater {
        void incrementProgress();
        void updateStatus(String text);
        void defineBarMaximum(int fileNumber);
    }

    public static void readAllInfoFromFiles(Data_Extractor.ProgressUpdater updater) {
        try {
            List<Path> pdfFiles = findAllPdfFiles();
            List<List<Object>> Activity_Data_list = new ArrayList<>();
            List<List<Object>> LiquidBankruptcy_Data_list = new ArrayList<>();
            List<String> INN_list = ConnectToDB_Start.CollectINNFromDB();;

            int countdown = pdfFiles.size();
            updater.defineBarMaximum(countdown);

            for (Path pdfFile : pdfFiles) {
                try{
                    Activity_Data_list.addAll(readMemberINFOFromPDF(pdfFile, INN_list, new ArrayList<>(), 0));
                    List<List<Object>> INFO_PDF = readMemberINFOFromPDF(pdfFile, INN_list, new ArrayList<>(), 1);
                    if (INFO_PDF != null){
                        LiquidBankruptcy_Data_list.addAll(INFO_PDF);
                    }
                }
                catch (Exception e) {
                    continue;
                }
                updater.incrementProgress();
                if ((countdown-1) != 0){
                    updater.updateStatus("Осталось: " + (countdown-1));
                }
                else {
                    updater.updateStatus("");
                }
                System.out.println("Осталось обработать " + countdown + " pdf файлов");
                countdown -= 1;
            }
            ConnectToDB_Finish.writeToPostgres(Activity_Data_list);
            writeToPostgres(LiquidBankruptcy_Data_list);
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файлов: " + e.getMessage());
        }
    }

    private static List<Path> findAllPdfFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(DOWNLOAD_DIR)) {
            return paths
                    .filter(Files::isRegularFile)              // Только файлы (не директории)
                    .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))  // Только PDF
                    .collect(Collectors.toList());              // Собираем в список
        }
    }

    private static List<List<Object>> readMemberINFOFromPDF(Path filePath, List<String> INN_list, List<List<Object>> data_list, int index) throws Exception {
        try (PDDocument pdfDocument = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String pdf_text = stripper.getText(pdfDocument);

            String inn = checkINN(pdf_text, INN_list);

            if (index == 0){
                if (checkOGRNIP(pdf_text) == null){
                    return Arrays.asList(Arrays.asList(inn, null, null, null));
                }
                else if (checkMainActivity(pdf_text) == null){
                    return Arrays.asList(Arrays.asList(inn, null, null, null));
                }
                else {
                    float monthsBetweenNow = getMonthsFromNow(checkMainActivityDate(pdf_text));
                    if (checkEndDate(pdf_text) == null)
                    {
                        data_list.add(Arrays.asList(inn, checkMainActivity(pdf_text), monthsBetweenNow, monthsBetweenNow));
                    }
                    else{
                        float monthsBetween = getMonthsBetweenDates(checkMainActivityDate(pdf_text), checkEndDate(pdf_text));
                        data_list.add(Arrays.asList(inn, checkMainActivity(pdf_text), monthsBetween, monthsBetweenNow));
                    }

                    try {
                        data_list.addAll(checkAndAddDopActivity(pdf_text, inn, filePath));
                    } catch (Exception e) {

                    }
                    return data_list;
                }
            }

            if (index == 1){
                if (checkMainActivity(pdf_text) == null){
                    return null;
                }
                else {
                    if (checkLiquidBankruptcy(pdf_text) != null){
                        data_list.add(Arrays.asList(inn, checkLiquidBankruptcy(pdf_text), 0));
                    }
                    return data_list;
                }
            }

            return null;

        } catch (IOException e) {
            System.err.println("Ошибка при конвертации файла: " + filePath);
            e.printStackTrace();
            throw new Exception();
        } catch (Exception e){
            System.err.println("Ошибка при обработке файла: " + filePath);
            e.printStackTrace();
            data_list = new ArrayList<>();
            return data_list;
        }
    }




















    public static List<List<Object>> checkAndAddDopActivity(String pdf_text, String inn, Path filepath) throws Exception {

        List<List<Object>> activity_list = new ArrayList<>();

        // Находим начальную позицию
        int startIndex = pdf_text.indexOf("Сведения о дополнительных видах деятельности");
        if (startIndex == -1) {
            throw new Exception("Что-то пошло не так!");
        }

        // Работаем с текстом, начиная с найденной фразы
        String relevantText = pdf_text.substring(startIndex);

        // Регулярные выражения для паттернов
        Pattern pattern1 = Pattern.compile("наименование вида деятельности\\s+(\\S+)\\s+[А-ЯЁ]");
        Pattern pattern2 = Pattern.compile("указанные сведения\\s+(\\d+)\\s+(\\d{2}\\.\\d{2}\\.\\d{4})");

        // Списки для результатов
        List<String> keysList = new ArrayList<>();
        List<String> valuesList = new ArrayList<>();

        Matcher matcher1 = pattern1.matcher(relevantText);
        while (matcher1.find()) {
            keysList.add(matcher1.group(1)); // Сохраняем "Искомые символы"
        }

        Matcher matcher2 = pattern2.matcher(relevantText);
        while (matcher2.find()) {
            // Сохраняем число и дату в формате "число дата"
            valuesList.add(matcher2.group(2));
        }

        // Проверяем, что списки одинакового размера
        if (keysList.size() != valuesList.size()) {
//            System.out.println("Предупреждение: количество ключей (" + keysList.size() +
//                    ") не совпадает с количеством значений (" + valuesList.size() + ")");
            // Оставляем минимальное количество элементов
            int minSize = Math.min(keysList.size(), valuesList.size());
            keysList = keysList.subList(0, minSize);
            valuesList = valuesList.subList(0, minSize);
        }

        for (int i = 0; i < keysList.size(); i++){
            float monthsBetweenNow = getMonthsFromNow(valuesList.get(i));
            if (checkEndDate(pdf_text) == null){
                activity_list.add(Arrays.asList(inn, keysList.get(i), monthsBetweenNow/2, monthsBetweenNow));
            }
            else{
                float monthsBetween = getMonthsBetweenDates(valuesList.get(i), checkEndDate(pdf_text));
                activity_list.add(Arrays.asList(inn, keysList.get(i), monthsBetween/2, monthsBetweenNow));
            }
        }

        return activity_list;
    }

    private static String checkLiquidBankruptcy(String text) {
        // Ищем неизменяемый текст, затем число (от 1 до 1000)
        Pattern initialPattern = Pattern.compile("Сведения о состоянии\\s*(?:\\S+\\s*)*?(\\d{1,5})\\b");
        Matcher initialMatcher = initialPattern.matcher(text);

        if (!initialMatcher.find()) {
            return null;
        }

        int startIndex = initialMatcher.end(); // Начинаем поиск после найденного числа
        int nextNumberIndex = text.length();   // По умолчанию ищем до конца текста

        // Ищем следующее число после начального
        Pattern nextNumberPattern = Pattern.compile("\\s(\\d{1,4})\\s");
        Matcher nextNumberMatcher = nextNumberPattern.matcher(text.substring(startIndex));

        if (nextNumberMatcher.find()) {
            nextNumberIndex = startIndex + nextNumberMatcher.start();
        }

        // Вырезаем отрезок для анализа
        String segment = text.substring(startIndex, nextNumberIndex);

        if (segment.replace("\n", " ").contains("Утратил государственную регистрацию")) {
            return "Потеря гос. регистрации";
        } else if (segment.replace("\n", " ").contains("в стадии ликвидации")) {
            return "В стадии ликвидации";
        } else if (segment.replace("\n", " ").contains("(банкрот")) {
            return "Банкротство";
        }

        return null;
    }

    public static void writeToPostgres(List<List<Object>> data) {
        String url = "jdbc:postgresql://192.168.234.237:5432/global_module_238";
        String user = "postgres";
        String password = "globalA17P14";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            if (tableExists(conn, "temp_table")) {
                stmt.executeUpdate("DROP TABLE temp_table");
                System.out.println("Прошлая таблица temp_table удалена.");
            }

            if (tableExists(conn, "criterion_liqbank")) {
                stmt.executeUpdate("DROP TABLE criterion_liqbank");
                System.out.println("Прошлая таблица criterion_liqbank удалена.");
            }

            stmt.executeUpdate(
                    "CREATE TABLE temp_table (" +
                            "inn VARCHAR(30), " +
                            "critical_status VARCHAR(30), " +
                            "coeff FLOAT)");

            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO temp_table (inn, critical_status, coeff) VALUES (?, ?, ?)");

            for (List<Object> row : data) {
                try {
                    String field1 = (row.get(0) != null) ? row.get(0).toString() : "";
                    String field2 = (row.get(1) != null) ? row.get(1).toString() : "";

                    Float field3 = null;
                    if (row.get(2) != null) {
                        String normalizedValue3 = row.get(2).toString().replace(',', '.');
                        field3 = Float.parseFloat(normalizedValue3);
                    }

                    // Установка значений в PreparedStatement
                    pstmt.setString(1, field1);
                    pstmt.setString(2, field2);

                    if (field3 != null) {
                        pstmt.setFloat(3, field3);
                    } else {
                        pstmt.setFloat(3, 0);
                    }

                    pstmt.addBatch();

                } catch (NumberFormatException e) {
                    System.err.println("Ошибка преобразования числа в строке: " + row);
                    // Пропускаем проблемную строку или устанавливаем значения по умолчанию
                    pstmt.setFloat(3, 0);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();

            // Создаем финальную таблицу с вычислением нового поля
            stmt.executeUpdate( "CREATE TABLE criterion_liqbank AS " +
                    "SELECT DISTINCT ON (bs.id) bs.id AS id, t.inn AS inn, t.critical_status AS critical_status, t.coeff AS coefficient " +
                    "FROM temp_table as t " +
                    "JOIN bs_contras as bs ON bs.sinn = t.inn");

            // Добавляем первичный ключ
            stmt.executeUpdate( "ALTER TABLE criterion_liqbank " +
                    "ADD PRIMARY KEY (id)");

            if (tableExists(conn, "temp_table")) {
                stmt.executeUpdate("DROP TABLE temp_table");
                System.out.println("Временная таблица удалена.");
            }

            conn.commit();
            System.out.println("Финальная таблица создана успешно.");

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

    private static String checkOGRNIP(String text){
        Pattern pattern = Pattern.compile(
                "(?:3|4|5|6|7|8|9|10|11|12|13|14)\\s+(?:ОГРН|ОГРНИП)\\s+(.*?)\\s+(?:[1-9]\\d?|100)\\b",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String checkINN(String text, List<String> INN_list) {
        for (String number : INN_list) {
            if (text.contains(number)) {
                return number;
            }
        }
        return null;
    }

    private static String checkMainActivity(String text) {
        Pattern pattern = Pattern.compile("Сведения об основном виде деятельности.*?Код и наименование вида деятельности\\s(.*?)\\s[А-ЯЁ]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
    private static String checkMainActivityDate(String text){
        int startIndex = text.indexOf("Сведения об основном виде деятельности");
        String relevantText = text.substring(startIndex);
        Pattern pattern = Pattern.compile("указанные сведения\\s+(\\d+)\\s+(\\d{2}\\.\\d{2}\\.\\d{4})");
        Matcher matcher = pattern.matcher(relevantText);

        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }
    private static String checkEndDate(String text) {
        Pattern pattern = Pattern.compile("(Дата прекращения деятельности)\\s+(\\d{2}\\.\\d{2}\\.\\d{4})");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }
    public static int getMonthsBetweenDates(String firstDateStr, String secondDateStr) {
        LocalDate firstDate = LocalDate.parse(firstDateStr, DATE_FORMATTER);
        LocalDate secondDate = LocalDate.parse(secondDateStr, DATE_FORMATTER);

        Period period = Period.between(firstDate, secondDate);
        return period.getYears() * 12 + period.getMonths();
    }

    public static int getMonthsFromNow(String firstDateStr) {

        LocalDate firstDate = null;
        firstDate = LocalDate.parse(firstDateStr, DATE_FORMATTER);
        LocalDate today = LocalDate.now();
        Period period = Period.between(firstDate, today);
        return period.getYears() * 12 + period.getMonths();
    }






    private static String checkOGRNIPData(String text){
        Pattern pattern = Pattern.compile("(Дата регистрации\\s+(.*?)\\s+Сведения)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }

    private static String checkTaxStartData(String text){
        Pattern pattern = Pattern.compile("((?:7|8|9|10|11|12|13|14|15|16)\\s+Дата постановки на учет\\s+(.*?)\\s+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }
}