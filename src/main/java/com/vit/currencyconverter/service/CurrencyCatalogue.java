package com.vit.currencyconverter.service;

import com.vit.currencyconverter.exception.CurrencyNotFoundException;
import com.vit.currencyconverter.model.CurrencyInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

public class CurrencyCatalogue {
    private final List<CurrencyInfo> currencies = new ArrayList<CurrencyInfo>();

    public CurrencyCatalogue() {
        for (Currency currency : Currency.getAvailableCurrencies()) {
            currencies.add(new CurrencyInfo(currency));
        }
        Collections.sort(currencies, new Comparator<CurrencyInfo>() {
            public int compare(CurrencyInfo a, CurrencyInfo b) {
                return a.getCode().compareTo(b.getCode());
            }
        });
    }

    public CurrencyInfo findByCode(String code) throws CurrencyNotFoundException {
        String wanted = code.trim().toUpperCase();
        for (CurrencyInfo currency : currencies) {
            if (currency.getCode().equals(wanted)) return currency;
        }
        throw new CurrencyNotFoundException("Unknown currency code: " + wanted);
    }

    public List<CurrencyInfo> search(String text) {
        String query = text.trim().toLowerCase();
        List<CurrencyInfo> matches = new ArrayList<CurrencyInfo>();
        for (CurrencyInfo currency : currencies) {
            if (currency.getCode().toLowerCase().contains(query)
                    || currency.getName().toLowerCase().contains(query)) {
                matches.add(currency);
            }
        }
        return matches;
    }

    public List<CurrencyInfo> getAll() {
        return new ArrayList<CurrencyInfo>(currencies);
    }
}

