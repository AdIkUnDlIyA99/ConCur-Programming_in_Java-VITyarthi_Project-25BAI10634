package com.vit.currencyconverter.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExchangeRate {
    private final String currencyCode;
    private final BigDecimal unitsPerUsd;
    private final LocalDateTime updatedAt;

    public ExchangeRate(String currencyCode, BigDecimal unitsPerUsd, LocalDateTime updatedAt) {
        this.currencyCode = currencyCode;
        this.unitsPerUsd = unitsPerUsd;
        this.updatedAt = updatedAt;
    }

    public String getCurrencyCode() { return currencyCode; }
    public BigDecimal getUnitsPerUsd() { return unitsPerUsd; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

