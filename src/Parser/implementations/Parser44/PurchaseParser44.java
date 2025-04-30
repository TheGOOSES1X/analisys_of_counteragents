package Parser.implementations.Parser44;

import Parser.Database.hooks.HibernateUtil;
import Parser.Database.models.*;
import Parser.interfaces.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class PurchaseParser44 implements Parser {
    private final DriverSetup driverSetup;
    private ExecutorService executor;
    private volatile boolean isStopped;
    private volatile boolean isPaused;
    private final LitigationParser litigationParser;
    private final PurchasePageParser purchasePageParser;
    private final CustomerPageParser customerPageParser;
    private final ContractPageParser contractPageParser;
    private final DatabaseService databaseService;
    private final SupplierStatusParser supplierStatusParser;


    public PurchaseParser44(DriverSetup driverSetup) {
        this.driverSetup = driverSetup;
        this.purchasePageParser = new PurchasePageParser();
        this.customerPageParser = new CustomerPageParser();
        this.contractPageParser = new ContractPageParser();
        this.databaseService = new DatabaseService();
        this.litigationParser = new LitigationParser(driverSetup.setupDriver());
        this.supplierStatusParser = new SupplierStatusParser(driverSetup.setupDriver());
    }

    @Override
    public void parse() {
        // Реализация если нужна
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

            driver = driverSetup.setupDriver();
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            return parsePurchaseUrl(url, driver, wait);
        } catch (Exception e) {
            return new ParseResult(url, null, e);
        } finally {
            if (driver != null) driver.quit();
        }
    }

    private ParseResult parsePurchaseUrl(String url, WebDriver driver, WebDriverWait wait) {
        try {
            Purchase purchase = purchasePageParser.parsePurchasePage(url, driver, wait);
            Customer customer = customerPageParser.parseCustomerInfo(driver);
            Contract contract = contractPageParser.parseContractInfo(url, driver, wait);

            if (contract != null) {
                contract.setPurchase(purchase);
                purchase.setContract(contract);
            }

            purchase.setCustomer(customer);
            if (customer != null) {
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