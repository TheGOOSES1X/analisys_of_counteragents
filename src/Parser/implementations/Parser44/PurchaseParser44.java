package Parser.implementations.Parser44;

import Parser.Database.hooks.DatabaseService;
import Parser.Database.models.*;
import Parser.implementations.Parser223.DocumentParser;
import Parser.implementations.Parser223.MainInfoParser223;
import Parser.interfaces.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class PurchaseParser44 implements PurchaseDetailsParser {
    private final DriverSetup driverSetup;
    private final DocumentParser documentParser;
    private ExecutorService executor;
    private volatile boolean isStopped;
    private volatile boolean isPaused;
    private final LitigationParser litigationParser;
    private final PurchasePageParser purchasePageParser;
    private final CustomerPageParser customerPageParser;
    private final ContractPageParser contractPageParser;
    private  final MainInfoParser223 mainInfoParser223;
    private final DatabaseService databaseService;
    private final WebDriverPool driverPool;
    private final SupplierStatusParser supplierStatusParser;
    private static final Path ERROR_URLS_FILE = Paths.get("error_urls.txt");

    public PurchaseParser44(DriverSetup driverSetup) {
        this.driverSetup = driverSetup;
        this.purchasePageParser = new PurchasePageParser();
        this.customerPageParser = new CustomerPageParser();
        this.contractPageParser = new ContractPageParser();
        this.mainInfoParser223 = new MainInfoParser223();
        this.databaseService = new DatabaseService();

        this.litigationParser = new LitigationParser(driverSetup.setupDriver());
        this.supplierStatusParser = new SupplierStatusParser(driverSetup.setupDriver());
        this.documentParser = new DocumentParser();
        this.driverPool = new WebDriverPool(driverSetup, 6);
        // Инициализация файла для ошибок
        try {
            if (!Files.exists(ERROR_URLS_FILE)) {
                Files.createFile(ERROR_URLS_FILE);
            }
        } catch (IOException e) {
            System.err.println("Failed to create error URLs file: " + e.getMessage());
        }

    }



    private WebDriver createDriverWithCleanup() {
        WebDriver driver = driverSetup.setupDriver();
        // Добавляем хук для очистки при завершении работы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception e) {
                    System.err.println("Error while quitting driver: " + e.getMessage());
                }
            }
        }));
        return driver;
    }


    @Override
    public void parseUrlsParallel(List<String> urls, Consumer<ParseResult> callback,
                                  int threadCount, Consumer<Integer> progressCallback) {
        databaseService.initializeDatabase();
        isStopped = false;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CompletionService<ParseResult> completionService = new ExecutorCompletionService<>(executor);

        for (String url : urls) {
            completionService.submit(() -> parseSingleUrl(url));
        }

        for (int i = 0; i < urls.size(); i++) {
            if (isStopped || Thread.currentThread().isInterrupted()) break;

            while(isPaused && !isStopped) {
                try { Thread.sleep(100); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            try {
                Future<ParseResult> future = completionService.take();
                ParseResult result = future.get();

                if (result.purchaseData != null) {
                    databaseService.saveToDatabase(result);
                }
                callback.accept(result);

                if (progressCallback != null) {
                    progressCallback.accept(i + 1);
                }
            } catch (Exception e) {
                callback.accept(new ParseResult(null, null, e));
            }
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private ParseResult parseSingleUrl(String url) {
        WebDriver driver = null;
        try {
            if (isStopped) return new ParseResult(url, null, null);

            while(isPaused && !isStopped) {
                try {
                    synchronized(this) { this.wait(100); }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return new ParseResult(url, null, e);
                }
            }


            driver = driverPool.borrowDriver();  // Используем наш метод с cleanup
            driver.manage().deleteAllCookies(); // Очищаем куки
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            return parsePurchaseUrl(url, driver, wait);
        } catch (Exception e) {
            saveErrorUrl(url); // Сохраняем URL при ошибке
            return new ParseResult(url, null, e);
        } finally {
            if (driver != null) driverPool.returnDriver(driver);;
        }
    }

    private synchronized void saveErrorUrl(String url) {
        try {
            // Открываем файл в режиме добавления (APPEND)
            Files.write(ERROR_URLS_FILE, (url + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to save error URL to file: " + e.getMessage());
        }
    }

    private ParseResult parsePurchaseUrl(String url, WebDriver driver, WebDriverWait wait) {
        try {
            Purchase purchase;
            Customer customer;
            Contract contract;
            List<ProcurementObject> procurementObjects;

            if (url.contains("notice223")) {
                // Обработка для 223-ФЗ
                purchase = mainInfoParser223.parsePurchaseMainInfo(url, driver);
                customer = mainInfoParser223.parsePurchaseCustomer(url, driver);
                contract = mainInfoParser223.parsePurchaseContract(url, driver);
                procurementObjects = mainInfoParser223.parsePurchaseSubjects(url, driver);
                if (procurementObjects != null) {
                    procurementObjects.forEach(purchase::addProcurementObject);
                }
                // Дополнительный парсинг документов, если нужно
                // documentParser.parseDocumentInfo(url, driver, wait);
            } else {
                // Обработка для других типов закупок
                purchase = purchasePageParser.parsePurchasePage(url, driver, wait);
                customer = customerPageParser.parseCustomerInfo(driver);
                contract = contractPageParser.parseContractInfo(url, driver, wait);

            }



            if (contract != null) {
                contract.setPurchase(purchase);
                purchase.setContract(contract);
            }

            if (customer != null) {
                purchase.setCustomer(customer);
                customer.getPurchases().add(purchase);
            }

            return new ParseResult(url, purchase, null);
        } catch (Exception e) {
            return new ParseResult(url, null, e);
        }
    }

    @Override
    public void pauseParser() { isPaused = true; }

    @Override
    public void resumeParser() {
        isPaused = false;
        synchronized(this) { this.notifyAll(); }
    }

    @Override
    public void stopParser() {
        isStopped = true;
        shutdown();
        driverPool.closeAll();
    }

    private void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public List<String> getSupplierInnsFromDatabase() {
        return databaseService.getAllSupplierInns();
    }
    @Override
    public void parseSupplierLitigations() {
        List<String> supplierInns = getSupplierInnsFromDatabase();
        DatabaseService dbService = new DatabaseService();

        for (int i = 0; i < supplierInns.size(); i++) {
            if (isStopped) break;

            while(isPaused && !isStopped) {
                try { Thread.sleep(100); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            try {
                String inn = supplierInns.get(i);
                litigationParser.searchByInn(inn);
                List<JudicialProceeding> proceedings = litigationParser.parseCases();
                dbService.saveJudicialProceedings(proceedings, inn);

            } catch (Exception e) {
                System.err.println("Ошибка при парсинге дел для ИНН " + supplierInns.get(i) + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void parseSupplierStatuses() {
        List<String> supplierInns = getSupplierInnsFromDatabase();
        DatabaseService dbService = new DatabaseService();
        List<SupplierReliability> reliabilities = new ArrayList<>();

        for (int i = 0; i < supplierInns.size(); i++) {
            if (isStopped) break;

            while(isPaused && !isStopped) {
                try { Thread.sleep(100); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            try {
                String inn = supplierInns.get(i);
                List<String> dishonestyLinks = supplierStatusParser.parseSupplierStatuses(Collections.singletonList(inn));

                // Обрабатываем каждую найденную ссылку
                for (String link : dishonestyLinks) {
                    try {
                        SupplierReliability reliability = supplierStatusParser.parseAndPrintDetails(link);
                        if (reliability != null) {
                            reliability.setInn(inn); // Убедимся, что ИНН установлен
                            reliabilities.add(reliability);
                        }
                    } catch (Exception e) {
                        System.err.println("Ошибка при обработке ссылки " + link + " для ИНН " + inn + ": " + e.getMessage());
                    }
                }

                // Периодически сохраняем данные в базу
                if (reliabilities.size() >= 10 || i == supplierInns.size() - 1) {
                    dbService.saveSupplierReliability(reliabilities);
                    reliabilities.clear();
                }

                System.out.println("Processed " + (i + 1) + " of " + supplierInns.size() + " INNs");

            } catch (Exception e) {
                System.err.println("Ошибка при парсинге статусов для ИНН " + supplierInns.get(i) + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    @Override
    public void cleanupDownloadDirectory() {
        Path downloadDir = Paths.get("downloads");

        if (Files.exists(downloadDir)) {
            try {
                Files.walkFileTree(downloadDir, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        // Не удаляем саму папку downloads, только файлы внутри
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Download directory cleaned up successfully");
            } catch (IOException e) {
                System.err.println("Failed to clean up download directory: " + e.getMessage());
            }
        }
    }

    public static class ParseResult {
        public final String url;
        public final Purchase purchaseData;
        public final Exception error;

        public ParseResult(String url, Purchase purchaseData, Exception error) {
            this.url = url;
            this.purchaseData = purchaseData;
            this.error = error;
        }
    }
}