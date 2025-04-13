package Parser.Database.hooks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class DatabaseConnector {
    private String user;
    private String password;
    private String dbName;
    private String Host = "localhost";
    private String Port = "5432";
    private String url;

    public DatabaseConnector(String configPath) {
        try {

            String content = new String(Files.readAllBytes(Paths.get(configPath)));
            JSONObject config = new JSONObject(content);

            this.user = config.getString("USER");
            this.password = config.getString("PASSWORD");


            this.url = String.format("jdbc:postgresql://%s:%s/%s",
                    Host,
                    Port,
                    config.getString("DB_Global_Marine"));

        } catch (Exception e) {
            System.err.println("Ошибка загрузки конфига: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
            return false;
        }
    }
}