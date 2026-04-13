package com.week07.config;

/**
 * TestConfig – centralised configuration layer.
 *
 * All values are read from Java system properties first,
 * falling back to hardcoded defaults. This lets a CI pipeline
 * override any value without touching source code:
 *
 *   mvn test -Dbase.url=https://staging.myapp.com -Dtimeout=20
 *
 * LAYER RULE: Only the config layer reads system properties.
 * Pages and tests call TestConfig.getXxx() – they never call
 * System.getProperty() directly.
 */
public final class TestConfig {

    // =========================================================================
    // Defaults
    // =========================================================================

    private static final String DEFAULT_BASE_URL = "https://the-internet.herokuapp.com";
    private static final int    DEFAULT_TIMEOUT   = 10;
    private static final String DEFAULT_BROWSER   = "chrome";
    private static final boolean DEFAULT_HEADLESS = false;

    // =========================================================================
    // Public accessors
    // =========================================================================

    /** Base URL for all UI tests. */
    public static String getBaseUrl() {
        return System.getProperty("base.url", DEFAULT_BASE_URL);
    }

    /** Explicit wait timeout in seconds. */
    public static int getTimeout() {
        String raw = System.getProperty("timeout", String.valueOf(DEFAULT_TIMEOUT));
        return Integer.parseInt(raw);
    }

    /** Browser name (only "chrome" supported in this project). */
    public static String getBrowser() {
        return System.getProperty("browser", DEFAULT_BROWSER).toLowerCase();
    }

    /** Whether to run Chrome in headless mode. */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless",
                String.valueOf(DEFAULT_HEADLESS)));
    }

    // =========================================================================
    // Composed URL helpers
    // =========================================================================

    public static String getLoginUrl() {
        return getBaseUrl() + "/login";
    }

    public static String getTablesUrl() {
        return getBaseUrl() + "/tables";
    }

    public static String getCheckboxesUrl() {
        return getBaseUrl() + "/checkboxes";
    }

    public static String getDropdownUrl() {
        return getBaseUrl() + "/dropdown";
    }

    // =========================================================================

    private TestConfig() { /* utility class */ }
}
