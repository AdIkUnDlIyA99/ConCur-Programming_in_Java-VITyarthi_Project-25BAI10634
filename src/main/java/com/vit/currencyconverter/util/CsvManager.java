package com.vit.currencyconverter.util;

import com.vit.currencyconverter.model.ConversionRecord;
import com.vit.currencyconverter.service.RateManager;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CsvManager {
    public void importRates(File file, RateManager rateManager) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    rateManager.saveRate(parts[0].trim(), new BigDecimal(parts[1].trim()));
                }
            }
        }
    }

    public void exportHistory(File file, List<ConversionRecord> records) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("id,source_amount,source_currency,target_amount,target_currency,applied_rate,converted_at");
            for (ConversionRecord record : records) {
                writer.println(record.getId() + "," + record.getSourceAmount() + ","
                        + record.getSourceCurrency() + "," + record.getTargetAmount() + ","
                        + record.getTargetCurrency() + "," + record.getAppliedRate() + ","
                        + record.getConvertedAt());
            }
        }
    }
}

