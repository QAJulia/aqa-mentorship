package ui;

import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.ConfigReader;

import java.util.List;

/**
 * Tests for the Checkboxes page.
 * URL: https://the-internet.herokuapp.com/checkboxes
 *
 * This class is the HOMEWORK SOLUTION for Task 2.
 * Students write this from scratch — this file is the reference answer.
 *
 * Page structure:
 *   <input type="checkbox">          ← checkbox 1, unchecked by default
 *   <input type="checkbox" checked>  ← checkbox 2, checked by default
 */
@Epic("UI Tests")
@Feature("Checkboxes")
public class CheckboxTest extends BaseUiTest {

    private static final String BASE_URL = ConfigReader.get("base.url");

    // Both checkboxes share the same selector — findElements returns a list
    private static final By CHECKBOXES = By.cssSelector("input[type='checkbox']");

    @Test
    @Story("Initial checkbox state")
    @Description("Verifies the default state: checkbox 1 unchecked, checkbox 2 checked")
    @Severity(SeverityLevel.NORMAL)
    public void testCheckboxInitialState() {
        driver.get(BASE_URL + "/checkboxes");

        // findElements (plural) returns ALL matching elements as a List
        List<WebElement> checkboxes = driver.findElements(CHECKBOXES);

        // The page has exactly 2 checkboxes
        Assert.assertEquals(checkboxes.size(), 2, "Expected 2 checkboxes on the page");

        // Checkbox 1 — should NOT be selected by default
        Assert.assertFalse(
                checkboxes.get(0).isSelected(),
                "Checkbox 1 should be unchecked by default"
        );

        // Checkbox 2 — should BE selected by default
        Assert.assertTrue(
                checkboxes.get(1).isSelected(),
                "Checkbox 2 should be checked by default"
        );
    }

    @Test
    @Story("Toggle checkbox")
    @Description("Clicks checkbox 1 and verifies it becomes selected")
    @Severity(SeverityLevel.NORMAL)
    public void testToggleCheckbox() {
        driver.get(BASE_URL + "/checkboxes");

        List<WebElement> checkboxes = driver.findElements(CHECKBOXES);

        // Checkbox 1 starts unchecked — click it
        checkboxes.get(0).click();

        // After clicking it should now be selected
        Assert.assertTrue(
                checkboxes.get(0).isSelected(),
                "Checkbox 1 should be checked after clicking it"
        );
    }
}
