# Week 7 – UI Test Architecture & Patterns

## Goals
- Understand the difference between Classic POM, Page Factory, and Fluent POM
- Apply `isPageOpened()` contract on every page
- Use Screen/State objects for complex UIs
- Structure code into clean layers: config → data → pages → tests
- Recognise and fix the most common anti-patterns

---

## Part 1 – Page Object Variants

### 1.1 Classic Page Object (Week 6 recap)

```java
// Locator stored as a field
private final By usernameInput = By.id("username");

// Used explicitly in a method
public void enterUsername(String value) {
    driver.findElement(usernameInput).sendKeys(value);
}
```

**Pros:** Simple, explicit, no magic.  
**Cons:** More boilerplate; locator and element are separate objects.

---

### 1.2 Page Factory

`PageFactory` is a Selenium support class that uses **annotations** to declare locators
and **lazy-initialises** `WebElement` fields via a proxy.

```java
@FindBy(id = "username")
private WebElement usernameInput;   // proxy – not found until first use

// Initialise in constructor:
PageFactory.initElements(driver, this);
```

The proxy calls `driver.findElement(...)` every time you interact with the field,
so the element is always fresh (no `StaleElementReferenceException` from stale cache).

#### Additional @FindBy strategies

```java
@FindBy(css = "button.radius")
@FindBy(xpath = "//h4[@class='subheader']")
@FindBy(name = "username")
@FindBy(linkText = "Logout")

// Multiple locators – tries each in order, returns first match
@FindBys({
    @FindBy(id = "loginBtn"),
    @FindBy(css = "button[type='submit']")
})

// All elements matching ANY of the locators
@FindAll({
    @FindBy(className = "error"),
    @FindBy(className = "flash")
})
```

#### Page Factory vs Classic POM – comparison

| Aspect | Classic POM (`By`) | Page Factory (`@FindBy`) |
|---|---|---|
| Locator declaration | `By` field | `@FindBy` annotation |
| Element lookup | On every `driver.findElement(By)` call | Lazy proxy on first access |
| Stale element handling | Manual re-find | Proxy re-finds automatically |
| Readability | Explicit, verbose | Concise annotations |
| `WebDriverWait` | Works naturally | Needs care (proxy finds, but may be not-visible yet) |
| Dynamic locators | Easy (`By.xpath(...)` with string param) | Hard (annotations are compile-time constants) |
| Recommended for | Complex/dynamic locators | Stable, simple element declarations |

**Rule of thumb:** Use Page Factory for stable pages with static locators.  
Use Classic POM when you need dynamic/parameterised XPath.

---

### 1.3 Fluent Page Object

Every action method returns either:
- `this` – if the action keeps the user on the same page
- `new AnotherPage(driver)` – if the action navigates

```java
loginPage
    .open()
    .enterUsername("tomsmith")
    .enterPassword("SuperSecretPassword!")
    .clickLogin()              // returns SecureAreaPage
    .assertWelcomeVisible()    // returns SecureAreaPage
    .clickLogout();            // returns LoginPage
```

This makes tests read like a **user story script**, not a list of driver commands.

---

### 1.4 Loadable Component / isPageOpened()

Every page class should expose a guard method that asserts the page is fully loaded
before test interaction begins. This catches navigation failures early with a clear message.

```java
public LoginPage isPageOpened() {
    wait.until(ExpectedConditions.urlContains("/login"));
    wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
    return this;
}
```

Usage pattern:

```java
LoginPage loginPage = new LoginPage(driver).open().isPageOpened();
```

The `open()` navigates; `isPageOpened()` blocks until the page is ready and fails fast
if the expected URL or element never appears.

---

### 1.5 Screen / State Object

For **complex pages** with multiple distinct UI states (e.g. a table that can be
sorted, filtered, paginated), a Screen object encapsulates the **current visible state**
and provides assertion helpers.

```java
TablesScreenState state = new TablesScreenState(driver);
state.assertRowExists("Smith")
     .assertColumnHeaders("Last Name", "First Name", "Email", "Due", "Web Site", "Action")
     .assertRowCount(4);
```

The Screen object does **not** perform actions — it reads and asserts.  
This separates "doing" (page actions) from "verifying" (screen state).

---

## Part 2 – Layer Separation

### 2.1 The Four Layers

```
┌──────────────────────────────────────────────────────┐
│  TEST LAYER                                           │
│  week07.*Test  – orchestrates steps, asserts        │
├──────────────────────────────────────────────────────┤
│  PAGE / SCREEN LAYER                                  │
│  .pages.*  – locators + actions            │
│  .screen.* – state/assertion helpers       │
├──────────────────────────────────────────────────────┤
│  DATA LAYER                                           │
│  .data.model.*   – domain objects          │
│  .data.builders.* – test data factories    │
├──────────────────────────────────────────────────────┤
│  CONFIG LAYER                                         │
│  .config.TestConfig – base URL, timeouts  │
└──────────────────────────────────────────────────────┘
```

Each layer talks **only to the layer directly below** it.  
Tests call pages; pages never call test assertions; data builders know nothing about pages.

---

### 2.2 TestConfig

Centralises all configuration values. Uses Java system properties so values
can be overridden from the command line or a CI pipeline:

```bash
mvn test -Dbase.url=https://staging.example.com -Dtimeout=15
```

