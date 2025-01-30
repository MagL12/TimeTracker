package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * Класс для управления соединением с базой данных PostgreSQL.
 */

public class DatabaseConnection {

    // Константы для настройки подключения к базе данных
    private static final String URL = "jdbc:postgresql://localhost:15432/time_tracker";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

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

        logger.info("HikariCP DataSource initialized successfully.");
    }

    /**
     * Получает соединение с базой данных.
     *
     * @return объект Connection для взаимодействия с базой данных
     * @throws SQLException если произошла ошибка при получении соединения
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            logger.error("DataSource is not initialized.");
            throw new SQLException("DataSource is not initialized.");
        }
        return dataSource.getConnection();
    }


}
