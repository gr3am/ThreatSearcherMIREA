package Project;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseLogger {
    private static final Logger logger = LogManager.getLogger(DatabaseLogger.class);

    // Параметры подключения к MySQL (измените согласно вашей среде)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mireaprojectlogger?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234567890MP";

    // Метод для записи запроса в базу
    public static void logRequest(String type, String content, String response) {
        String sql = "INSERT INTO request_logs (request_type, content, response) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);
            stmt.setString(2, content);
            stmt.setString(3, response);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Ошибка записи лога в БД", e);
        }
    }
}