```java
String url     = TestConfig.getBaseUrl();   // default: https://the-internet.herokuapp.com
int    timeout = TestConfig.getTimeout();   // default: 10
```

---

### 2.3 Test Data Builders (Builder Pattern)

The **Builder pattern** separates test data construction from the test logic.

```java
User user = new UserBuilder()
        .withUsername("tomsmith")
        .withPassword("SuperSecretPassword!")
        .build();

loginPage.enterUsername(user.getUsername())
         .enterPassword(user.getPassword());
```

Benefits:
- Default values in one place
- Easy to create variations: `UserBuilder.validUser()`, `UserBuilder.invalidUser()`
- Tests read business intent, not magic strings

---

## Part 3 – Anti-patterns

### AP-1: Business logic inside test methods

```java
// ❌ BAD – test manually constructs the full URL
driver.get("https://the-internet.herokuapp.com" + "/login");
driver.findElement(By.id("username")).sendKeys("tomsmith");
```

```java
// ✅ GOOD – page object handles navigation and interaction
new LoginPage(driver).open().enterUsername("tomsmith");
```

---

### AP-2: Duplicated locators

```java
// ❌ BAD – same XPath in three test methods
driver.findElement(By.xpath("//button[contains(@class,'radius')]")).click();
// ... 50 lines later ...
driver.findElement(By.xpath("//button[contains(@class,'radius')]")).click();
```

```java
// ✅ GOOD – declared once in the page class
private final By loginButton = By.xpath("//button[contains(@class,'radius')]");
```

---

### AP-3: Hardcoded Thread.sleep

```java
// ❌ BAD – arbitrary pause; fails on slow machines, wastes time on fast ones
Thread.sleep(3000);
driver.findElement(By.id("flash")).getText();
```

```java
// ✅ GOOD – explicit wait, precise condition
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
driver.findElement(By.id("flash")).getText();
```

---

### AP-4: Driver calls in test methods

```java
// ❌ BAD – test reaches directly into WebDriver
driver.findElement(By.id("username")).sendKeys("tom");
driver.findElement(By.cssSelector("button.radius")).click();
assertTrue(driver.findElement(By.id("flash")).getText().contains("You logged"));
```

```java
// ✅ GOOD – test delegates everything to the page object
new LoginPage(driver).open()
    .enterUsername("tomsmith")
    .enterPassword("SuperSecretPassword!")
    .clickLogin()
    .assertWelcomeVisible();
```

---

### AP-5: Hardcoded test data in test methods

```java
// ❌ BAD – magic strings scattered everywhere
loginPage.enterUsername("tomsmith");
loginPage.enterPassword("SuperSecretPassword!");
```

```java
// ✅ GOOD – data comes from a builder with a named factory method
User user = UserBuilder.validUser();
loginPage.enterUsername(user.getUsername())
         .enterPassword(user.getPassword());
```

---

## Part 4 – Project Structure (Week 7 additions)

```
src/main/java/week07/
├── config/
│   └── TestConfig.java               ← base URL, timeout from system properties
├── data/
│   ├── model/
│   │   └── User.java                 ← domain object
│   └── builders/
│       └── UserBuilder.java          ← builder + static factory methods
├── pages/
│   ├── (Week 6 pages – unchanged)
│   ├── fluent/
│   │   └── FluentLoginPage.java      ← full fluent chain + isPageOpened()
│   └── factory/
│       ├── LoginPageFactory.java     ← PageFactory style
│       └── TablesPageFactory.java    ← PageFactory style
├── screen/
│   ├── TablesScreenState.java        ← assertion helpers for tables page
│   └── LoginScreenState.java        ← assertion helpers for login page
└── antipatterns/
    └── LoginPageAntiPattern.java     ← intentional bad code, educational

src/test/java/week07/
├── BaseTest.java                     ← same as week 6
├── LoginFactoryTest.java             ← uses PageFactory page
├── FluentLoginTest.java              ← uses FluentLoginPage + UserBuilder
├── ScreenStateTest.java              ← uses TablesScreenState
└── AntiPatternsTest.java             ← shows bad code side by side with good
```

---

## Quick Comparison: All Three Styles on the Same Scenario

```java
// ── Classic POM (Week 6) ──────────────────────────────────────────────────
LoginPage page = new LoginPage(driver);
page.open(BASE_URL);
page.enterUsername("tomsmith");
page.enterPassword("SuperSecretPassword!");
SecureAreaPage secure = page.clickLogin();
assertTrue(secure.isFlashDisplayed());

// ── Page Factory ──────────────────────────────────────────────────────────
LoginPageFactory page = new LoginPageFactory(driver);
page.open(BASE_URL);
page.enterUsername("tomsmith");
page.enterPassword("SuperSecretPassword!");
assertTrue(page.isFlashDisplayed());

// ── Fluent POM ────────────────────────────────────────────────────────────
User user = UserBuilder.validUser();

new FluentLoginPage(driver)
    .open()
    .isPageOpened()
    .enterUsername(user.getUsername())
    .enterPassword(user.getPassword())
    .clickLogin()
    .assertWelcomeVisible();
```

---

## Running Week 7 Tests

```bash
mvn clean test -Dsuite=testng-week7.xml
mvn allure:serve
```
