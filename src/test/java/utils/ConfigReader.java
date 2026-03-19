package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads key-value pairs from src/test/resources/config.properties.
 *
 * Usage:
 *   String browser = ConfigReader.get("browser");
 *   String url     = ConfigReader.get("base.url");
 *
 * The static block runs once when the class is first used.
 * Properties are loaded into memory and reused for every call.
 */
public class ConfigReader {

    private static final Properties props = new Properties();

    static {
        // "static block" — runs automatically when the class is loaded, just once.
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "config.properties not found in src/test/resources/"
                );
            }
            props.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /**
     * Returns the value for the given property key.
     * Throws an exception if the key is not found so you know immediately something is wrong.
     */
    public static String get(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property '" + key + "' not found in config.properties");
        }
        return value;
    }
}