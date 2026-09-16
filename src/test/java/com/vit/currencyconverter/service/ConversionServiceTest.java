package com.vit.currencyconverter.service;

import com.vit.currencyconverter.exception.InvalidAmountException;
import com.vit.currencyconverter.exception.RateNotFoundException;
import com.vit.currencyconverter.model.ConversionRecord;
import com.vit.currencyconverter.model.ExchangeRate;
import com.vit.currencyconverter.repository.ConversionRepository;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class ConversionServiceTest {
    public static void main(String[] args) throws Exception {
        FakeRepository repository = new FakeRepository();
        repository.rates.put("USD", new BigDecimal("1.0"));
        repository.rates.put("INR", new BigDecimal("80.0"));
        repository.rates.put("EUR", new BigDecimal("0.8"));
        ConversionService service = new ConversionService(new RateManager(repository));
        check(new BigDecimal("800.00").equals(service.convert(new BigDecimal("10"), "USD", "INR")), "USD to INR");
        check(new BigDecimal("1.00").equals(service.convert(new BigDecimal("100"), "INR", "EUR")), "INR to EUR");
        try { service.convert(new BigDecimal("-1"), "USD", "INR"); throw new AssertionError("Negative amount accepted"); }
        catch (InvalidAmountException expected) { System.out.println("PASS: negative amount validation"); }
        try { service.convert(BigDecimal.ONE, "USD", "ABC"); throw new AssertionError("Missing rate accepted"); }
        catch (RateNotFoundException expected) { System.out.println("PASS: missing rate validation"); }
        System.out.println("All conversion tests passed.");
    }
    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError("Failed: " + name);
        System.out.println("PASS: " + name);
    }
    private static class FakeRepository implements ConversionRepository {
        final Map<String, BigDecimal> rates = new HashMap<String, BigDecimal>();
        public void initialize() { }
        public void saveRate(ExchangeRate rate) { rates.put(rate.getCurrencyCode(), rate.getUnitsPerUsd()); }
        public BigDecimal findRate(String code) { return rates.get(code); }
        public List<ExchangeRate> findAllRates() { return Collections.emptyList(); }
        public void saveConversion(ConversionRecord record) { }
        public List<ConversionRecord> findAllConversions() { return Collections.emptyList(); }
        public List<ConversionRecord> findConversionsByCurrency(String code) { return Collections.emptyList(); }
    }
}
