package com.vit.currencyconverter.service;

import com.vit.currencyconverter.exception.InvalidAmountException;
import com.vit.currencyconverter.exception.RateNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class ConversionService {
    private final RateManager rateManager;

    public ConversionService(RateManager rateManager) {
        this.rateManager = rateManager;
    }

    public BigDecimal convert(BigDecimal amount, String fromCode, String toCode)
            throws InvalidAmountException, RateNotFoundException, SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
        BigDecimal fromRate = rateManager.getRate(fromCode);
        BigDecimal toRate = rateManager.getRate(toCode);
        BigDecimal amountInUsd = amount.divide(fromRate, 12, RoundingMode.HALF_UP);
        return amountInUsd.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAppliedRate(String fromCode, String toCode)
            throws RateNotFoundException, SQLException {
        return rateManager.getRate(toCode)
                .divide(rateManager.getRate(fromCode), 8, RoundingMode.HALF_UP);
    }
}

