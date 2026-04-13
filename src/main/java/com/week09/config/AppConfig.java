package com.week09.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * AppConfig – typed, centralised configuration.
 *
 * Reads values from:
 *   1. src/main/resources/config.properties (defaults)
 *   2. Java system properties (-Dkey=value) – OVERRIDE the file value
 *
 * This two-layer approach lets the project have sensible defaults for local
 * development while allowing a CI pipeline to override any value without
 * touching source code:
 *
 *   mvn test -Dui.headless=true -Dapi.base.url=https://staging.demoqa.com
 *
 * LAYER RULE:
 *   - Only AppConfig reads system properties / the properties file.
 *   - All other classes (pages, clients, base tests) call AppConfig.getXxx().
 *   - Never call System.getProperty() outside this class.
 *
 * USAGE:
 *   AppConfig.getUiBaseUrl()     → "https://demoqa.com" (or override)
 *   AppConfig.getApiBaseUrl()    → "https://demoqa.com"
 *   AppConfig.isHeadless()       → false (unless -Dui.headless=true)
 *   AppConfig.getTimeout()       → 10000
 */
public final class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties props = new Properties();

    // Static initialiser – runs once when the class is first loaded
    static {
        try (InputStream in = AppConfig.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                log.warn("config.properties not found on classpath – using system properties only");
            } else {
                props.load(in);
                log.debug("Loaded config from {}", CONFIG_FILE);
            }
        } catch (IOException e) {
            log.error("Failed to load {}: {}", CONFIG_FILE, e.getMessage());
        }
    }

    private AppConfig() { /* utility class */ }

    // =========================================================================
    // Private helper – system property wins over file value
    // =========================================================================

    private static String get(String key) {
        return System.getProperty(key, props.getProperty(key, ""));
    }

    private static String get(String key, String defaultValue) {
        return System.getProperty(key, props.getProperty(key, defaultValue));
    }

    // =========================================================================
    // UI configuration
    // =========================================================================

    public static String getUiBaseUrl() {
        return get("ui.base.url", "https://demoqa.com");
    }

    public static String getBrowser() {
        return get("ui.browser", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("ui.headless", "false"));
    }

    /** Global Selenide auto-wait timeout in milliseconds. */
    public static long getTimeout() {
        return Long.parseLong(get("ui.timeout", "10000"));
    }

    public static String getBrowserSize() {
        return get("ui.browser.size", "1920x1080");
    }

    // =========================================================================
    // API configuration
    // =========================================================================

    public static String getApiBaseUrl() {
        return get("api.base.url", "https://demoqa.com");
    }

    public static String getBookStorePath() {
        return get("api.books.path", "/BookStore/v1/Books");
    }

    /** Full Books endpoint URL. */
    public static String getBooksEndpoint() {
        return getApiBaseUrl() + getBookStorePath();
    }
}
