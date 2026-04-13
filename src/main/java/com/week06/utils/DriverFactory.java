package com.week06.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * DriverFactory – centralises WebDriver creation.
 *
 * Responsibilities:
 *  - Download and configure the correct ChromeDriver binary via WebDriverManager
 *  - Apply browser options (window size, optional headless mode)
 *  - Return a ready-to-use WebDriver instance
 *
 * Tests never create a driver themselves; they call DriverFactory.createDriver().
 */
public class DriverFactory {

    // Set to true to run without a visible browser window (useful for CI pipelines)
    private static final boolean HEADLESS = Boolean.parseBoolean(
            System.getProperty("headless", "false")
    );

    private DriverFactory() {
        // Utility class – no instances
    }

    /**
     * Creates and returns a configured ChromeDriver.
     *
     * @return a new WebDriver instance pointing to a blank page
     */
    public static WebDriver createDriver() {
        // WebDriverManager resolves and downloads the matching chromedriver binary.
        // No manual chromedriver.exe management needed.
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = buildOptions();
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        return driver;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static ChromeOptions buildOptions() {
        ChromeOptions options = new ChromeOptions();

        if (HEADLESS) {
            // Selenium 4 headless mode – uses the new headless implementation
            options.addArguments("--headless=new");
        }

        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }
}
