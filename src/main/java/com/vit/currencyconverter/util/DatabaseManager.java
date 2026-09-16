package com.vit.currencyconverter.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private final String url;

    public DatabaseManager(String databasePath) throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        this.url = "jdbc:h2:" + databasePath;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }
}

