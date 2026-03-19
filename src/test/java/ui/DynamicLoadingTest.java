package ui;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.time.Duration;

/**
 * Tests for the Dynamic Loading page.
 * URL: https://the-internet.herokuapp.com/dynamic_loading/1
 *
 * This class is the HOMEWORK SOLUTION for Task 3.
 *
 * The page shows a "Start" button.
 * After clicking it, a loading spinner appears, then disappears,
 * and finally "Hello World!" text becomes visible.
 *
 * This is the classic real-world scenario where you MUST use explicit wait
 * because the element is not available immediately.
 */
@Epic("UI Tests")
@Feature("Dynamic Loading")
public class DynamicLoadingTest extends BaseUiTest {

    private static final String BASE_URL = ConfigReader.get("base.url");

    private static final By START_BUTTON = By.cssSelector("#start button");
    private static final By FINISH_TEXT  = By.cssSelector("#finish h4");

    @Test
    @Story("Dynamic element appears after loading")
    @Description("Clicks Start, waits for the hidden element to appear, asserts its text")
    @Severity(SeverityLevel.NORMAL)
    public void testDynamicElementAppears() {
        driver.get(BASE_URL + "/dynamic_loading/1");

        // Click the Start button — this triggers the loading process
        driver.findElement(START_BUTTON).click();

        // Create an explicit wait — we are willing to wait UP TO 10 seconds
        // for the specific condition below to become true.
        // Unlike implicit wait, this targets ONE specific element and ONE specific condition.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait until the #finish h4 element is VISIBLE on the page
        // (it exists in HTML but is hidden; after loading it becomes visible)
        WebElement finishElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(FINISH_TEXT)
        );

        Assert.assertEquals(
                finishElement.getText(),
                "Hello World!",
                "Finish text did not match expected value"
        );
    }

    @Test
    @Story("Page title is correct")
    @Description("Navigates to the dynamic loading page and checks the page heading")
    @Severity(SeverityLevel.MINOR)
    public void testPageHeading() {
        driver.get(BASE_URL + "/dynamic_loading/1");

        String heading = driver.findElement(By.cssSelector("h3")).getText();
        Assert.assertEquals(heading, "Dynamically Loaded Page Elements");
    }
}