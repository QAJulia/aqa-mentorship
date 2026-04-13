package ui;

import com.week06.pages.CheckboxesPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * CheckboxesPageTest – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *   - new CheckboxesPage(driver).open(BASE_URL)  →  new CheckboxesPage().open()
 *   - assertFalse(page.isFirstChecked())         →  same (boolean query delegating to Selenide)
 *   - assertEquals(page.getCheckedCount(), ...)  →  page.assertTotalCount(N) uses shouldHave(size)
 */
@Epic("Week 8 – Selenide")
@Feature("Checkboxes Page")
public class CheckboxesPageTest extends BaseTest {

    @Test(description = "Page has exactly 2 checkboxes",
          groups = {"smoke", "checkboxes"})
    @Story("Page content")
    @Severity(SeverityLevel.NORMAL)
    public void pageHasTwoCheckboxes() {
        new CheckboxesPage().open().assertTotalCount(2);
    }

    @Test(description = "First checkbox is initially unchecked",
          groups = {"smoke", "checkboxes"})
    @Story("Initial state")
    @Severity(SeverityLevel.NORMAL)
    public void firstCheckboxInitiallyUnchecked() {
        assertFalse(new CheckboxesPage().open().isFirstChecked(),
                "First checkbox should be unchecked by default");
    }

    @Test(description = "Second (last) checkbox is initially checked",
          groups = {"smoke", "checkboxes"})
    @Story("Initial state")
    @Severity(SeverityLevel.NORMAL)
    public void lastCheckboxInitiallyChecked() {
        assertTrue(new CheckboxesPage().open().isLastChecked(),
                "Last checkbox should be checked by default");
    }

    @Test(description = "Checking the first checkbox makes it selected",
          groups = {"regression", "checkboxes"})
    @Story("User interaction")
    @Severity(SeverityLevel.CRITICAL)
    @Description("setSelected(true) replaces the conditional click from Week 6.")
    public void checkFirstCheckbox() {
        // assertFirstIsChecked() uses $(By).shouldBe(checked) — auto-wait
        new CheckboxesPage()
                .open()
                .checkFirst()
                .assertFirstIsChecked();
    }

    @Test(description = "Unchecking the last checkbox makes it deselected",
          groups = {"regression", "checkboxes"})
    @Story("User interaction")
    @Severity(SeverityLevel.CRITICAL)
    public void uncheckLastCheckbox() {
        CheckboxesPage page = new CheckboxesPage().open();
        page.uncheckLast();
        assertFalse(page.isLastChecked(), "Last checkbox should be unchecked after click");
    }

    @Test(description = "checkAll() selects both checkboxes",
          groups = {"regression", "checkboxes"})
    @Story("Bulk actions")
    @Severity(SeverityLevel.NORMAL)
    public void checkAllSelectsAll() {
        CheckboxesPage page = new CheckboxesPage().open();
        page.checkAll();
        assertEquals(page.getCheckedCount(), page.getTotalCheckboxCount(),
                "All checkboxes should be checked");
    }

    @Test(description = "uncheckAll() deselects both checkboxes",
          groups = {"regression", "checkboxes"})
    @Story("Bulk actions")
    @Severity(SeverityLevel.NORMAL)
    public void uncheckAllDeselectsAll() {
        CheckboxesPage page = new CheckboxesPage().open();
        page.uncheckAll();
        assertEquals(page.getCheckedCount(), 0,
                "No checkboxes should be checked after uncheckAll()");
    }
}
