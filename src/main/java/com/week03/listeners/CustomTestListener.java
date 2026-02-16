package com.week03.listeners;

import io.qameta.allure.Allure;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Custom TestNG listener that logs test execution and integrates with Allure.
 * This listener provides console output and enriches Allure reports with additional information.
 */
public class CustomTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("▶ STARTING TEST: " + testName);
        Allure.step("Test started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        System.out.println("✓ PASSED: " + testName + " (Duration: " + duration + "ms)");
        Allure.step("Test passed in " + duration + "ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        System.out.println("✗ FAILED: " + testName);
        if (throwable != null) {
            System.out.println("  Reason: " + throwable.getMessage());
            
            // Attach failure details to Allure report
            Allure.addAttachment("Failure Reason", throwable.toString());
            Allure.addAttachment("Stack Trace", getStackTrace(throwable));
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println("⊘ SKIPPED: " + testName);
        Allure.step("Test skipped: " + testName);
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("\n========== STARTING TEST EXECUTION ==========");
        System.out.println("Test Suite: " + context.getName());
        System.out.println("=============================================\n");
    }

    @Override
    public void onFinish(ITestContext context) {
        int total = context.getAllTestMethods().length;
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        
        System.out.println("\n========== TEST EXECUTION SUMMARY ==========");
        System.out.println("Total tests run: " + total);
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Skipped: " + skipped);
        System.out.println("Success Rate: " + String.format("%.2f%%", (passed * 100.0 / total)));
        System.out.println("============================================\n");
    }

    /**
     * Formats throwable stack trace as a string.
     */
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("    at ").append(element.toString()).append("\n");
        }
        
        return sb.toString();
    }
}