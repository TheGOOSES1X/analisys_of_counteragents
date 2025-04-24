package Parser.Database.hooks;


import Parser.Database.models.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;

import org.json.JSONObject;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String configPath = "src/config.json"; // Укажите явно путь по умолчанию
    private static boolean isDatabaseInitialized = false;
    public static void initialize(String path) {
        if (path != null) {
            configPath = path;
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Проверка существования файла
                File configFile = new File(configPath);
                if (!configFile.exists()) {
                    throw new FileNotFoundException("Config file not found: " + configPath);
                }

                String content = new String(Files.readAllBytes(configFile.toPath()));
                JSONObject config = new JSONObject(content);

                StandardServiceRegistryBuilder registryBuilder =
                        new StandardServiceRegistryBuilder();

                Map<String, Object> settings = new HashMap<>();
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, String.format("jdbc:postgresql://%s:%s/%s",
                        config.optString("HOST", "localhost"),
                        config.optString("PORT", "5432"),
                        config.getString("DB_Global_Marine")));
                settings.put(Environment.USER, config.getString("USER"));
                settings.put(Environment.PASS, config.getString("PASSWORD"));
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.put(Environment.SHOW_SQL, "true");
                // Только при первом запуске - создаем схему
                if (!isDatabaseInitialized) {
                    settings.put(Environment.HBM2DDL_AUTO, "create");
                    isDatabaseInitialized = true;
                } else {
                    settings.put(Environment.HBM2DDL_AUTO, "validate");  // или "none"
                }

                registryBuilder.applySettings(settings);

                StandardServiceRegistry registry = registryBuilder.build();
                MetadataSources sources = new MetadataSources(registry)
                        .addAnnotatedClass(Parser.Database.models.Customer.class)
                        .addAnnotatedClass(Parser.Database.models.Purchase.class)
                        .addAnnotatedClass(Parser.Database.models.ProcurementObject.class)
                        .addAnnotatedClass(Parser.Database.models.Supplier.class)
                        .addAnnotatedClass(Parser.Database.models.Contract.class);

                Metadata metadata = sources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                System.err.println("Ошибка инициализации Hibernate:");
                e.printStackTrace();
                throw new ExceptionInInitializerError("Failed to initialize Hibernate: " + e.getMessage());
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public static boolean testConnection() {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            try (Session session = sessionFactory.openSession()) {
                // Простая проверка соединения
                session.doWork(connection -> {
                    System.out.println("Connection successful! Database: " + connection.getMetaData().getDatabaseProductName());
                });
                return true;
            }
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }
    public static void printDatabaseSchema() {
        try (Session session = getSessionFactory().openSession()) {
            session.doWork(connection -> {
                DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    System.out.println("Tables in database:");
                    while (tables.next()) {
                        System.out.println("- " + tables.getString("TABLE_NAME"));
                    }
                }
            });
        }
    }

    public static void initializeTestData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Создаем несколько тестовых клиентов
            Customer customer1 = createCustomer(
                    "ООО 'Ромашка'",
                    "Ромашка",
                    "CR001",
                    LocalDate.of(2020, Month.JANUARY, 15),
                    "1234567890",
                    "987654321",
                    "1023456789012",
                    "45345000",
                    "г. Москва, ул. Ленина, д. 1",
                    "IKU0001",
                    LocalDate.of(2020, Month.MARCH, 10),
                    "16",
                    "Коммерческая организация",
                    "12300",
                    "Общество с ограниченной ответственностью"
            );

            Customer customer2 = createCustomer(
                    "АО 'Весна'",
                    "Весна",
                    "CR002",
                    LocalDate.of(2019, Month.MAY, 20),
                    "0987654321",
                    "123456789",
                    "2034567890123",
                    "45346000",
                    "г. Санкт-Петербург, Невский пр-т, д. 100",
                    "IKU0002",
                    LocalDate.of(2019, Month.JULY, 15),
                    "13",
                    "Акционерное общество",
                    "12247",
                    "Публичное акционерное общество"
            );

            session.save(customer1);
            session.save(customer2);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Customer createCustomer(
            String fullName, String shortName, String consolidatedRegisterCode,
            LocalDate registrationDate, String inn, String kpp, String ogrn,
            String oktmo, String location, String iku, LocalDate ikuAssignmentDate,
            String okfsCode, String ownershipFormName, String okopfCode,
            String legalFormName) {

        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setShortName(shortName);
        customer.setConsolidatedRegisterCode(consolidatedRegisterCode);
        customer.setRegistrationDate(registrationDate);
        customer.setLastUpdated(LocalDateTime.now());
        customer.setInn(inn);
        customer.setKpp(kpp);
        customer.setOgrn(ogrn);
        customer.setOktmo(oktmo);
        customer.setLocation(location);
        customer.setIku(iku);
        customer.setIkuAssignmentDate(ikuAssignmentDate);
        customer.setOkfsCode(okfsCode);
        customer.setOwnershipFormName(ownershipFormName);
        customer.setOkopfCode(okopfCode);
        customer.setLegalFormName(legalFormName);

        // Устанавливаем дополнительные поля (необязательные)
        customer.setOrganizationAuthorities("Генеральный директор: Иванов И.И.");
        customer.setUniqueRegistrationNumber("URN001");
        customer.setTaxRegistrationDate(registrationDate.plusDays(10));
        customer.setOrganizationType("Юридическое лицо");
        customer.setOrganizationLevel("Федеральный");
        customer.setOkved("62.01");
        customer.setConsolidatedRegisterCodeAlt(consolidatedRegisterCode + "-ALT");
        customer.setAuthorizedOrganizationName(fullName);
        customer.setPhone("+7 (495) 123-45-67");
        customer.setFax("+7 (495) 123-45-68");
        customer.setPostalAddress(location);
        customer.setEmail("info@" + shortName.toLowerCase() + ".ru");
        customer.setWebsite("www." + shortName.toLowerCase() + ".ru");
        customer.setContactPerson("Иванов Иван Иванович");
        customer.setTimeZone("UTC+3");

        return customer;
    }

}