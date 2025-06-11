package Parser.implementations;

import Parser.interfaces.PurchaseItem;
import Parser.interfaces.ResultsSaver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TextFileResultsSaver implements ResultsSaver<PurchaseItem> {
    private static final String DEFAULT_OUTPUT_PREFIX = "zakupki_results_";
    private String outputPath;

    public TextFileResultsSaver() {
        this.outputPath = System.getProperty("user.dir");
    }

    @Override
    public void save(Set<PurchaseItem> purchases) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = DEFAULT_OUTPUT_PREFIX + timestamp + ".txt";
        Path filePath = Paths.get(outputPath, filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toString()))) {
            writer.write(String.format("Результаты парсинга закупок (%s)\n", timestamp));
            writer.write(String.format("Всего найдено: %d записей\n\n", purchases.size()));

            for (PurchaseItem item : purchases) {
                writer.write(item.getUrl().toString());
//                writer.write("\n".repeat(2));
//                writer.write("-".repeat(80));
                writer.write("\n\n");
            }

            System.out.println("Результаты сохранены в файл: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении в файл: " + e.getMessage());
        }
    }


    @Override
    public void setOutputPath(String path) {
        this.outputPath = path;
    }

    @Override
    public String getOutputPath() {
        return outputPath;
    }
}