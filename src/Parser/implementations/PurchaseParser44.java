package Parser.implementations;
import Parser.Database.hooks.HibernateUtil;
import Parser.Database.models.Customer;
import Parser.Database.models.ProcurementObject;
import Parser.interfaces.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import Parser.Database.models.Purchase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void parseUrlsParallel(List<String> urls, Consumer<ParseResult> callback, int threadCount) {
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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (result.purchaseData.getCustomer() != null) {
                    session.persist(result.purchaseData.getCustomer());
                }
                session.persist(result.purchaseData);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) transaction.rollback();
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
            String customerUrl = extractCustomerUrl(driver);
            Customer customer = null;

            if (customerUrl != null) {
                customer = parseCustomerPage(customerUrl, driver);
                driver.get(url); // Возвращаемся на страницу закупки
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("div.cardMainInfo__title.d-flex.text-truncate")));
            }

            Purchase purchase = parsePurchasePage(url, driver, wait, customer);
            return new ParseResult(url, purchase, null);
        } catch (Exception e) {
            return new ParseResult(url, null, e);
        }
    }
//START PURCHASE
    private Purchase parsePurchasePage(String url, WebDriver driver, WebDriverWait wait, Customer customer) {
        try {
            Purchase purchase = new Purchase();
            purchase.setCustomer(customer);


            // 1. Парсим основную информацию из верхней части карточки
            parseCardMainInfo(driver, wait, purchase);

            // 2. Собираем все данные со страницы
            Map<String, String> allData = collectAllSectionData(driver);

            // 3. Собираем данные о датах из специального блока
            Map<String, String> dateData = collectDateInfo(driver);

            // 3. Выводим все собранные данные для анализа
            System.out.println("\n=== ВСЕ ДАННЫЕ СО СТРАНИЦЫ ЗАКУПКИ ===");
            allData.forEach((key, value) -> System.out.printf("%-50s: %s%n", key, value));
            System.out.println("=======================================\n");

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
            // Основная информация в верхней части карточки
            purchase.setLaw(wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.cardMainInfo__title.d-flex.text-truncate"))).getText().trim());

            purchase.setPurchaseNumber(wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("span.cardMainInfo__purchaseLink a"))).getText().trim());

            purchase.setProcurementStage(wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("span.cardMainInfo__state.distancedText"))).getText().trim());

            // Объект закупки (может отсутствовать)
            try {
                purchase.setPurchaseObject(driver.findElement(
                                By.xpath("//div[contains(@class,'cardMainInfo__section')]" +
                                        "[.//span[contains(@class,'cardMainInfo__title') and contains(text(),'Объект закупки')]]"))
                        .findElement(By.cssSelector("span.cardMainInfo__content"))
                        .getText().trim());
            } catch (Exception e) {
                System.out.println("Не найден объект закупки: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Ошибка при парсинге основной информации: " + e.getMessage());
            throw e;
        }
    }

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
    private String extractCustomerUrl(WebDriver driver) {
        try {

            List<WebElement> sections = driver.findElements(By.cssSelector("section.blockInfo__section.section"));


            for (WebElement section : sections) {
                try {
                    WebElement title = section.findElement(By.cssSelector("span.section__title"));
                    if ("Размещение осуществляет".equals(title.getText().trim())) {
                        // В найденной секции ищем ссылку
                        WebElement link = section.findElement(By.cssSelector("span.section__info a"));
                        System.out.println("Нашел ссылку: " + link);
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
            System.out.println("Новая ссылка :" + otherTabUrl);
            Map<String, String> otherData = collectAllSectionData(driver);

            allData.putAll(otherData);
            System.out.println("Объединенные данные с обеих вкладок:");
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