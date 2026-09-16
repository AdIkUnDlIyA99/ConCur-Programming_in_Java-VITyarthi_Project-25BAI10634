package com.vit.currencyconverter.app;

import com.vit.currencyconverter.exception.CurrencyNotFoundException;
import com.vit.currencyconverter.model.ConversionRecord;
import com.vit.currencyconverter.model.CurrencyInfo;
import com.vit.currencyconverter.model.ExchangeRate;
import com.vit.currencyconverter.repository.JdbcConversionRepository;
import com.vit.currencyconverter.repository.FileConversionRepository;
import com.vit.currencyconverter.repository.ConversionRepository;
import com.vit.currencyconverter.service.ConversionService;
import com.vit.currencyconverter.service.CurrencyCatalogue;
import com.vit.currencyconverter.service.RateManager;
import com.vit.currencyconverter.task.RateMonitorTask;
import com.vit.currencyconverter.util.CsvManager;
import com.vit.currencyconverter.util.DatabaseManager;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private CurrencyCatalogue catalogue;
    private ConversionRepository repository;
    private RateManager rateManager;
    private ConversionService conversionService;
    private CsvManager csvManager;

    public static void main(String[] args) {
        try {
            new Main().start();
        } catch (Exception error) {
            System.out.println("Unable to start application: " + error.getMessage());
        }
    }

    private void start() throws Exception {
        new File("data").mkdirs();
        try {
            DatabaseManager database = new DatabaseManager("./data/currencydb");
            repository = new JdbcConversionRepository(database);
            System.out.println("Storage mode: JDBC database");
        } catch (ClassNotFoundException missingDriver) {
            repository = new FileConversionRepository(new File("data"));
            System.out.println("Storage mode: local files (H2 driver not found)");
        }
        repository.initialize();
        catalogue = new CurrencyCatalogue();
        rateManager = new RateManager(repository);
        conversionService = new ConversionService(rateManager);
        csvManager = new CsvManager();

        if (rateManager.getAllRates().isEmpty()) {
            csvManager.importRates(new File("data/exchange-rates.csv"), rateManager);
        }

        RateMonitorTask monitorTask = new RateMonitorTask(rateManager);
        monitorTask.start();
        try {
            menuLoop();
        } finally {
            monitorTask.stopMonitor();
        }
    }

    private void menuLoop() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": convertCurrency(); break;
                    case "2": searchCurrencies(); break;
                    case "3": viewRates(); break;
                    case "4": updateRate(); break;
                    case "5": showHistory(repository.findAllConversions()); break;
                    case "6": searchHistory(); break;
                    case "7": exportHistory(); break;
                    case "8": running = false; break;
                    default: System.out.println("Please enter a number from 1 to 8.");
                }
            } catch (Exception error) {
                System.out.println("Error: " + error.getMessage());
            }
        }
        System.out.println("Thank you for using the currency converter.");
    }

    private void printMenu() {
        System.out.println("\n===== OFFLINE CURRENCY CONVERTER =====");
        System.out.println("1. Convert currency");
        System.out.println("2. Search currencies");
        System.out.println("3. View configured rates");
        System.out.println("4. Add or update a rate");
        System.out.println("5. View conversion history");
        System.out.println("6. Search conversion history");
        System.out.println("7. Export history to CSV");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    private void convertCurrency() throws Exception {
        System.out.print("Enter amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        String from = readCurrencyCode("From currency code: ");
        String to = readCurrencyCode("To currency code: ");
        BigDecimal result = conversionService.convert(amount, from, to);
        BigDecimal appliedRate = conversionService.calculateAppliedRate(from, to);
        repository.saveConversion(new ConversionRecord(0, amount, from, result, to,
                appliedRate, LocalDateTime.now()));
        System.out.println(amount + " " + from + " = " + result + " " + to);
        System.out.println("Conversion saved successfully.");
    }

    private void searchCurrencies() {
        System.out.print("Enter currency name or code: ");
        List<CurrencyInfo> matches = catalogue.search(scanner.nextLine());
        if (matches.isEmpty()) System.out.println("No matching currencies found.");
        else for (CurrencyInfo currency : matches) System.out.println(currency);
        System.out.println("Matches: " + matches.size());
    }

    private void viewRates() throws Exception {
        System.out.println("Rates are stored as units per 1 USD:");
        for (ExchangeRate rate : rateManager.getAllRates()) {
            System.out.println(rate.getCurrencyCode() + " = " + rate.getUnitsPerUsd());
        }
    }

    private void updateRate() throws Exception {
        String code = readCurrencyCode("Currency code: ");
        System.out.print("Units equal to 1 USD: ");
        BigDecimal rate = new BigDecimal(scanner.nextLine().trim());
        rateManager.saveRate(code, rate);
        System.out.println("Exchange rate saved.");
    }

    private void searchHistory() throws Exception {
        String code = readCurrencyCode("Currency code: ");
        showHistory(repository.findConversionsByCurrency(code));
    }

    private void showHistory(List<ConversionRecord> records) {
        if (records.isEmpty()) {
            System.out.println("No conversion records found.");
            return;
        }
        for (ConversionRecord record : records) {
            System.out.println("#" + record.getId() + " | " + record.getSourceAmount() + " "
                    + record.getSourceCurrency() + " -> " + record.getTargetAmount() + " "
                    + record.getTargetCurrency() + " | " + record.getConvertedAt());
        }
    }

    private void exportHistory() throws Exception {
        File file = new File("exports/conversion-history.csv");
        csvManager.exportHistory(file, repository.findAllConversions());
        System.out.println("History exported to " + file.getPath());
    }

    private String readCurrencyCode(String prompt) throws CurrencyNotFoundException {
        System.out.print(prompt);
        String code = scanner.nextLine().trim().toUpperCase();
        catalogue.findByCode(code);
        return code;
    }
}
