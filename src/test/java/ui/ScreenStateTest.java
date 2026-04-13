package ui;

import com.week07.pages.factory.TablesPageFactory;
import com.week07.screen.TablesScreenState;
import io.qameta.allure.*;
import org.testng.annotations.Test;

/**
 * ScreenStateTest – demonstrates the Screen/State Object pattern.
 *
 * KEY DESIGN:
 *   TablesPageFactory  → performs actions (open, navigate)
 *   TablesScreenState  → reads and asserts the current UI state
 *
 * These two concerns are deliberately in separate classes.
 * The test composes them: first "do", then "verify".
 *
 *   TablesPageFactory page  = new TablesPageFactory(driver).open().isPageOpened();
 *   TablesScreenState state = new TablesScreenState(driver);
 *   state.assertRowCount(4).assertRowExists("Smith");
 *
 * The driver is shared – both objects see the same page.
 */
@Epic("Week 7 – Architecture & Patterns")
@Feature("Tables – Screen State Object")
public class ScreenStateTest extends BaseTest {

    @Test(description = "Tables page: full state assertion chain using TablesScreenState",
          groups = {"smoke", "screen-state"})
    @Story("Screen State – initial page verification")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "Opens tables page via TablesPageFactory, then uses TablesScreenState " +
        "to chain all assertions: row count, headers, specific row existence, email."
    )
    public void tablesPageFullStateChain() {
        // 1. ACT – navigate using the page object
        new TablesPageFactory(driver).open().isPageOpened();

        // 2. VERIFY – assert via the screen state object
        new TablesScreenState(driver)
                .assertRowCount(4)
                .assertColumnHeaders(
                        "Last Name", "First Name", "Email", "Due", "Web Site", "Action"
                )
                .assertRowExists("Smith")
                .assertRowExists("Bach")
                .assertRowExists("Conway")
                .assertAllRowsHaveActionLinks();
    }

    @Test(description = "Email for Smith row is correct – following-sibling assertion",
          groups = {"regression", "screen-state"})
    @Story("Screen State – column value assertion")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies the email column for the Smith row using the assertEmailForRow method.")
    public void emailForSmithRowIsCorrect() {
        new TablesPageFactory(driver).open().isPageOpened();

        new TablesScreenState(driver)
                .assertEmailForRow("Smith", "jsmith@gmail.com");
    }

    @Test(description = "Tables page: dynamic locator via PageFactory falls back to By.xpath",
          groups = {"regression", "screen-state"})
    @Story("Page Factory limitation – dynamic XPath")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Shows that TablesPageFactory.hasRowWithLastName() must use By.xpath() dynamically " +
        "because @FindBy annotations cannot accept runtime parameters. " +
        "This is one case where Classic POM is more natural than Page Factory."
    )
    public void pageFactoryDynamicLocatorFallback() {
        TablesPageFactory page = new TablesPageFactory(driver).open().isPageOpened();

        // This method uses By.xpath() internally – cannot be expressed as @FindBy
        assert page.hasRowWithLastName("Bach") : "Expected row with last name Bach";
        assert page.getEmailByLastName("Bach").equals("fbach@hotmail.com")
                : "Expected fbach@hotmail.com";
    }

    @Test(description = "assertRowAbsent – non-existent last name",
          groups = {"regression", "screen-state"})
    @Story("Screen State – absence assertion")
    @Severity(SeverityLevel.MINOR)
    @Description("Verifies assertRowAbsent() correctly passes when no row with that name exists.")
    public void assertRowAbsentForNonExistentName() {
        new TablesPageFactory(driver).open().isPageOpened();

        new TablesScreenState(driver)
                .assertRowAbsent("NonExistentUser");
    }
}
