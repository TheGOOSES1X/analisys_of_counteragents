package Parser_EGRUL;

import Parser.implementations.Parser44.DatabaseService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import Parser_EGRUL.EGRUL_member;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Data_Extractor {

    private static final Path DOWNLOAD_DIR = Paths.get(System.getProperty("user.dir"), "src", "Parser_EGRUL");

    public static void main(String[] args) {
        readAllInfoFromFiles();
    }

    public static void readAllInfoFromFiles() {
        try {
            List<Path> pdfFiles = findAllPdfFiles();
            List<EGRUL_member> EGRUL_list = null;

            for (Path pdfFile : pdfFiles) {
                EGRUL_list.add(readMemberINFOFromPDF(pdfFile));
            }

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

    private static EGRUL_member readMemberINFOFromPDF(Path filePath) {
        try {
            DatabaseService ds_obj = new DatabaseService();
            List<String> INN_list = ds_obj.getAllSupplierInns();

            PDDocument pdfDocument = PDDocument.load(filePath.toFile());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdfDocument);

            if (checkOGRNIP(text) != null){
                return new EGRUL_member(checkINN(text, INN_list), checkOGRNIP(text), true, checkOGRNIPData(text), checkTaxStartData(text), checkMainActivity(text));
            }
            else{
                return new EGRUL_member(checkINN(text, INN_list), null, false, null, null, null);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при конвертации файла: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    private static String checkOGRNIP(String text){
        Pattern pattern = Pattern.compile(
                "(?:5|6|7|8|9|10)\\s+ОГРНИП\\s+(.*?)\\s+(?:[1-9]\\d?|100)\\b",
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

    private static String checkMainActivity(String text) {
        Pattern pattern = Pattern.compile("(Сведения об основном виде деятельности\\s+(?:[1-9]\\d?|100)\\s+Код и наименование вида деятельности\\s+(.*?)\\s+[А-ЯЁ])");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(2);
        } else {
            return null;
        }
    }
}