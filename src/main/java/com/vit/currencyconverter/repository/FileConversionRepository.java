package com.vit.currencyconverter.repository;

import com.vit.currencyconverter.model.ConversionRecord;
import com.vit.currencyconverter.model.ExchangeRate;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class FileConversionRepository implements ConversionRepository {
    private final File ratesFile;
    private final File historyFile;
    private final Map<String, ExchangeRate> rates = new TreeMap<String, ExchangeRate>();
    private final List<ConversionRecord> history = new ArrayList<ConversionRecord>();

    public FileConversionRepository(File dataDirectory) {
        dataDirectory.mkdirs();
        this.ratesFile = new File(dataDirectory, "stored-rates.csv");
        this.historyFile = new File(dataDirectory, "conversion-history.csv");
    }

    public synchronized void initialize() throws SQLException {
        try {
            loadRates();
            loadHistory();
        } catch (IOException error) {
            throw new SQLException("Could not read local data files.", error);
        }
    }

    public synchronized void saveRate(ExchangeRate rate) throws SQLException {
        rates.put(rate.getCurrencyCode(), rate);
        writeRates();
    }

    public synchronized BigDecimal findRate(String currencyCode) {
        ExchangeRate rate = rates.get(currencyCode);
        return rate == null ? null : rate.getUnitsPerUsd();
    }

    public synchronized List<ExchangeRate> findAllRates() {
        return new ArrayList<ExchangeRate>(rates.values());
    }

    public synchronized void saveConversion(ConversionRecord record) throws SQLException {
        long nextId = history.isEmpty() ? 1 : history.get(history.size() - 1).getId() + 1;
        history.add(new ConversionRecord(nextId, record.getSourceAmount(), record.getSourceCurrency(),
                record.getTargetAmount(), record.getTargetCurrency(), record.getAppliedRate(), record.getConvertedAt()));
        writeHistory();
    }

    public synchronized List<ConversionRecord> findAllConversions() {
        List<ConversionRecord> result = new ArrayList<ConversionRecord>(history);
        Collections.reverse(result);
        return result;
    }

    public synchronized List<ConversionRecord> findConversionsByCurrency(String currencyCode) {
        List<ConversionRecord> result = new ArrayList<ConversionRecord>();
        for (ConversionRecord record : findAllConversions()) {
            if (record.getSourceCurrency().equals(currencyCode)
                    || record.getTargetCurrency().equals(currencyCode)) result.add(record);
        }
        return result;
    }

    private void loadRates() throws IOException {
        if (!ratesFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(ratesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) rates.put(parts[0], new ExchangeRate(parts[0],
                        new BigDecimal(parts[1]), LocalDateTime.parse(parts[2])));
            }
        }
    }

    private void loadHistory() throws IOException {
        if (!historyFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 7) history.add(new ConversionRecord(Long.parseLong(p[0]),
                        new BigDecimal(p[1]), p[2], new BigDecimal(p[3]), p[4],
                        new BigDecimal(p[5]), LocalDateTime.parse(p[6])));
            }
        }
    }

    private void writeRates() throws SQLException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ratesFile))) {
            for (ExchangeRate rate : rates.values()) writer.println(rate.getCurrencyCode() + ","
                    + rate.getUnitsPerUsd() + "," + rate.getUpdatedAt());
        } catch (IOException error) {
            throw new SQLException("Could not save rates.", error);
        }
    }

    private void writeHistory() throws SQLException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(historyFile))) {
            for (ConversionRecord record : history) writer.println(record.getId() + ","
                    + record.getSourceAmount() + "," + record.getSourceCurrency() + ","
                    + record.getTargetAmount() + "," + record.getTargetCurrency() + ","
                    + record.getAppliedRate() + "," + record.getConvertedAt());
        } catch (IOException error) {
            throw new SQLException("Could not save conversion history.", error);
        }
    }
}
