package com.week03.listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Custom suite listener that logs suite start and finish events.
 * Provides visual feedback in console for test suite execution.
 */
public class SuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  🚀 Starting Test Suite: " + suite.getName());
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }

    @Override
    public void onFinish(ISuite suite) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  ✅ Finished Test Suite: " + suite.getName());
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }
}