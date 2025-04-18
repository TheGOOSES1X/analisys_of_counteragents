package Parser.Database.hooks;

import java.nio.file.Files;
import java.nio.file.Paths;

import Parser.Database.hooks.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseTest {
    public static void main(String[] args) {
        testDirectJdbcConnection();
        testHibernateConnection();
    }

    public static void testDirectJdbcConnection() {
        try {
            // Чтение конфига
            String content = new String(Files.readAllBytes(Paths.get("src/config.json")));
            JSONObject config = new JSONObject(content);

            // Формирование URL
            String url = String.format("jdbc:postgresql://%s:%s/%s",
                    config.optString("HOST", "localhost"),
                    config.optString("PORT", "5432"),
                    config.getString("DB_Global_Marine"));

            // Подключение
            Connection conn = DriverManager.getConnection(
                    url,
                    config.getString("USER"),
                    config.getString("PASSWORD"));

            System.out.println("Прямое JDBC подключение успешно!");
            conn.close();
        } catch (Exception e) {
            System.err.println("Ошибка прямого JDBC подключения:");
            e.printStackTrace();
        }
    }

    public static void testHibernateConnection() {
        try {
            HibernateUtil.initialize("src/config.json");
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

            try (Session session = sessionFactory.openSession()) {
                System.out.println("Hibernate подключение успешно!");

                // Простой тестовый запрос
                Long count = session.createQuery("select count(*) from Purchase", Long.class)
                        .getSingleResult();
                System.out.println("Записей в таблице Purchase: " + count);
            }
        } catch (Exception e) {
            System.err.println("Ошибка Hibernate подключения:");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}