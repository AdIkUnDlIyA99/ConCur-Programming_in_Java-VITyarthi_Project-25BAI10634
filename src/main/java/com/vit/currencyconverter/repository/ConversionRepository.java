package com.vit.currencyconverter.repository;

import com.vit.currencyconverter.model.ConversionRecord;
import com.vit.currencyconverter.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ConversionRepository {
    void initialize() throws SQLException;
    void saveRate(ExchangeRate rate) throws SQLException;
    BigDecimal findRate(String currencyCode) throws SQLException;
    List<ExchangeRate> findAllRates() throws SQLException;
    void saveConversion(ConversionRecord record) throws SQLException;
    List<ConversionRecord> findAllConversions() throws SQLException;
    List<ConversionRecord> findConversionsByCurrency(String currencyCode) throws SQLException;
}

