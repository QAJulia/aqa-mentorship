package ui;

import com.week06.pages.TablesPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * TablesPageTest – Selenide version (Week 8).
 *
 * MIGRATION from Week 6:
 *   - new TablesPage(driver).open(BASE_URL)  →  new TablesPage().open()
 *   - getTable1Headers() now calls $$(By).texts() internally — same List<String> returned
 *   - getRowCellsByLastName() uses row.$$(tagName).texts() instead of stream().map()
 *   - assertRowCount() uses shouldHave(CollectionCondition.size(N))
 *   - assertEmailForRow() uses shouldHave(exactText(...))
 *
 * Test method bodies are identical to Week 6 — only page construction changed.
 */
@Epic("Week 8 – Selenide")
@Feature("Tables Page")
public class TablesPageTest extends BaseTest {

    @Test(description = "Table 1 has the expected column headers",
          groups = {"smoke", "tables"})
    @Story("Table structure")
    @Severity(SeverityLevel.NORMAL)
    public void tableHeadersAreCorrect() {
        List<String> headers = new TablesPage().open().getTable1Headers();

        assertEquals(headers, Arrays.asList(
                "Last Name", "First Name", "Email", "Due", "Web Site", "Action"
        ));
    }

    @Test(description = "Table 1 contains exactly 4 data rows",
          groups = {"smoke", "tables"})
    @Story("Table structure")
    @Severity(SeverityLevel.NORMAL)
    public void tableHasFourRows() {
        // assertRowCount uses shouldHave(CollectionCondition.size(4)) — cleaner failure msg
        new TablesPage().open().assertRowCount(4);
    }

    @Test(description = "Row with last name 'Smith' exists in Table 1",
          groups = {"smoke", "tables"})
    @Story("Row lookup by cell value")
    @Severity(SeverityLevel.CRITICAL)
    public void rowWithSmithExists() {
        assertTrue(new TablesPage().open().hasRowWithLastName("Smith"),
                "Expected a row with last name 'Smith'");
    }

    @Test(description = "Email for 'Smith' retrieved via following-sibling axis",
          groups = {"regression", "tables"})
    @Story("Column value retrieval")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Same XPath as Week 6; only the underlying call changed to Selenide getText().")
    public void emailForSmithIsCorrect() {
        // assertEmailForRow uses $(By).shouldHave(exactText(...))
        new TablesPage().open().assertEmailForRow("Smith", "jsmith@gmail.com");
    }

    @Test(description = "Full row data for 'Bach' contains expected values",
          groups = {"regression", "tables"})
    @Story("Row data validation")
    @Severity(SeverityLevel.NORMAL)
    public void fullRowForBachIsCorrect() {
        List<String> cells = new TablesPage().open().getRowCellsByLastName("Bach");

        assertEquals(cells.get(0), "Bach",              "Last name column");
        assertEquals(cells.get(1), "Frank",             "First name column");
        assertEquals(cells.get(2), "fbach@hotmail.com", "Email column");
    }

    @Test(description = "Each body row has both an edit and a delete action link",
          groups = {"regression", "tables"})
    @Story("Action links")
    @Severity(SeverityLevel.NORMAL)
    public void eachRowHasEditAndDeleteLink() {
        TablesPage page = new TablesPage().open();
        int rows = page.getTable1RowCount();

        assertEquals(page.getEditLinkCount(),   rows, "Edit link count should equal row count");
        assertEquals(page.getDeleteLinkCount(), rows, "Delete link count should equal row count");
    }

    @Test(description = "Clicking edit for 'Smith' does not throw (ancestor-axis XPath check)",
          groups = {"regression", "tables"})
    @Story("Action links")
    @Severity(SeverityLevel.MINOR)
    public void clickEditForSmithDoesNotFail() {
        TablesPage page = new TablesPage().open();
        page.clickEditFor("Smith");
        assertTrue(page.getCurrentUrl().contains("tables"),
                "Should remain on tables page");
    }
}
