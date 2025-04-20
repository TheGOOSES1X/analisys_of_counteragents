package Parser.implementations;
import Parser.Database.hooks.HibernateUtil;
import Parser.Database.models.*;
import Parser.interfaces.*;

import java.time.format.DateTimeFormatter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PurchaseParser44 implements Parser {
    private final DriverSetup driverSetup;
    private ExecutorService executor;
    private volatile boolean isStopped;

    @Override
    public void parse() {

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

    public PurchaseParser44(DriverSetup driverSetup) {
        this.driverSetup = driverSetup;
    }

    public void parseUrlsParallel(List<String> urls, Consumer<ParseResult> callback, int threadCount,Consumer<Integer> progressCallback) {
        // 1. Инициализация БД (создание таблиц если их нет)
        initializeDatabase();

        isStopped = false;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CompletionService<ParseResult> completionService = new ExecutorCompletionService<>(executor);

        for (String url : urls) {
            completionService.submit(() -> parseSingleUrl(url));
        }

        // 3. Обработка результатов и сохранение в БД
        for (int i = 0; i < urls.size(); i++) {
            if (isStopped) break;

            try {
                Future<ParseResult> future = completionService.take();
                ParseResult result = future.get();

                // Сохранение в БД в основном потоке
                if (result.purchaseData != null) {
                    saveToDatabase(result);
                }
                callback.accept(result);

                if (progressCallback != null) {
                    progressCallback.accept(i + 1); // +1 потому что i начинается с 0
                }
            } catch (Exception e) {
                callback.accept(new ParseResult(null, null, e));
            }
        }
        // 4. Завершение работы
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

    private void initializeDatabase() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.createQuery("from Purchase where 1=0").list();
        }
    }
    private void saveToDatabase(ParseResult result) {
        if (result.purchaseData == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Purchase purchase = result.purchaseData;
                Customer customer = purchase.getCustomer();
                Contract contract = purchase.getContract();
                // 1. Обработка заказчика
                if (customer != null) {
                    // Пытаемся найти существующего заказчика по fullName
                    Customer existingCustomer = session.createQuery(
                                    "FROM Customer WHERE fullName = :fullName", Customer.class)
                            .setParameter("fullName", customer.getFullName())
                            .uniqueResult();

                    if (existingCustomer != null) {
                        // Используем существующего заказчика
                        purchase.setCustomer(existingCustomer);
                        existingCustomer.getPurchases().add(purchase);
                    } else {
                        // Сохраняем нового заказчика
                        session.persist(customer);
                        customer.getPurchases().add(purchase);
                    }
                }

                if (contract != null && contract.getSupplier() != null) {
                    Supplier supplier = contract.getSupplier();
                    Supplier existingSupplier = session.byNaturalId(Supplier.class)
                            .using("name", supplier.getName())
                            .load();

                    if (existingSupplier != null) {
                        contract.setSupplier(existingSupplier);
                    } else {
                        session.persist(supplier);
                    }
                }

                // 2. Сохраняем связанные с Contract сущности
                if (purchase.getContract() != null) {
                    contract.setPurchase(purchase);

                    // Сохраняем Supplier (если есть)
                    if (contract.getSupplier() != null) {
                        session.persist(contract.getSupplier());
                    }

                    // Сохраняем ProcurementObject (если есть)
                    if (contract.getProcurementObject() != null) {
                        session.persist(contract.getProcurementObject());
                    }

                    session.persist(contract);
                }

                // 3. Сохраняем Purchase (после всех зависимостей)
                session.persist(purchase);

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw new RuntimeException("Failed to save to database", e);
            }
        }
    }

    private ParseResult parseSingleUrl(String url) {
        WebDriver driver = null;
        try {
            if (isStopped) return new ParseResult(url, null, null);

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
            // 1. Загружаем основную страницу закупки
            driver.get(url);
            wait.until(d -> ((JavascriptExecutor)d).executeScript("return document.readyState").equals("complete"));

            // 2. Парсим закупку (до перехода на другие страницы)
            Purchase purchase = parsePurchasePage(url, driver, wait);

            // 3. Парсим заказчика (если есть)
            String customerUrl = extractCustomerUrl(driver);
            Customer customer = null;
            if (customerUrl != null) {
                // Сохраняем текущий URL и куки
                String currentUrl = driver.getCurrentUrl();
                Set<Cookie> cookies = driver.manage().getCookies();

                // Переходим на страницу заказчика
                driver.get(customerUrl);
                customer = parseCustomerPage(customerUrl, driver);

                // Возвращаемся обратно
                driver.get(currentUrl);
                cookies.forEach(cookie -> driver.manage().addCookie(cookie));
                wait.until(d -> ((JavascriptExecutor)d).executeScript("return document.readyState").equals("complete"));
            }

            // 4. Парсим контракт (если есть)
            String contractDraftUrl = findContractDraftUrl(url, driver, wait);
            if (contractDraftUrl != null) {
                // Сохраняем текущий URL и куки
                String currentUrl = driver.getCurrentUrl();
                Set<Cookie> cookies = driver.manage().getCookies();

                Contract contract = new Contract();
                Supplier supplier = new Supplier();
                ProcurementObject procurementObject = new ProcurementObject();

                // Переходим на страницу контракта
                driver.get(contractDraftUrl);
                Map<String, Object> contractDetails = parseContractDraft(contractDraftUrl, driver, wait);
                for (Map.Entry<String, Object> entry : contractDetails.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
                fillContractModel(contract, contractDetails);
                fillSupplierModel(supplier, contractDetails);
                fillProcurementObjectModel(procurementObject, contractDetails);

                contract.setSupplier(supplier);
                contract.setProcurementObject(procurementObject);
                contract.setPurchase(purchase);
                purchase.setContract(contract);
                purchase.setCustomer(customer);
                if (customer != null) {
                    customer.getPurchases().add(purchase); // Добавляем purchase в коллекцию customer
                }

                // Возвращаемся обратно
                driver.get(currentUrl);
                cookies.forEach(cookie -> driver.manage().addCookie(cookie));
            }

            return new ParseResult(url, purchase, null);
        } catch (Exception e) {
            // Делаем скриншот при ошибке

            return new ParseResult(url, null, e);
        }
    }

    //START CONTRACTS

    private void fillSupplierModel(Supplier supplier, Map<String, Object> contractData) {
        try {
            // 2.2. Информация о поставщике
            Map<String, Object> contractParties = (Map<String, Object>) contractData.get("2. Стороны контракта");
            if (contractParties != null) {
                Map<String, String> supplierInfo = (Map<String, String>) contractParties.get("2.2. Информация о поставщике");
                if (supplierInfo != null) {
                    supplier.setType(supplierInfo.get("Вид"));
                    supplier.setName(supplierInfo.get("Наименование организации (ФИО физического лица)"));

                    // Парсим страну (формат: "Российская Федерация (643)")
                    String countryInfo = supplierInfo.get("Наименование страны, код по ОКСМ");
                    if (countryInfo != null && countryInfo.contains("(")) {
                        supplier.setCountryName(countryInfo.substring(0, countryInfo.indexOf("(")).trim());
                        supplier.setCountryCode(countryInfo.substring(countryInfo.indexOf("(") + 1, countryInfo.indexOf(")")).trim());
                    }

                    supplier.setAddress(supplierInfo.get("Адрес места нахождения (адрес места жительства)"));
                    supplier.setPostalAddress(supplierInfo.get("Почтовый адрес"));
                    supplier.setOgrn(supplierInfo.get("ОГРН (для юридических лиц)"));
                    supplier.setInn(supplierInfo.get("ИНН"));
                    supplier.setKpp(supplierInfo.get("КПП (для юридических лиц)"));
                    supplier.setStatus(supplierInfo.get("Статус"));
                    supplier.setEmail(supplierInfo.get("Электронная почта"));
                    supplier.setPhone(supplierInfo.get("Телефон"));

                    // Парсим информацию о руководителе (если есть)
                    String directorInfo = supplierInfo.get("Руководитель (лицо, имеющее право без доверенности действовать от имени юридического лица)");
                    if (directorInfo != null) {
                        // Можно сохранить дополнительную информацию о руководителе
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при заполнении данных поставщика: " + e.getMessage());
        }
    }

    private void fillProcurementObjectModel(ProcurementObject procurementObject,
                                            Map<String, Object> contractData) {
        try {
            Map<String, Object> contractSubject = (Map<String, Object>) contractData.get("3. Предмет контракта");
            if (contractSubject != null) {
                Map<String, Object> procurementObjectInfo = (Map<String, Object>) contractSubject.get("3.1. Объект закупки");
                if (procurementObjectInfo != null) {
                    List<Map<String, String>> objectsTable = (List<Map<String, String>>) procurementObjectInfo.get("objects_table");
                    if (objectsTable != null && !objectsTable.isEmpty()) {
                        Map<String, String> firstObject = objectsTable.get(0);

                        // Находим полные ключи по частичному совпадению
                        String nameKey = findPartialKey(firstObject, "Наименование объекта закупки");
                        String typeKey = findPartialKey(firstObject, "Тип объекта закупки");
                        String ktruKey = findPartialKey(firstObject, "Позиции по КТРУ");
                        String quantityKey = findPartialKey(firstObject, "Количество (объем)");
                        String priceKey = findPartialKey(firstObject, "Цена за единицу");
                        String vatKey = findPartialKey(firstObject, "Ставка НДС");
                        String countryKey = findPartialKey(firstObject, "Страна происхождения");
                        String amountKey = findPartialKey(firstObject, "Сумма");

                        // Заполняем основные поля
                        procurementObject.setName(nameKey != null ?
                                firstObject.get(nameKey) : "Не указано");
                        procurementObject.setType(typeKey != null ?
                                firstObject.get(typeKey) : null);
                        procurementObject.setKtruOkpd2Codes(ktruKey != null ?
                                firstObject.get(ktruKey) : null);

                        // Обработка количества и единицы измерения
                        if (quantityKey != null) {
                            String quantityValue = firstObject.get(quantityKey);
                            if (quantityValue != null) {
                                String[] parts = quantityValue.split(" ", 2);
                                if (parts.length > 0) {
                                    try {
                                        procurementObject.setQuantity(new BigDecimal(parts[0]));
                                    } catch (NumberFormatException e) {
                                        System.out.println("Ошибка парсинга количества: " + quantityValue);
                                    }
                                }
                                if (parts.length > 1) {
                                    procurementObject.setUnit(parts[1].replaceAll("[()]", ""));
                                }
                            }
                        }

                        // Обработка цены
                        if (priceKey != null && firstObject.get(priceKey) != null) {
                            try {
                                procurementObject.setPricePerUnit(new BigDecimal(
                                        firstObject.get(priceKey).replaceAll("[^\\d.]", "")));
                            } catch (NumberFormatException e) {
                                System.out.println("Ошибка парсинга цены: " + firstObject.get(priceKey));
                            }
                        }

                        // Обработка НДС
                        if (vatKey != null) {
                            String vatValue = firstObject.get(vatKey);
                                try {
                                    procurementObject.setVatRate(
                                            vatValue);
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка парсинга НДС: " + vatValue);
                                }
                            }


                        // Страна происхождения
                        procurementObject.setCountryOfOrigin(countryKey != null ?
                                firstObject.get(countryKey) : null);

                        // Сумма
                        if (amountKey != null && firstObject.get(amountKey) != null) {
                            try {
                                procurementObject.setTotalAmount(new BigDecimal(
                                        firstObject.get(amountKey).replaceAll("[^\\d.]", "")));
                            } catch (NumberFormatException e) {
                                System.out.println("Ошибка парсинга суммы: " + firstObject.get(amountKey));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при заполнении данных объекта закупки: " + e.getMessage());
            procurementObject.setName("Не указано");
        }
    }
    private String findPartialKey(Map<String, ?> map, String partialKey) {
        if (map == null) return null;
        for (String key : map.keySet()) {
            if (key.contains(partialKey)) {
                return key;
            }
        }
        return null;
    }

    private String findContractDraftUrl(String originalUrl, WebDriver driver, WebDriverWait wait) {
        try {
            // 1. Извлекаем номер закупки из оригинального URL
            String regNumber = extractRegNumber(originalUrl);
            if (regNumber == null) {
                System.out.println("Не удалось извлечь номер закупки из URL: " + originalUrl);
                return null;
            }

            // 2. Формируем URL черновика контракта напрямую
            String draftContractUrl = originalUrl.replaceAll(
                    "/epz/order/notice/\\w+/view/\\w+-info\\.html\\?regNumber=\\d+",
                    "/epz/order/notice/rpec/contract-draft.html?regNumber=" + regNumber + "0001"
            );

            // 3. Проверяем существование страницы контракта
            driver.get(draftContractUrl);

            // Проверяем, не попали ли мы на страницу "Запрашиваемая страница не существует"
            try {
                WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//td[contains(text(),'Запрашиваемая страница не существует')]")));
                if (errorMessage != null) {
                    System.out.println("Страница контракта не существует: " + draftContractUrl);
                    return null;
                }
            } catch (TimeoutException e) {
                // Ошибка не найдена, значит страница существует
            }

            // 4. Проверяем наличие данных контракта на странице
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.block")));
                System.out.println("Найден черновик контракта: " + draftContractUrl);
                return draftContractUrl;
            } catch (TimeoutException e) {
                System.out.println("Не удалось найти данные контракта на странице: " + draftContractUrl);
                return null;
            }

        } catch (Exception e) {
            System.out.println("Ошибка при поиске контракта: " + e.getMessage());
            return null;
        }
    }

    // Вспомогательный метод для извлечения номера закупки из URL
    private String extractRegNumber(String url) {
        try {
            // Извлекаем номер после regNumber=
            Pattern pattern = Pattern.compile("regNumber=(\\d+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при извлечении номера закупки: " + e.getMessage());
        }
        return null;
    }

    private Map<String, Object> parseContractDraft(String contractDraftUrl, WebDriver driver, WebDriverWait wait) {
        Map<String, Object> contractData = new LinkedHashMap<>();

        try {
            driver.get(contractDraftUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.block")));


            String stateContractId = parseStateContractId(driver);
            if (stateContractId != null) {
                contractData.put("state_contract_id", stateContractId);
            }

            List<WebElement> blocks = driver.findElements(By.cssSelector("div.block"));
            for (WebElement block : blocks) {
                String blockTitle = block.findElement(By.cssSelector("p.block_title")).getText().trim();
                Map<String, Object> blockData = new LinkedHashMap<>();

                if (blockTitle.equals("3. Предмет контракта")) {
                    // Основная информация о предмете контракта
                    blockData.put("3. Предмет контракта", parseSimpleTable(block));

                    // Объекты закупки
                    try {
                        WebElement subBlock = block.findElement(By.xpath(".//div[p[@class='sub-block_title' and contains(text(),'3.1. Объект закупки')]]"));
                        List<Map<String, String>> objectsTable = parseProcurementObjectsTable(subBlock);

                        Map<String, Object> procurementObjects = new LinkedHashMap<>();
                        procurementObjects.put("objects_table", objectsTable);

                        if (!objectsTable.isEmpty()) {
                            Map<String, String> lastRow = objectsTable.get(objectsTable.size() - 1);
                            if (lastRow.containsKey("Итого:")) {
                                procurementObjects.put("total_amount", lastRow.get("Итого:"));
                            }
                        }

                        blockData.put("3.1. Объект закупки", procurementObjects);
                    } catch (Exception e) {
                        System.out.println("Не удалось распарсить таблицу объектов закупки: " + e.getMessage());
                    }
                }
                else if (blockTitle.startsWith("4. Условия контракта")) {
                    // Обработка блока условий контракта
                    List<WebElement> subBlocks = block.findElements(By.cssSelector("div.sub-block"));
                    for (WebElement subBlock : subBlocks) {
                        String subBlockTitle = subBlock.findElement(By.cssSelector("p.sub-block_title")).getText().trim();

                        if (subBlockTitle.startsWith("4.3. Место поставки")) {
                            // Специальная обработка для места поставки
                            blockData.put(subBlockTitle, parseDeliveryPlaceTable(subBlock));
                        } else {
                            // Обычная обработка для других подблоков
                            blockData.put(subBlockTitle, parseSimpleTable(subBlock));
                        }
                    }
                }
                else {
                    // Обработка остальных блоков
                    List<WebElement> subBlocks = block.findElements(By.cssSelector("div.sub-block"));

                    if (!subBlocks.isEmpty()) {
                        for (WebElement subBlock : subBlocks) {
                            String subBlockTitle = subBlock.findElement(By.cssSelector("p.sub-block_title")).getText().trim();
                            blockData.put(subBlockTitle, parseSimpleTable(subBlock));
                        }
                    } else {
                        blockData.putAll(parseSimpleTable(block));
                    }
                }

                contractData.put(blockTitle, blockData);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге черновика контракта: " + e.getMessage());
        }

        return contractData;
    }
    /**
     * Парсит все таблицы в заданном элементе
     */
    private List<Map<String, String>> parseProcurementObjectsTable(WebElement subBlock) {
        List<Map<String, String>> objects = new ArrayList<>();

        try {
            WebElement table = subBlock.findElement(By.cssSelector("table.table-centred-data"));

            // Получаем заголовки из ПЕРВОЙ строки thead (настоящие названия столбцов)
            List<WebElement> headerRows = table.findElements(By.cssSelector("thead tr"));
            if (headerRows.isEmpty()) {
                System.out.println("Не найдены строки заголовков таблицы");
                return objects;
            }

            // Берем первую строку с заголовками (вторая строка с цифрами нам не нужна)
            List<String> headers = headerRows.get(0).findElements(By.tagName("td")).stream()
                    .map(WebElement::getText)
                    .map(text -> text.replaceAll("\\s+", " ").trim())
                    .collect(Collectors.toList());

            // Парсим строки с данными из tbody
            List<WebElement> dataRows = table.findElements(By.cssSelector("tbody tr:not(:last-child)"));
            for (WebElement row : dataRows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() >= headers.size()) {
                    Map<String, String> objectData = new LinkedHashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        String cellText = cells.get(i).getText()
                                .replaceAll("\\s+", " ")
                                .trim();
                        objectData.put(headers.get(i), cellText);
                    }
                    objects.add(objectData);
                }
            }

            // Логирование для отладки
            System.out.println("Заголовки таблицы: " + headers);
            System.out.println("Найдено объектов: " + objects.size());
            for (Map<String, String> obj : objects) {
                System.out.println("Объект: " + obj);
            }

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге таблицы объектов закупки: " + e.getMessage());
            e.printStackTrace();
        }

        return objects;
    }
    private Map<String, String> parseDeliveryPlaceTable(WebElement subBlock) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            WebElement table = subBlock.findElement(By.cssSelector("table.printFormTbl"));
            List<WebElement> headers = table.findElements(By.cssSelector("thead tr td"));
            List<WebElement> dataRows = table.findElements(By.cssSelector("tbody tr"));

            if (!dataRows.isEmpty()) {
                List<WebElement> dataCells = dataRows.get(0).findElements(By.tagName("td"));

                for (int i = 0; i < Math.min(headers.size(), dataCells.size()); i++) {
                    String header = headers.get(i).getText().trim();
                    String value = dataCells.get(i).getText().trim();
                    if (!header.isEmpty() && !value.isEmpty()) {
                        result.put(header, value);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге таблицы места поставки: " + e.getMessage());
        }
        return result;
    }

    // Парсит простую таблицу (ключ-значение)
    private Map<String, String> parseSimpleTable(WebElement element) {
        Map<String, String> tableData = new LinkedHashMap<>();
        try {
            List<WebElement> tables = element.findElements(By.cssSelector("table.printFormTbl"));
            for (WebElement table : tables) {
                List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));
                for (WebElement row : rows) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (cells.size() == 2) {
                        String key = cells.get(0).getText().trim();
                        String value = cells.get(1).getText().trim();
                        if (!key.isEmpty()) {
                            tableData.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге простой таблицы: " + e.getMessage());
        }
        return tableData;
    }

    private String parseStateContractId(WebDriver driver) {
        try {
            WebElement contractIdElement = driver.findElement(By.cssSelector("span.cardMainInfo__purchaseLink.distancedText a"));
            String fullText = contractIdElement.getText().trim();
            // Извлекаем часть после "№ " (номер контракта)
            if (fullText.startsWith("№ ")) {
                return fullText.substring(2).trim();
            }
            return fullText;
        } catch (Exception e) {
            System.out.println("Не удалось извлечь stateContractId: " + e.getMessage());
            return null;
        }
    }
    private String findAndGet(Map<String, String> map, String partialKey) {
        if (map == null) return null;
        String key = findPartialKey(map, partialKey);
        return key != null ? map.get(key) : null;
    }
    private void fillContractModel(Contract contract, Map<String, Object> contractData) {
        try {
            // 3. Предмет контракта
            Map<String, Object> subjectData = (Map<String, Object>) contractData.get("3. Предмет контракта");
            if (subjectData != null) {
                Map<String, String> mainSubject = (Map<String, String>) subjectData.get("3. Предмет контракта");
                if (mainSubject != null) {
                    String subjectKey = findPartialKey(mainSubject, "Предмет контракта");
                    String specKey = findPartialKey(mainSubject, "Специализация");
                    String defenseKey = findPartialKey(mainSubject, "Контракт заключен для выполнения");
                    String lifecycleKey = findPartialKey(mainSubject, "Контракт жизненного цикла");
                    String quantityKey = findPartialKey(mainSubject, "Невозможно определить количество");

                    contract.setSubject(subjectKey != null ? mainSubject.get(subjectKey) : null);
                    contract.setSpecialization(specKey != null ? mainSubject.get(specKey) : null);
                    contract.setDefenseOrder(defenseKey != null ? mainSubject.get(defenseKey) : null);
                    contract.setLifecycleContract(lifecycleKey != null ? mainSubject.get(lifecycleKey) : null);
                    contract.setQuantityUndefined(quantityKey != null ? mainSubject.get(quantityKey) : null);
                }
            }
            contract.setStateContractId((String) contractData.get("state_contract_id"));

            // 1. Номер контракта
            String contractNumberKey = findPartialKey(contractData, "Номер контракта");
            if (contractNumberKey != null) {
                Object contractNumberValue = contractData.get(contractNumberKey);

                if (contractNumberValue instanceof Map) {
                    // Если значение - Map (старая логика)
                    Map<String, Object> contractNumberSection = (Map<String, Object>) contractNumberValue;
                    if (!contractNumberSection.isEmpty()) {
                        Object firstValue = contractNumberSection.values().iterator().next();
                        if (firstValue instanceof Map) {
                            Map<String, String> contractNumberData = (Map<String, String>) firstValue;
                            contract.setContractNumber(contractNumberData.get("Номер контракта"));
                        }
                    }
                } else if (contractNumberValue instanceof String) {
                    // Если значение - просто строка (новая логика)
                    contract.setContractNumber((String) contractNumberValue);
                } else if (contractNumberValue != null) {
                    // Другие случаи - преобразуем в строку
                    contract.setContractNumber(contractNumberValue.toString());
                }
            }

            // 4. Условия контракта
            Map<String, Object> conditionsData = (Map<String, Object>) contractData.get("4. Условия контракта");
            if (conditionsData != null) {
                // 4.1. Сроки исполнения контракта
                Map<String, String> executionTerms = (Map<String, String>) conditionsData.get("4.1. Сроки исполнения контракта");
                if (executionTerms != null) {
                    String startDateKey = findPartialKey(executionTerms, "Дата начала");
                    String endDateKey = findPartialKey(executionTerms, "Дата окончания");

                    contract.setStartDate(startDateKey != null ?
                            parseDate(executionTerms.get(startDateKey)) : null);
                    contract.setEndDate(endDateKey != null ?
                            parseDate(executionTerms.get(endDateKey)) : null);
                }
                // 4.2. Этапы исполнения контракта
                String executionStagesKey = findPartialKey(conditionsData, "Этапы исполнения");
                if (executionStagesKey != null) {
                    Map<String, String> executionStages = (Map<String, String>) conditionsData.get(executionStagesKey);
                    if (executionStages != null && !executionStages.isEmpty()) {
                        // Если мапа не пустая, значит есть этапы, иначе - "Контракт не разделен на этапы"
                        contract.setExecutionStages(executionStages.isEmpty() ?
                                "Контракт не разделен на этапы исполнения контракта" :
                                String.join(", ", executionStages.values()));
                    }
                }

                // 4.3. Место поставки
                Map<String, String> deliveryPlace = (Map<String, String>) conditionsData.get("4.3. Место поставки товара, выполнения работы или оказания услуги");
                if (deliveryPlace != null) {
                    String countryKey = findPartialKey(deliveryPlace, "Страна");
                    String addressKey = findPartialKey(deliveryPlace, "Место");
                    String addInfoKey = findPartialKey(deliveryPlace, "Адрес");

                    contract.setCountry(countryKey != null ? deliveryPlace.get(countryKey) : null);
                    contract.setAddress(addressKey != null ? deliveryPlace.get(addressKey) : null);
                    contract.setAdditionalAddressInfo(addInfoKey != null ?
                            deliveryPlace.get(addInfoKey) : null);
                }

                // 4.4. Требования к гарантии качества
                Map<String, String> qualityGuarantee = (Map<String, String>) conditionsData.get("4.4. Требования к гарантии качества товара, работы, услуги");
                if (qualityGuarantee != null) {
                    String qualReqKey = findPartialKey(qualityGuarantee, "Требования гарантия качества");
                    String warrantyReqKey = findPartialKey(qualityGuarantee, "Информация о требованиях ");
                    String manufReqKey = findPartialKey(qualityGuarantee, "Требования к гарантии ");
                    String warrantyPeriodKey = findPartialKey(qualityGuarantee, "Срок, на который предоставляется гарантия");
                    String warrantyGuarKey = findPartialKey(qualityGuarantee, "Требуется обеспечение исполнения");

                    contract.setQualityGuaranteeRequired(qualReqKey != null ? qualityGuarantee.get(qualReqKey) : null);
                    contract.setWarrantyRequirements(warrantyReqKey != null ? qualityGuarantee.get(warrantyReqKey) : null);
                    contract.setManufacturerWarrantyRequirements(manufReqKey != null ? qualityGuarantee.get(manufReqKey) : null);
                    contract.setWarrantyPeriod(warrantyPeriodKey != null ? qualityGuarantee.get(warrantyPeriodKey) : null);
                    contract.setWarrantyGuaranteeRequired(warrantyGuarKey != null ? qualityGuarantee.get(warrantyGuarKey) : null);
                }

                // 4.6. Условия привлечения субподрядчиков
                Map<String, String> subcontractors = (Map<String, String>) conditionsData.get("4.6.  Условия привлечения субподрядчиков, соисполнителей из числа СМП, СОНО");
                if (subcontractors != null) {
                    String smpReqKey = findPartialKey(subcontractors, "Предъявляется требование о привлечении к исполнению контракта субподрядчиков");
                    String smpExemptKey = findPartialKey(subcontractors, "Объем привлечения к исполнению контракта");
                    String smpPrecentKey = findPartialKey(subcontractors, "За неисполнение условий по привлечению");

                    contract.setSmpSubcontractorsRequired(smpReqKey != null ? subcontractors.get(smpReqKey) : null);
                    contract.setSmpSubcontractorsExempt(smpExemptKey != null ? subcontractors.get(smpExemptKey) : null);
                    contract.setSmpSubcontractorsLiability(smpPrecentKey != null ? subcontractors.get(smpExemptKey) : null);

                }

                // 4.7. Прочие условия
                Map<String, String> otherConditions = (Map<String, String>) conditionsData.get("4.7. Прочие условия контракта");
                if (otherConditions != null) {
                    String terminationKey = findPartialKey(otherConditions, "Предусмотрена возможность");
                    contract.setUnilateralTerminationAllowed(terminationKey != null ? otherConditions.get(terminationKey) : null);
                }
            }

            // 5. Финансирование контракта
            Map<String, Object> financingData = (Map<String, Object>) contractData.get("5. Финансирование контракта");
            if (financingData != null) {
                // 5.1. Источники финансирования
                Map<String, String> fundingSources = (Map<String, String>) financingData.get("5.1. Источники финансирования");
                if (fundingSources != null) {
                    String budgetNameKey = findPartialKey(fundingSources, "Наименование бюджета");
                    String budgetTypeKey = findPartialKey(fundingSources, "Вид бюджета");
                    String municipKey = findPartialKey(fundingSources, "Код территории");
                    String selfFundKey = findPartialKey(fundingSources, "Закупка за счет");
                    String bankSupportKey = findPartialKey(fundingSources, "Информация о банковском");

                    contract.setBudgetName(budgetNameKey != null ? fundingSources.get(budgetNameKey) : null);
                    contract.setBudgetType(budgetTypeKey != null ? fundingSources.get(budgetTypeKey) : null);
                    contract.setMunicipalityCode(municipKey != null ? fundingSources.get(municipKey) : null);
                    contract.setSelfFunded(selfFundKey != null ? fundingSources.get(selfFundKey) : null);
                    contract.setBankingSupportInfo(bankSupportKey != null ? fundingSources.get(bankSupportKey) : null);
                }
                // 5.2. Цена контракта
                String contractPriceKey = findPartialKey(financingData, "Цена контракта");
                if (contractPriceKey != null) {
                    Map<String, String> contractPrice = (Map<String, String>) financingData.get(contractPriceKey);
                    if (contractPrice != null) {
                        contract.setContractRightPrice(
                                parseBigDecimal(findAndGet(contractPrice, "Цена за право заключения")));
                    }
                }

                // 5.2. Цена контракта
                Map<String, String> contractPrice = (Map<String, String>) financingData.get("5.2. Цена контракта");
                if (contractPrice != null) {
                    String priceMethodKey = findPartialKey(contractPrice, "Способ указания цены");
                    String priceKey = findPartialKey(contractPrice, "Цена контракта");
                    String vatKey = findPartialKey(contractPrice, "В том числе НДС");
                    String treasuryKey = findPartialKey(contractPrice, "казначейского обеспечения");
                    String formulaKey = findPartialKey(contractPrice, "Формула цены");
                    String currencyKey = findPartialKey(contractPrice, "Валюта контракта");

                    contract.setPriceIndicationMethod(priceMethodKey != null ? contractPrice.get(priceMethodKey) : null);
                    contract.setContractPrice(priceKey != null ? parseBigDecimal(contractPrice.get(priceKey)) : null);
                    contract.setIncludingVat(vatKey != null ? parseBigDecimal(contractPrice.get(vatKey)) : null);
                    contract.setTreasuryGuaranteeAmount(treasuryKey != null ? parseBigDecimal(contractPrice.get(treasuryKey)) : null);
                    contract.setPriceFormula(formulaKey != null ? contractPrice.get(formulaKey) : null);
                    contract.setCurrency(currencyKey != null ? contractPrice.get(currencyKey) : null);
                }

                // 5.3. Порядок расчетов
                Map<String, String> paymentTerms = (Map<String, String>) financingData.get("5.3. Порядок расчетов");
                if (paymentTerms != null) {
                    String advanceKey = findPartialKey(paymentTerms, "Предусмотрена выплата аванса");
                    String advancePercKey = findPartialKey(paymentTerms, "Размер аванса (%)");
                    String advanceAmtKey = findPartialKey(paymentTerms, "Размер аванса в валюте");
                    String taxDeductKey = findPartialKey(paymentTerms, "Суммы, уплачиваемые заказчиком");
                    String penaltyKey = findPartialKey(paymentTerms, "Предусмотрено удержание");

                    contract.setAdvancePaymentAvailable(advanceKey != null ? paymentTerms.get(advanceKey) : null);
                    contract.setAdvancePercentage(advancePercKey != null ? parseBigDecimal(paymentTerms.get(advancePercKey)) : null);
                    contract.setAdvanceAmount(advanceAmtKey != null ? parseBigDecimal(paymentTerms.get(advanceAmtKey)) : null);
                    contract.setTaxDeductionApplied(taxDeductKey != null ? paymentTerms.get(taxDeductKey) : null);
                    contract.setPenaltyDeductionApplied(penaltyKey != null ? paymentTerms.get(penaltyKey) : null);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при заполнении модели контракта: " + e.getMessage());
            throw new RuntimeException("Не удалось заполнить данные контракта", e);
        }
    }


    private BigDecimal parseBigDecimal(String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) return null;
        try {
            return new BigDecimal(numberStr.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга числа: " + numberStr);
            return null;
        }
    }
    //END CONTRACTS
//START PURCHASE
    private Purchase parsePurchasePage(String url, WebDriver driver, WebDriverWait wait) {
        try {
            Purchase purchase = new Purchase();



            // 1. Парсим основную информацию из верхней части карточки
            parseCardMainInfo(driver, wait, purchase);

            // 2. Собираем все данные со страницы
            Map<String, String> allData = collectAllSectionData(driver);

            // 3. Собираем данные о датах из специального блока
            Map<String, String> dateData = collectDateInfo(driver);

            // 3. Выводим все собранные данные для анализа
//            System.out.println("\n=== ВСЕ ДАННЫЕ СО СТРАНИЦЫ ЗАКУПКИ ===");
//            allData.forEach((key, value) -> System.out.printf("%-50s: %s%n", key, value));
//            System.out.println("=======================================\n");
//
//            System.out.println("\n=== ДАННЫЕ О ДАТАХ ===");
//            dateData.forEach((key, value) -> System.out.printf("%-50s: %s%n", key, value));
//            System.out.println("=======================================\n");

            // 4. Заполняем поля закупки из собранных данных

            if (allData.containsKey("Дата подведения итогов определения поставщика (подрядчика, исполнителя)")) {

                purchase.setAuctionDate(parseDate(allData.get("Дата подведения итогов определения поставщика (подрядчика, исполнителя)")));
            }
            if (allData.containsKey("Начальная (максимальная) цена контракта")) {
                purchase.setInitialMaxPrice(parsePrice(allData.get("Начальная (максимальная) цена контракта")));
            }
            if (allData.containsKey("Способ определения поставщика (подрядчика, исполнителя)")) {
                purchase.setProcurementMethod(allData.get("Способ определения поставщика (подрядчика, исполнителя)"));
            }

            if (allData.containsKey("Валюта")) {
                purchase.setCurrency(allData.get("Валюта"));
            }
            if (allData.containsKey("Идентификационный код закупки (ИКЗ)")) {
                purchase.setIkz(allData.get("Идентификационный код закупки (ИКЗ)"));
            }
            if (dateData.containsKey("Размещено")) {
                purchase.setPublicationDate(parseDate(dateData.get("Размещено")));
            }
            if (dateData.containsKey("Окончание подачи заявок")) {
                purchase.setApplicationEndDate(parseDate(dateData.get("Окончание подачи заявок")));
            }

            if (dateData.containsKey("Обновлено")) {
                purchase.setUpdateDate(parseDateTime(dateData.get("Обновлено")));
            }

            return purchase;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга страницы закупки: " + e.getMessage(), e);
        }
    }

    private Map<String, String> collectDateInfo(WebDriver driver) {
        Map<String, String> dateData = new LinkedHashMap<>();
        try {
            WebElement dateBlock = driver.findElement(By.cssSelector("div.date"));
            List<WebElement> dateSections = dateBlock.findElements(By.cssSelector("div.cardMainInfo__section"));

            for (WebElement section : dateSections) {
                try {
                    String title = section.findElement(By.cssSelector("span.cardMainInfo__title")).getText().trim();
                    String value = section.findElement(By.cssSelector("span.cardMainInfo__content")).getText().trim();

                    if (!title.isEmpty() && !value.isEmpty()) {
                        dateData.put(title, value);
                        System.out.println("Добавлена дата: '" + title + "' = '" + value + "'");
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при обработке секции с датой: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при поиске блока с датами: " + e.getMessage());
        }
        return dateData;
    }
    private void parseCardMainInfo(WebDriver driver, WebDriverWait wait, Purchase purchase) {
        try {
            // 1. Ожидаем загрузки всей секции
            WebElement sectionMainInfo = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.sectionMainInfo")));

            // 2. Парсим закон и тип процедуры
            WebElement titleElement = sectionMainInfo.findElement(
                    By.cssSelector("div.cardMainInfo__title.d-flex.text-truncate"));
            purchase.setLaw(titleElement.getText().trim().split("\\s+")[0]); // Берем только "44-ФЗ"

            // 3. Парсим номер закупки
            WebElement numberElement = sectionMainInfo.findElement(
                    By.cssSelector("span.cardMainInfo__purchaseLink a"));
            purchase.setPurchaseNumber(numberElement.getText().trim());

            // 4. Парсим статус/этап закупки
            WebElement stageElement = sectionMainInfo.findElement(
                    By.cssSelector("span.cardMainInfo__state.distancedText"));
            purchase.setProcurementStage(stageElement.getText().trim());

            // 5. Парсим объект закупки (может отсутствовать)
            try {
                purchase.setPurchaseObject(sectionMainInfo.findElement(
                                By.xpath(".//div[contains(@class,'cardMainInfo__section')]" +
                                        "[.//span[contains(@class,'cardMainInfo__title') and " +
                                        "contains(text(),'Объект закупки')]]"))
                        .findElement(By.cssSelector("span.cardMainInfo__content"))
                        .getText().trim());
            } catch (NoSuchElementException e) {
                System.out.println("Объект закупки не указан");
                purchase.setPurchaseObject(null);
            }

        } catch (Exception e) {
            System.out.println("Критическая ошибка при парсинге основной информации: " + e.getMessage());
            throw new RuntimeException("Не удалось распарсить основную информацию", e);
        }
    }

    // Метод для создания скриншота


    private BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) return null;
        try {
            return new BigDecimal(priceStr.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга цены: " + priceStr);
            return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr.trim().replaceAll("[^\\d.]", ""),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга даты: " + dateStr);
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        try {
            String cleaned = dateTimeStr.trim().replaceAll("[^\\d.:]", " ");
            return LocalDateTime.parse(cleaned,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (Exception e) {
            System.out.println("Ошибка парсинга даты/времени: " + dateTimeStr);
            return null;
        }
    }

    /// END PURCHASE
    /// START CUSTOMER
    private String extractCustomerUrl(WebDriver driver) {
        try {

            List<WebElement> sections = driver.findElements(By.cssSelector("section.blockInfo__section.section"));


            for (WebElement section : sections) {
                try {
                    WebElement title = section.findElement(By.cssSelector("span.section__title"));
                    if ("Размещение осуществляет".equals(title.getText().trim())) {
                        // В найденной секции ищем ссылку
                        WebElement link = section.findElement(By.cssSelector("span.section__info a"));
//                        System.out.println("Нашел ссылку: " + link);
                        return link.getAttribute("href");
                    }
                } catch (Exception e) {


                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println("Не удалось найти ссылку на заказчика: " + e.getMessage());
        }
        return null;
    }
    private Map<String, String> collectAllSectionData(WebDriver driver) {
        Map<String, String> sectionData = new LinkedHashMap<>();
        Map<String, Integer> titleCounts = new HashMap<>();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            List<WebElement> containers = wait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.cssSelector("div.container")));

//            System.out.println("Найдено контейнеров: " + containers.size());


            String[][] selectors = {
                    {"section.blockInfo__section.section", null},  // Прямые секции
                    {"section.blockInfo__section", null},         // Для блока ИКУ
                    {"section", "div.blockInfo__section"}         // Вложенные секции
            };

            for (WebElement container : containers) {
                for (String[] selectorPair : selectors) {
                    String sectionSelector = selectorPair[0];
                    String parentSelector = selectorPair[1];

                    try {
                        List<WebElement> parentElements = parentSelector != null ?
                                container.findElements(By.cssSelector(parentSelector)) :
                                Collections.singletonList(container);

                        for (WebElement parent : parentElements) {
                            try {
                                List<WebElement> sections = parent.findElements(By.cssSelector(sectionSelector));
//                                System.out.println("Найдено секций (" + sectionSelector + " в " +
//                                        (parentSelector != null ? parentSelector : "container") + "): " + sections.size());

                                for (WebElement section : sections) {
                                    try {
                                        List<WebElement> titles = section.findElements(By.cssSelector("span.section__title"));
                                        List<WebElement> infos = section.findElements(By.cssSelector("span.section__info"));

                                        if (!titles.isEmpty() && !infos.isEmpty()) {
                                            String title = titles.get(0).getText().trim();
                                            String info = infos.get(0).getText().trim();

                                            if (!title.isEmpty()) {

                                                String originalTitle = title;
                                                int count = titleCounts.getOrDefault(originalTitle, 0);

                                                if (count > 0) {
                                                    title = originalTitle + " (" + count + ")";
                                                }

                                                titleCounts.put(originalTitle, count + 1);

                                                sectionData.put(title, info);
//                                                System.out.println("Добавлено: '" + title + "' = '" + info + "'");
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Ошибка в секции: " + e.getMessage());
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Ошибка при поиске секций: " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка с родительским элементом: " + e.getMessage());
                    }
                }
            }

            // Дополнительная диагностика
//            System.out.println("\nНайдено заголовков блоков:");
//            driver.findElements(By.cssSelector("h2.blockInfo__title"))
//                    .forEach(h -> System.out.println("- " + h.getText().trim()));

        } catch (Exception e) {
            System.out.println("Ошибка при сборе данных: " + e.getMessage());
        }

//        System.out.println("\n=== ИТОГОВЫЕ ДАННЫЕ ===");
//        sectionData.forEach((k, v) -> System.out.printf("%-40s: %s%n", k, v));
//        System.out.println("=======================");

        return sectionData;
    }

    private Customer parseCustomerPage(String customerUrl, WebDriver driver) {
        if (customerUrl == null) {
            System.out.println("URL заказчика не предоставлен");
            return null;
        }

        try {
            driver.get(customerUrl);

            // Сначала собираем все данные в словарь
            Map<String, String> allData = collectAllSectionData(driver);

            String otherTabUrl = customerUrl + "&tab=other";
            driver.get(otherTabUrl);
//            System.out.println("Новая ссылка :" + otherTabUrl);
            Map<String, String> otherData = collectAllSectionData(driver);

            allData.putAll(otherData);
//            System.out.println("Объединенные данные с обеих вкладок:");
//            allData.forEach((key, value) -> System.out.println(key + ": " + value));

            // Затем создаем объект Customer и заполняем его
            Customer customer = new Customer();
            customer.setLastUpdated(LocalDateTime.now());

            // Заполняем поля из словаря
            customer.setFullName(allData.getOrDefault("Полное наименование", null));
            customer.setShortName(allData.getOrDefault("Сокращенное наименование", null));
            customer.setConsolidatedRegisterCode(allData.getOrDefault("Код по Сводному реестру", null));

            // Обработка дат
            try {
                String regDateStr = allData.get("Дата регистрации");
                if (regDateStr != null) {
                    regDateStr = regDateStr.replaceAll("[^\\d.]", ""); // Очистка от лишних символов
                    customer.setRegistrationDate(LocalDate.parse(regDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты регистрации: " + e.getMessage());
            }

            // Остальные поля...
            customer.setInn(allData.getOrDefault("ИНН", null));
            customer.setKpp(allData.getOrDefault("КПП", null));
            customer.setOgrn(allData.getOrDefault("ОГРН", null));
            customer.setOktmo(allData.getOrDefault("ОКТМО", null));
            customer.setLocation(allData.getOrDefault("Место нахождения", null));


            // ИКУ и дата назначения ИКУ
            customer.setIku(allData.getOrDefault("ИКУ", null));
            try {
                String ikuDateStr = allData.get("Дата присвоения ИКУ");
                if (ikuDateStr != null) {
                    ikuDateStr = ikuDateStr.replaceAll("[^\\d.]", "");
                    customer.setIkuAssignmentDate(LocalDate.parse(ikuDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты присвоения ИКУ: " + e.getMessage());
            }

            // Коды и формы собственности

            customer.setOkfsCode(allData.getOrDefault("Код по ОКФС", null));
            customer.setOwnershipFormName(allData.getOrDefault("Форма собственности", null));
            customer.setOkopfCode(allData.getOrDefault("Код по ОКОПФ", null));
            customer.setLegalFormName(allData.getOrDefault("Организационно-правовая форма", null));

            // Обработка даты регистрации (если еще не добавлено)
            try {
                String regDateStr = allData.get("Дата регистрации");
                if (regDateStr != null) {
                    regDateStr = regDateStr.replaceAll("[^\\d.]", "");
                    customer.setRegistrationDate(LocalDate.parse(regDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты регистрации: " + e.getMessage());
            }
            // Новые поля из предоставленного HTML
            customer.setOrganizationAuthorities(allData.getOrDefault("Полномочия организации", null));
            customer.setUniqueRegistrationNumber(allData.getOrDefault("Уникальный учетный номер организации", null));

            customer.setOrganizationType(allData.getOrDefault("Тип организации", null));
            customer.setOrganizationLevel(allData.getOrDefault("Уровень организации", null));

            // Обработка ОКВЭД (может быть несколько значений)
            String okveds = allData.entrySet().stream()
                    .filter(e -> e.getKey().contains("ОКВЭД"))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.joining("; "));
            customer.setOkved(okveds.isEmpty() ? null : okveds);

            // Уполномоченная организация
            customer.setConsolidatedRegisterCodeAlt(allData.getOrDefault("Код по Сводному реестру", null));
            customer.setAuthorizedOrganizationName(allData.getOrDefault("Наименование", null));

            // Контактная информация
            customer.setPhone(allData.getOrDefault("Телефон", null));
            customer.setFax(allData.getOrDefault("Факс", null));
            customer.setPostalAddress(allData.getOrDefault("Почтовый адрес", null));
            customer.setEmail(allData.getOrDefault("Контактный адрес электронной почты", null));
            customer.setWebsite(allData.getOrDefault("Адрес организации в сети Интернет", null));
            customer.setContactPerson(allData.getOrDefault("Контактное лицо", null));
            customer.setTimeZone(allData.getOrDefault("Часовая зона", null));

            // Обработка дат
            try {
                String taxRegDate = allData.get("Дата постановки организации на учет в налоговом органе");
                if (taxRegDate != null) {
                    customer.setTaxRegistrationDate(LocalDate.parse(
                            taxRegDate.replaceAll("[^\\d.]", ""),
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    ));
                }
            } catch (Exception e) {
                System.out.println("Ошибка парсинга даты налоговой регистрации: " + e.getMessage());
            }


            return customer;

        } catch (Exception e) {
            System.out.println("Ошибка при парсинге страницы заказчика: " + e.getMessage());
            return null;
        }
    }
    /// END CUSTOMER
    public void stopParsing() {
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
}