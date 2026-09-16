package com.vit.currencyconverter.repository;

import com.vit.currencyconverter.model.ConversionRecord;
import com.vit.currencyconverter.model.ExchangeRate;
import com.vit.currencyconverter.util.DatabaseManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcConversionRepository implements ConversionRepository {
    private final DatabaseManager databaseManager;

    public JdbcConversionRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initialize() throws SQLException {
        String rates = "CREATE TABLE IF NOT EXISTS exchange_rates (currency_code VARCHAR(3) PRIMARY KEY, units_per_usd DECIMAL(20,8) NOT NULL, updated_at VARCHAR(30) NOT NULL)";
        String history = "CREATE TABLE IF NOT EXISTS conversion_history (id IDENTITY PRIMARY KEY, source_amount DECIMAL(20,4), source_currency VARCHAR(3), target_amount DECIMAL(20,4), target_currency VARCHAR(3), applied_rate DECIMAL(20,8), converted_at VARCHAR(30))";
        try (Connection connection = databaseManager.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(rates);
            statement.execute(history);
        }
    }

    public void saveRate(ExchangeRate rate) throws SQLException {
        String sql = "MERGE INTO exchange_rates (currency_code, units_per_usd, updated_at) KEY(currency_code) VALUES (?, ?, ?)";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rate.getCurrencyCode());
            statement.setBigDecimal(2, rate.getUnitsPerUsd());
            statement.setString(3, rate.getUpdatedAt().toString());
            statement.executeUpdate();
        }
    }

    public BigDecimal findRate(String currencyCode) throws SQLException {
        String sql = "SELECT units_per_usd FROM exchange_rates WHERE currency_code = ?";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, currencyCode);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getBigDecimal(1) : null;
            }
        }
    }

    public List<ExchangeRate> findAllRates() throws SQLException {
        List<ExchangeRate> rates = new ArrayList<ExchangeRate>();
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT * FROM exchange_rates ORDER BY currency_code")) {
            while (result.next()) {
                rates.add(new ExchangeRate(result.getString("currency_code"),
                        result.getBigDecimal("units_per_usd"),
                        LocalDateTime.parse(result.getString("updated_at"))));
            }
        }
        return rates;
    }

    public void saveConversion(ConversionRecord record) throws SQLException {
        String sql = "INSERT INTO conversion_history (source_amount, source_currency, target_amount, target_currency, applied_rate, converted_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, record.getSourceAmount());
            statement.setString(2, record.getSourceCurrency());
            statement.setBigDecimal(3, record.getTargetAmount());
            statement.setString(4, record.getTargetCurrency());
            statement.setBigDecimal(5, record.getAppliedRate());
            statement.setString(6, record.getConvertedAt().toString());
            statement.executeUpdate();
        }
    }

    public List<ConversionRecord> findAllConversions() throws SQLException {
        return findConversions(null);
    }

    public List<ConversionRecord> findConversionsByCurrency(String currencyCode) throws SQLException {
        return findConversions(currencyCode.toUpperCase());
    }

    private List<ConversionRecord> findConversions(String currencyCode) throws SQLException {
        List<ConversionRecord> records = new ArrayList<ConversionRecord>();
        String sql = currencyCode == null
                ? "SELECT * FROM conversion_history ORDER BY id DESC"
                : "SELECT * FROM conversion_history WHERE source_currency = ? OR target_currency = ? ORDER BY id DESC";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (currencyCode != null) {
                statement.setString(1, currencyCode);
                statement.setString(2, currencyCode);
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    records.add(new ConversionRecord(result.getLong("id"),
                            result.getBigDecimal("source_amount"), result.getString("source_currency"),
                            result.getBigDecimal("target_amount"), result.getString("target_currency"),
                            result.getBigDecimal("applied_rate"),
                            LocalDateTime.parse(result.getString("converted_at"))));
                }
            }
        }
        return records;
    }
}

