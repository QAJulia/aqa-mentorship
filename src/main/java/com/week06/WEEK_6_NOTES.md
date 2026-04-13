# Week 6 – Advanced XPath & Page Object Model

## Goals
- Write complex XPath expressions confidently using axes and functions
- Understand and apply the Page Object Model (POM) design pattern
- Separate WebDriver logic from test logic

---

## Part 1 – Advanced XPath

### 1.1 Why XPath?

XPath (XML Path Language) is a query language for selecting nodes in an HTML/XML document tree.
It is more powerful than CSS selectors in certain scenarios, especially when you need to:
- Navigate **upward** in the DOM (from child to parent)
- Select elements **by text content**
- Use **relationships** between elements (sibling, ancestor, following)

---

### 1.2 XPath Axes

An **axis** defines the direction of navigation relative to the current **context node**.

| Axis | Direction | Example |
|---|---|---|
| `self` | The node itself | `self::div` |
| `parent` | One level up | `parent::tr` |
| `child` | One level down | `child::td` |
| `ancestor` | All nodes above | `ancestor::table` |
| `ancestor-or-self` | Current + all above | `ancestor-or-self::form` |
| `following-sibling` | Siblings after current node | `following-sibling::td` |
| `preceding-sibling` | Siblings before current node | `preceding-sibling::th` |
| `preceding` | All nodes before in document order | `preceding::input` |
| `following` | All nodes after in document order | `following::button` |
| `descendant` | All children recursively | `descendant::input` |
| `descendant-or-self` | Current + all children | `//` shorthand |

#### Axis Syntax

```
axisname::nodetest[predicate]
```

**Examples:**

```xpath
// Find a <td> that is a following sibling of another <td> containing "Edit"
//td[text()='Edit']/following-sibling::td

// Find the <table> ancestor of any <td> containing "John"
//td[text()='John']/ancestor::table

// Find the <tr> parent of a checkbox input
//input[@type='checkbox']/parent::td/parent::tr

// Select all <td> elements that come after a <th> in the same row
//th[text()='Last Name']/following-sibling::td

// Select all rows that have NO checkboxes
//tr[not(descendant::input[@type='checkbox'])]
```

---

### 1.3 Text-Based Selectors

#### `text()` – exact text match

```xpath
//a[text()='Checkboxes']
//td[text()='John']
//h3[text()='Example Domain']
```

#### `contains()` – partial text match

```xpath
//a[contains(text(), 'Check')]
//div[contains(@class, 'flash')]
//input[contains(@placeholder, 'User')]
```

#### `starts-with()` – prefix match

```xpath
//input[starts-with(@id, 'user')]
//div[starts-with(@class, 'success')]
```

#### `normalize-space()` – trims whitespace before matching

```xpath
//td[normalize-space(text())='John']
```

#### Combining functions

```xpath
//div[contains(@class, 'alert') and starts-with(text(), 'You')]
//input[@type='text' and not(@disabled)]
```

---

### 1.4 Working with Tables

HTML tables have a predictable structure:
```
table > thead/tbody > tr > th/td
```

Common patterns:

```xpath
// All rows in tbody
//table[@id='example-table']//tbody/tr

// Cell in column 2 of every row
//table//tr/td[2]

// Row where first cell equals "John"
//table//tr[td[1][text()='John']]

// Cell in the same row as "John" but in the 3rd column
//table//tr[td[1][text()='John']]/td[3]

// Header text of column 2
//table//thead/tr/th[2]

// Number of rows in tbody
count(//table//tbody/tr)
```

---

### 1.5 Working with Lists

```xpath
// All <li> items inside a specific <ul>
//ul[@id='menu']/li

// First <li>
//ul[@id='menu']/li[1]

// Last <li>
//ul[@id='menu']/li[last()]

// <li> containing specific link text
//ul/li[a[text()='Login']]

// All links inside a list
//ul[@id='menu']//a
```

---

### 1.6 Nested Structures

```xpath
// Input inside a label that contains "Remember me"
//label[contains(text(),'Remember')]/input

// Button inside a div with class "actions"
//div[contains(@class,'actions')]//button

// All inputs that are NOT hidden
//form//input[not(@type='hidden')]

// Checkbox that is NOT checked
//input[@type='checkbox' and not(@checked)]
```

---

## Part 2 – Page Object Model (POM)

### 2.1 What is POM?

The **Page Object Model** is a design pattern where each web page (or component) in the application
is represented by a dedicated Java class. This class:

1. Holds all **locators** (XPath/CSS) for elements on that page
2. Exposes **action methods** that represent what a user can do on that page
3. Returns other page objects when navigation occurs

