package ui;

import com.week07.config.TestConfig;
import com.week07.utils.DriverFactory;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

/**
 * BaseTest – Week 7 lifecycle base.
 *
 * Same structure as Week 6, but now uses TestConfig for the BASE_URL
 * instead of a hardcoded string constant. This means the base URL
 * can be overridden from the command line:
 *
 *   mvn test -Dbase.url=https://staging.myapp.com
 *
 * All test classes in lesson07 extend this class.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    // Resolved from TestConfig (reads system property or uses default)
    protected final String BASE_URL = TestConfig.getBaseUrl();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            attachScreenshot("Failure screenshot – " + result.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    protected void attachScreenshot(String name) {
        try {
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(bytes), "png");
        } catch (Exception ignored) { }
    }
}
