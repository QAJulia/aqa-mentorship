package ui;

import com.week06.pages.TablesPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * TablesPageTest – tests for https://the-internet.herokuapp.com/tables
 *
 * The most XPath-heavy set of tests in Week 6:
 *  - Row selection by cell text
 *  - Column data retrieval via following-sibling axis
 *  - ancestor axis to navigate from cell → row → action link
 *  - normalize-space() for whitespace-tolerant matching
 */
@Epic("Week 6 – Page Object Model")
@Feature("Tables Page")
public class TablesPageTest extends BaseTest {

    @Test(description = "Table 1 has the expected column headers",
          groups = {"smoke", "tables"})
    @Story("Table structure")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that Table 1 has the correct column headers in order.")
    public void tableHeadersAreCorrect() {
        TablesPage page = new TablesPage(driver).open(BASE_URL);

        List<String> headers = page.getTable1Headers();
        List<String> expected = Arrays.asList(
                "Last Name", "First Name", "Email", "Due", "Web Site", "Action"
        );

        assertEquals(headers, expected,
                "Table 1 headers do not match expected values");
    }

    @Test(description = "Table 1 contains exactly 4 data rows",
          groups = {"smoke", "tables"})
    @Story("Table structure")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the row count in Table 1 tbody.")
    public void tableHasFourRows() {
        TablesPage page = new TablesPage(driver).open(BASE_URL);

        assertEquals(page.getTable1RowCount(), 4,
                "Table 1 should have 4 data rows");
    }

    @Test(description = "Row with last name 'Smith' exists in Table 1",
          groups = {"smoke", "tables"})
    @Story("Row lookup by cell value")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Use XPath predicate to find a row by last name using normalize-space().")
    public void rowWithSmithExists() {
        TablesPage page = new TablesPage(driver).open(BASE_URL);

        assertTrue(page.hasRowWithLastName("Smith"),
                "Expected a row with last name 'Smith'");
    }

    @Test(description = "Email for 'Smith' is retrieved via following-sibling axis",
          groups = {"regression", "tables"})
    @Story("Column value retrieval")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve the email of the 'Smith' row using following-sibling XPath axis.")
    public void emailForSmithIsCorrect() {
        TablesPage page = new TablesPage(driver).open(BASE_URL);

        String email = page.getEmailByLastName("Smith");
        assertEquals(email, "jsmith@gmail.com",
                "Email for Smith should be jsmith@gmail.com");
    }

    @Test(description = "Full row data for 'Bach' contains expected values",
          groups = {"regression", "tables"})
    @Story("Row data validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Retrieve all cells of the Bach row and assert each column's value.")
    public void fullRowForBachIsCorrect() {
        TablesPage page = new TablesPage(driver).open(BASE_URL);

        List<String> cells = page.getRowCellsByLastName("Bach");

        // cells = [LastName, FirstName, Email, Due, Website, Action]
        assertEquals(cells.get(0), "Bach",        "Last name column");
        assertEquals(cells.get(1), "Frank",       "First name column");
        assertEquals(cells.get(2), "fbach@hotmail.com", "Email column");
    }

    @Test(description = "Each body row has both an edit and a delete action link",
          groups = {"regression", "tables"})
    @Story("Action links")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the number of edit and delete links equals the number of rows.")
    public void eachRowHasEditAndDeleteLink() {
        TablesPage page = new TablesPage(driver).open(BASE_URL);

        int rows = page.getTable1RowCount();
        assertEquals(page.getEditLinkCount(), rows,
                "Edit link count should equal row count");
        assertEquals(page.getDeleteLinkCount(), rows,
                "Delete link count should equal row count");
    }

    @Test(description = "Clicking edit for 'Smith' does not throw an exception",
          groups = {"regression", "tables"})
    @Story("Action links")
    @Severity(SeverityLevel.MINOR)
    @Description("Click the edit link for the Smith row; verifies the ancestor-axis locator works.")
    public void clickEditForSmithDoesNotFail() {
        TablesPage page = new TablesPage(driver).open(BASE_URL);
        // This test mainly validates the ancestor-axis XPath does not throw
        // The-internet edit link is just a hash href, so no navigation happens
        page.clickEditFor("Smith");
        assertTrue(driver.getCurrentUrl().contains("tables"),
                "Should remain on the tables page after clicking edit");
    }
}