### 2.2 Why POM?

| Without POM | With POM |
|---|---|
| Locators scattered across test classes | Locators centralised in page classes |
| Same `findElement(...)` duplicated everywhere | One method per action |
| Changing a locator means updating N tests | Change once in the page class |
| Test code mixes UI interaction with assertions | Tests read like business steps |

---

### 2.3 POM Structure in This Project

```
src/
├── main/java/com/week08/
│   ├── base/
│   │   └── BasePage.java         ← shared driver + wait + helpers
│   ├── pages/
│   │   ├── LoginPage.java        ← login form page
│   │   ├── CheckboxesPage.java   ← checkboxes demo page
│   │   ├── TablesPage.java       ← sortable tables page
│   │   └── DropdownPage.java     ← dropdown page
│   └── utils/
│       └── DriverFactory.java    ← creates / manages ChromeDriver
│
└── test/java/
    ├── BaseTest.java             ← @BeforeMethod / @AfterMethod lifecycle
    ├── LoginPageTest.java
    ├── CheckboxesPageTest.java
    ├── TablesPageTest.java
    └── DropdownPageTest.java
```

---

### 2.4 Key Design Rules

1. **Page class = one page (or component).**  
   Never mix locators from two different pages in one class.

2. **No assertions inside page classes.**  
   Page methods return values or other page objects; assertions live in tests.

3. **No driver in test classes.**  
   Tests call page methods; they never call `driver.findElement(...)` directly.

4. **WebDriverWait is in BasePage, not scattered.**  
   All waits are centralised so timeouts are managed in one place.

5. **Page methods return `this` for fluent chaining**, or return the next page object after navigation.

---

### 2.5 Fluent API Example

```java
loginPage
    .enterUsername("tomsmith")
    .enterPassword("SuperSecretPassword!")
    .clickLogin()
    .assertWelcomeMessageVisible();
```

Each method that stays on the same page returns `this`.  
Methods that navigate to a new page return `new TargetPage(driver)`.

---

### 2.6 BaseTest Lifecycle (TestNG)

```
@BeforeSuite  → (optional) one-time setup
@BeforeMethod → open browser, navigate to base URL     ← per test
@AfterMethod  → take screenshot on failure, close driver ← per test
@AfterSuite   → (optional) cleanup
```

Using `@BeforeMethod` (not `@BeforeClass`) gives **test isolation**:
each test gets a fresh browser session.

---

### 2.7 DriverFactory Pattern

`DriverFactory` is a utility class that:
- Creates a `ChromeDriver` with `WebDriverManager` (no manual chromedriver setup)
- Configures options (headless flag, window size, etc.)
- Returns a ready-to-use `WebDriver` instance

```java
WebDriver driver = DriverFactory.createDriver();
```

---

### 2.8 Summary: Responsibilities Table

| Class | Responsibility |
|---|---|
| `DriverFactory` | Create / configure the browser driver |
| `BasePage` | Shared driver reference, explicit waits, utility methods |
| `XxxPage` | Locators + user actions for one page |
| `BaseTest` | TestNG lifecycle: open/close driver, screenshot on failure |
| `XxxTest` | Test logic: call page methods + assert outcomes |

---

## Part 3 – Advanced XPath in Page Objects

### Using `By.xpath()` inside page classes

```java
// Axes example – find table row by cell text
private final By rowByLastName(String name) {
    return By.xpath("//table[@id='table1']//tr[td[2][normalize-space()='" + name + "']]");
}

// contains() – partial class match
private final By flashMessage = By.xpath("//div[contains(@class,'flash')]");

// following-sibling – column value from row identified by different column
private final By emailForUser(String user) {
    return By.xpath("//td[text()='" + user + "']/following-sibling::td[2]");
}
```

---

## Quick Reference

### Most Useful XPath Patterns for the-internet.herokuapp.com

| Page | XPath | Explanation |
|---|---|---|
| Login | `//input[@id='username']` | id-based |
| Login | `//button[contains(@class,'radius')]` | partial class |
| Checkboxes | `//form[@id='checkboxes']/input[1]` | positional child |
| Tables | `//td[text()='Smith']/following-sibling::td` | axis |
| Tables | `//tr[td[1][text()='John']]` | row by cell value |
| Dropdown | `//select[@id='dropdown']` | select element |
| Dropdown | `//option[not(@disabled)]` | exclude disabled |

---

## Running the Tests

```bash
# Full suite
mvn clean test

# Single test class
mvn clean test -Dtest=TablesPageTest

# Generate Allure report
mvn allure:report
mvn allure:serve    # opens in browser automatically
```
