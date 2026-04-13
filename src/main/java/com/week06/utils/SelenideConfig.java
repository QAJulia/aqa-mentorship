package com.week06.utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

/**
 * SelenideConfig – replaces DriverFactory from Week 6.
 *
 * In Week 6 we used DriverFactory to:
 *   1. Call WebDriverManager.chromedriver().setup()
 *   2. Create a new ChromeDriver with options
 *   3. Maximize the window
 *
 * In Week 8 with Selenide, all of that is handled by the library.
 * We only need to SET CONFIGURATION VALUES once before the first open().
 *
 * Selenide internally:
 *   - Calls WebDriverManager to download the correct chromedriver binary
 *   - Creates the ChromeDriver instance on the first open() call
 *   - Closes the driver automatically at JVM shutdown (or on closeWebDriver())
 *
 * We also register the AllureSelenide listener here, which replaces the
 * manual TakesScreenshot code that was in BaseTest.tearDown() in Week 6.
 */
public final class SelenideConfig {

    private static boolean configured = false;

    private SelenideConfig() { /* utility class */ }

    /**
     * Configure Selenide and register the Allure listener.
     * Idempotent – safe to call multiple times (only runs once per JVM).
     *
     * Call this in @BeforeSuite in BaseTest.
     */
    public static void init() {
        if (configured) return;

        // ── Browser ──────────────────────────────────────────────────────────
        // System property -Dbrowser=firefox overrides the default
        Configuration.browser = System.getProperty("browser", "chrome");

        // ── Headless mode ────────────────────────────────────────────────────
        // Pass -Dheadless=true from CLI or CI pipeline
        Configuration.headless = Boolean.parseBoolean(
                System.getProperty("headless", "false")
        );

        // ── Base URL ─────────────────────────────────────────────────────────
        // Enables open("/login") shorthand in page classes.
        // Override with -Dbase.url=https://staging.myapp.com
        Configuration.baseUrl = System.getProperty(
                "base.url", "https://the-internet.herokuapp.com"
        );

        // ── Global auto-wait timeout (milliseconds) ──────────────────────────
        // Every $() call waits up to this duration for the condition to be met.
        // Override with -Dtimeout=15000
        Configuration.timeout = Long.parseLong(
                System.getProperty("timeout", "10000")
        );

        // ── Window size ──────────────────────────────────────────────────────
        // replaces driver.manage().window().maximize() from Week 6
        Configuration.browserSize = "1920x1080";

        // ── Screenshots ──────────────────────────────────────────────────────
        // Selenide saves a PNG on every test failure automatically.
        // AllureSelenide picks these up and attaches them to the report.
        Configuration.reportsFolder = "target/selenide-reports";
        Configuration.savePageSource = false;   // skip HTML source (keeps report lighter)

        // ── Allure Selenide Listener ─────────────────────────────────────────
        // Replaces the manual TakesScreenshot code in Week 6 BaseTest.tearDown().
        // Must be registered BEFORE the first open() call.
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true)
                .savePageSource(false)
        );

        configured = true;
    }
}
