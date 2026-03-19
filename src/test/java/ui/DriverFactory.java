package ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Creates a WebDriver instance for the requested browser.
 *
 * Why a factory?
 * ---------------
 * Without this class, every BaseUiTest would need an if/else block to pick the right browser.
 * By centralising that logic here, changing how Chrome is launched (e.g., adding --headless)
 * is a one-line change in one place.
 *
 * Adding a new browser later (Edge, Safari) is also isolated to this file.
 */
public class DriverFactory {

    /**
     * @param browser   "chrome" or "firefox" (case-insensitive)
     * @return a ready-to-use WebDriver instance
     */
    public static WebDriver createDriver(String browser) {
        switch (browser.toLowerCase().trim()) {

            case "chrome": {
                // WebDriverManager downloads the matching chromedriver binary automatically.
                // It checks your installed Chrome version and fetches the right driver.
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();
                // Uncomment the next line to run Chrome without opening a window (CI/CD mode):
                // options.addArguments("--headless=new");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");

                return new ChromeDriver(options);
            }

            case "firefox": {
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            }

            default:
                throw new IllegalArgumentException(
                        "Unknown browser: '" + browser + "'. Supported values: chrome, firefox"
                );
        }
    }
}
