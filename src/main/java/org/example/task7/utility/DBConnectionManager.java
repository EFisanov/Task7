package org.example.task7.utility;

import org.example.task7.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {
    public static Connection connect() throws SQLException {
        String jdbcUrl = DatabaseConfig.getDbUrl();
        String user = DatabaseConfig.getDbUsername();
        String password = DatabaseConfig.getDbPassword();

        return DriverManager.getConnection(jdbcUrl, user, password);
    }
}