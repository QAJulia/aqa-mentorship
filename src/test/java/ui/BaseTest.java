package ui;

import com.week06.utils.DriverFactory;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

/**
 * BaseTest – TestNG lifecycle base class for all Week 6 UI tests.
 *
 * Responsibilities:
 *  - Create a new WebDriver instance before each test method (@BeforeMethod)
 *  - Navigate to the base URL
 *  - Capture a screenshot and attach it to the Allure report on failure
 *  - Close the driver after each test method (@AfterMethod)
 *
 * Using @BeforeMethod (not @BeforeClass) ensures full test isolation:
 * each test runs in its own browser session.
 *
 * All concrete test classes extend BaseTest and receive 'driver' and 'BASE_URL'.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    /** Base URL for all the-internet tests. Override in subclass if needed. */
    protected static final String BASE_URL = "https://the-internet.herokuapp.com";

    // =========================================================================
    // TestNG lifecycle
    // =========================================================================

    /**
     * Runs before each @Test method.
     * Creates a fresh browser and navigates to the home page.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.createDriver();
    }

    /**
     * Runs after each @Test method.
     *  - On FAILURE: attaches a screenshot to the Allure report
     *  - Always: closes the browser
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            attachScreenshot("Screenshot on failure – " + result.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    // =========================================================================
    // Helpers available to all subclasses
    // =========================================================================

    /**
     * Takes a PNG screenshot and attaches it to the current Allure report step.
     *
     * @param name the attachment label shown in the report
     */
    protected void attachScreenshot(String name) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
        } catch (Exception e) {
            // Driver may already be closed; swallow to avoid masking the real failure
        }
    }
}
