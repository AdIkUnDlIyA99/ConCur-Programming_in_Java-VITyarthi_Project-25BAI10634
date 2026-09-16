package com.vit.currencyconverter.task;

import com.vit.currencyconverter.service.RateManager;

public class RateMonitorTask extends Thread {
    private final RateManager rateManager;
    private volatile boolean running = true;

    public RateMonitorTask(RateManager rateManager) {
        super("rate-monitor-thread");
        this.rateManager = rateManager;
        setDaemon(true);
    }

    public void stopMonitor() {
        running = false;
        interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(60000);
                int count = rateManager.getAllRates().size();
                System.out.println("\n[Background check: " + count + " exchange rates are configured]");
            } catch (InterruptedException ignored) {
                running = false;
            } catch (Exception error) {
                System.out.println("\n[Background rate check failed: " + error.getMessage() + "]");
            }
        }
    }
}
