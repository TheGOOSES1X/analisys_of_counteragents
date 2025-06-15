package Parser_EGRUL;
import java.sql.*;
import java.util.List;

public class ConnectToDB_Finish {
    public static void writeToPostgres(List<List<Object>> data) {
        String url = "jdbc:postgresql://192.168.234.237:5432/global_module_238";
        String user = "postgres";
        String password = "globalA17P14";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            // 1. Обработка временной таблицы
            if (tableExists(conn, "temp_table")) {
                stmt.executeUpdate("DROP TABLE temp_table");
                System.out.println("Временная таблица temp_table удалена.");
            }

            // Создаем временную таблицу с 4 полями
            stmt.executeUpdate(
                    "CREATE TABLE temp_table (" +
                            "field1 VARCHAR(30), " +
                            "field2 VARCHAR(30), " +
                            "field3 FLOAT, " +
                            "field4 FLOAT)");

            // 2. Вставка данных с нормализацией чисел
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO temp_table (field1, field2, field3, field4) VALUES (?, ?, ?, ?)");

            for (List<Object> row : data) {
                try {
                    // Обработка первого поля (обязательное)
                    String field1 = (row.get(0) != null) ? row.get(0).toString() : "";

                    // Обработка второго поля (обязательное)
                    String field2 = (row.get(1) != null) ? row.get(1).toString() : "";

                    // Обработка третьего поля (числовое)
                    Float field3 = null;
                    if (row.get(2) != null) {
                        String normalizedValue3 = row.get(2).toString().replace(',', '.');
                        field3 = Float.parseFloat(normalizedValue3);
                    }

                    // Обработка четвертого поля (числовое)
                    Float field4 = null;
                    if (row.get(3) != null) {
                        String normalizedValue4 = row.get(3).toString().replace(',', '.');
                        field4 = Float.parseFloat(normalizedValue4);
                    }

                    // Установка значений в PreparedStatement
                    pstmt.setString(1, field1);
                    pstmt.setString(2, field2);

                    if (field3 != null) {
                        pstmt.setFloat(3, field3);
                    } else {
                        pstmt.setFloat(3, 0);
                    }

                    if (field4 != null) {
                        pstmt.setFloat(4, field4);
                    } else {
                        pstmt.setFloat(4, 1);
                    }

                    pstmt.addBatch();

                } catch (NumberFormatException e) {
                    System.err.println("Ошибка преобразования числа в строке: " + row);
                    // Пропускаем проблемную строку или устанавливаем значения по умолчанию
                    pstmt.setFloat(3, 0);
                    pstmt.setFloat(4, 1);
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();

            // 3. Обработка промежуточной таблицы
            if (tableExists(conn, "intermediate_table")) {
                stmt.executeUpdate("DROP TABLE intermediate_table");
                System.out.println("Промежуточная таблица intermediate_table удалена.");
            }

            // Создаем промежуточную таблицу с агрегированными данными
            stmt.executeUpdate(
                    "CREATE TABLE intermediate_table AS " +
                            "SELECT t.field1, t.field2, " +
                            "SUM(t.field3) AS sum_field3, " +
                            "m.max_field4 AS max_field4 " +
                            "FROM temp_table t " +
                            "JOIN (SELECT field1, MAX(field4) AS max_field4 " +
                            "FROM temp_table " +
                            "GROUP BY field1) m " +
                            "ON t.field1 = m.field1 " +
                            "GROUP BY t.field1, t.field2, m.max_field4");

            // 4. Обработка финальной таблицы
            if (tableExists(conn, "criterion_fns")) {
                stmt.executeUpdate("DROP TABLE criterion_fns");
                System.out.println("Таблица criterion_fns удалена.");
            }

            // Создаем финальную таблицу с вычислением нового поля
            stmt.executeUpdate(
                    "CREATE TABLE criterion_fns AS " +
                            "SELECT field1 AS inn, field2 AS activity_code, " +
                            "(sum_field3 / max_field4) AS result_value " +
                            "FROM intermediate_table");

            // Добавляем первичный ключ
            stmt.executeUpdate(
                    "ALTER TABLE criterion_fns " +
                            "ADD PRIMARY KEY (inn, activity_code)");

            // 5. Удаление временных таблиц
            if (tableExists(conn, "temp_table")) {
                stmt.executeUpdate("DROP TABLE temp_table");
                System.out.println("Временная таблица удалена.");
            }
            if (tableExists(conn, "intermediate_table")) {
                stmt.executeUpdate("DROP TABLE intermediate_table");
                System.out.println("Промежуточная таблица удалена.");
            }

            conn.commit();
            System.out.println("Финальная таблица создана успешно.");

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате: " + ex.getMessage());
            }
            System.err.println("Ошибка SQL: " + e.getMessage());
        } catch (NumberFormatException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Ошибка при откате: " + ex.getMessage());
            }
            System.err.println("Ошибка преобразования числа: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии: " + e.getMessage());
            }
        }
    }

    // Универсальная проверка существования таблицы
    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(
                null, null, tableName, null)) {
            return rs.next();
        }
    }
}
