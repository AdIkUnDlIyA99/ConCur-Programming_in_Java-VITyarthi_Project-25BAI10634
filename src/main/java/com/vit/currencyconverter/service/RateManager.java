package com.vit.currencyconverter.service;

import com.vit.currencyconverter.exception.RateNotFoundException;
import com.vit.currencyconverter.model.ExchangeRate;
import com.vit.currencyconverter.repository.ConversionRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RateManager {
    private final ConversionRepository repository;

    public RateManager(ConversionRepository repository) {
        this.repository = repository;
    }

    public synchronized BigDecimal getRate(String currencyCode)
            throws SQLException, RateNotFoundException {
        BigDecimal rate = repository.findRate(currencyCode.toUpperCase());
        if (rate == null) {
            throw new RateNotFoundException("No exchange rate is configured for " + currencyCode.toUpperCase());
        }
        return rate;
    }

    public synchronized void saveRate(String currencyCode, BigDecimal unitsPerUsd) throws SQLException {
        if (unitsPerUsd == null || unitsPerUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be greater than zero.");
        }
        repository.saveRate(new ExchangeRate(currencyCode.toUpperCase(), unitsPerUsd, LocalDateTime.now()));
    }

    public synchronized List<ExchangeRate> getAllRates() throws SQLException {
        return repository.findAllRates();
    }
}

