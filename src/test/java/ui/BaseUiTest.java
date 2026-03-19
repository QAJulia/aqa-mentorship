package ui;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;

import java.time.Duration;

/**
 * Base class for all UI tests.
 *
 * What it does:
 * -------------
 * 1. Opens a fresh browser before each test (@BeforeMethod)
 * 2. Closes the browser after each test (@AfterMethod)
 * 3. Takes a screenshot when a test fails and attaches it to the Allure report
 *
 * Every UI test class extends this class and gets a ready `driver` field to use.
 *
 * Why @BeforeMethod and not @BeforeClass?
 * ----------------------------------------
 * @BeforeClass would open ONE browser for ALL tests in the class.
 * If test A leaves the browser in a broken state, test B would fail for the wrong reason.
 * @BeforeMethod gives each test a clean, independent browser — tests don't affect each other.
 */
public class BaseUiTest {

    // 'protected' = visible in this class and in all subclasses (our test classes)
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        String browser = ConfigReader.get("browser");
        driver = DriverFactory.createDriver(browser);

        // Implicit wait: Selenium will retry finding an element for up to 5 seconds
        // before throwing NoSuchElementException
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // Start every test with a maximized window for consistent layout
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Take a screenshot if the test FAILED and attach it to Allure report
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot();
        }

        // Always close the browser — even if the test threw an exception
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Takes a screenshot and returns the bytes.
     * The @Attachment annotation tells Allure to save the returned bytes as a PNG file
     * and display it inside the test report.
     */
    @Attachment(value = "Screenshot on failure", type = "image/png")
    private byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}