package com.vit.currencyconverter.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConversionRecord {
    private final long id;
    private final BigDecimal sourceAmount;
    private final String sourceCurrency;
    private final BigDecimal targetAmount;
    private final String targetCurrency;
    private final BigDecimal appliedRate;
    private final LocalDateTime convertedAt;

    public ConversionRecord(long id, BigDecimal sourceAmount, String sourceCurrency,
                            BigDecimal targetAmount, String targetCurrency,
                            BigDecimal appliedRate, LocalDateTime convertedAt) {
        this.id = id;
        this.sourceAmount = sourceAmount;
        this.sourceCurrency = sourceCurrency;
        this.targetAmount = targetAmount;
        this.targetCurrency = targetCurrency;
        this.appliedRate = appliedRate;
        this.convertedAt = convertedAt;
    }

    public long getId() { return id; }
    public BigDecimal getSourceAmount() { return sourceAmount; }
    public String getSourceCurrency() { return sourceCurrency; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public String getTargetCurrency() { return targetCurrency; }
    public BigDecimal getAppliedRate() { return appliedRate; }
    public LocalDateTime getConvertedAt() { return convertedAt; }
}

