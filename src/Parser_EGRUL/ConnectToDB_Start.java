package Parser_EGRUL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectToDB_Start {
    public static List<String> CollectINNFromDB() {
        // Данные для подключения к БД
        String url = "jdbc:postgresql://192.168.234.237:5432/global_module_238";
        String user = "postgres";
        String password = "globalA17P14";

        List<String> INN_list = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT sinn FROM bs_contras WHERE sinn IS NOT NULL GROUP BY sinn";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    String value = resultSet.getString("sinn");
                    INN_list.add(value);
                }
            }

            return INN_list;

        } catch (SQLException e) {
            System.err.println("Ошибка при работе с базой данных:");
            e.printStackTrace();
            return null;
        }
    }
}
