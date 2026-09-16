package com.vit.currencyconverter.model;

import java.util.Currency;

public class CurrencyInfo {
    private final String code;
    private final String name;
    private final String symbol;
    private final int fractionDigits;

    public CurrencyInfo(Currency currency) {
        this.code = currency.getCurrencyCode();
        this.name = currency.getDisplayName();
        this.symbol = currency.getSymbol();
        this.fractionDigits = currency.getDefaultFractionDigits();
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public int getFractionDigits() { return fractionDigits; }

    @Override
    public String toString() {
        return code + " - " + name + " (" + symbol + ")";
    }
}

