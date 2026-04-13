package ui;

import com.week06.pages.CheckboxesPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * CheckboxesPageTest – tests for https://the-internet.herokuapp.com/checkboxes
 *
 * Demonstrates:
 *  - Page Object interaction without any direct driver calls in tests
 *  - Testing initial state vs. after-action state
 *  - Using positional XPath and descendant axis through the page class
 */
@Epic("Week 6 – Page Object Model")
@Feature("Checkboxes Page")
public class CheckboxesPageTest extends BaseTest {

    @Test(description = "Page has exactly 2 checkboxes",
          groups = {"smoke", "checkboxes"})
    @Story("Page content")
    @Severity(SeverityLevel.NORMAL)
    @Description("Open the checkboxes page and verify that exactly two checkboxes are present.")
    public void pageHasTwoCheckboxes() {
        CheckboxesPage page = new CheckboxesPage(driver).open(BASE_URL);

        assertEquals(page.getTotalCheckboxCount(), 2,
                "Expected 2 checkboxes on the page");
    }

    @Test(description = "First checkbox is initially unchecked",
          groups = {"smoke", "checkboxes"})
    @Story("Initial state")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the initial state of the first checkbox using positional XPath.")
    public void firstCheckboxInitiallyUnchecked() {
        CheckboxesPage page = new CheckboxesPage(driver).open(BASE_URL);

        assertFalse(page.isFirstChecked(),
                "First checkbox should be unchecked by default");
    }

    @Test(description = "Second (last) checkbox is initially checked",
          groups = {"smoke", "checkboxes"})
    @Story("Initial state")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the initial state of the last checkbox.")
    public void lastCheckboxInitiallyChecked() {
        CheckboxesPage page = new CheckboxesPage(driver).open(BASE_URL);

        assertTrue(page.isLastChecked(),
                "Last checkbox should be checked by default");
    }

    @Test(description = "Checking the first checkbox makes it selected",
          groups = {"regression", "checkboxes"})
    @Story("User interaction")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Click the first checkbox and verify it becomes checked.")
    public void checkFirstCheckbox() {
        CheckboxesPage page = new CheckboxesPage(driver).open(BASE_URL);

        page.checkFirst();

        assertTrue(page.isFirstChecked(),
                "First checkbox should be checked after clicking");
    }

    @Test(description = "Unchecking the last checkbox makes it deselected",
          groups = {"regression", "checkboxes"})
    @Story("User interaction")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Click the last checkbox (already checked) and verify it becomes unchecked.")
    public void uncheckLastCheckbox() {
        CheckboxesPage page = new CheckboxesPage(driver).open(BASE_URL);

        page.uncheckLast();

        assertFalse(page.isLastChecked(),
                "Last checkbox should be unchecked after clicking");
    }

    @Test(description = "checkAll() selects both checkboxes",
          groups = {"regression", "checkboxes"})
    @Story("Bulk actions")
    @Severity(SeverityLevel.NORMAL)
    @Description("Use checkAll() helper and verify all checkboxes become selected.")
    public void checkAllSelectsAll() {
        CheckboxesPage page = new CheckboxesPage(driver).open(BASE_URL);

        page.checkAll();

        assertEquals(page.getCheckedCount(), page.getTotalCheckboxCount(),
                "All checkboxes should be checked after checkAll()");
    }

    @Test(description = "uncheckAll() deselects both checkboxes",
          groups = {"regression", "checkboxes"})
    @Story("Bulk actions")
    @Severity(SeverityLevel.NORMAL)
    @Description("Use uncheckAll() helper and verify no checkbox is selected.")
    public void uncheckAllDeselectsAll() {
        CheckboxesPage page = new CheckboxesPage(driver).open(BASE_URL);

        page.uncheckAll();

        assertEquals(page.getCheckedCount(), 0,
                "No checkboxes should be checked after uncheckAll()");
    }
}
