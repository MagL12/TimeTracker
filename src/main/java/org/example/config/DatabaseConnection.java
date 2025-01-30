package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс для управления соединением с базой данных PostgreSQL.
 */

public class DatabaseConnection {

    // Константы для настройки подключения к базе данных
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private static HikariDataSource dataSource;
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    static {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new RuntimeException("Environment variables for database connection are not set");
        }
        // Настройка пула соединений с использованием HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    /**
     * Получает соединение с базой данных.
     *
     * @return объект Connection для взаимодействия с базой данных
     * @throws SQLException если произошла ошибка при получении соединения
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized.");
        }
        return dataSource.getConnection();
    }


}
