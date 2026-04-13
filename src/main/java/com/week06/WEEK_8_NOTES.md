# Week 8 – Selenide Introduction

## Goals
- Replace raw Selenium WebDriver calls with Selenide's concise API
- Leverage built-in auto-waits instead of explicit `WebDriverWait`
- Keep the same Page Object structure from Week 6 — only the internals change
- Keep Allure integration working with Selenide

---

## Part 1 – What is Selenide?

Selenide is a thin wrapper around Selenium WebDriver that adds:

| Feature | Selenium 4 | Selenide |
|---|---|---|
| Element lookup | `driver.findElement(By.id("x"))` | `$(By.id("x"))` or `$("#x")` |
| Auto-wait | Manual `WebDriverWait` + `ExpectedConditions` | Built-in, every `$()` call waits automatically |
| Assertions | TestNG / JUnit `assertEquals(...)` | Fluent `shouldBe(visible)`, `shouldHave(text("..."))` |
| Screenshots on failure | Manual `TakesScreenshot` code | Automatic — Selenide saves a PNG on every failure |
| Driver management | `WebDriverManager` + explicit `driver.quit()` | `Selenide.closeWebDriver()` or auto-close |
| Select elements | `new Select(el).selectByVisibleText(...)` | `$(sel).selectOption("text")` |
| Checkbox | `el.click()` if `!el.isSelected()` | `$(sel).shouldBe(checked)` / `.setSelected(true)` |

**Core rule:** Selenide waits for the condition to be true before each interaction.  
You almost never need to write a wait yourself.

---

## Part 2 – POM.xml Changes

### 2.1 What to remove / replace

```xml
<!-- REMOVE these from Week 6 pom.xml -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.18.1</version>
</dependency>
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.8.0</version>
</dependency>
```

### 2.2 What to add

```xml
<!-- Selenide – includes Selenium 4 and WebDriverManager internally -->
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide</artifactId>
    <version>7.3.2</version>
</dependency>

<!-- Allure Selenide integration (replaces allure-testng screenshot listener) -->
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-selenide</artifactId>
    <version>2.25.0</version>
</dependency>
```

> **Why only one dependency?**  
> Selenide 7.x already pulls in `selenium-java` and `webdrivermanager` as transitive dependencies.
> You get browser driver auto-management for free — no `WebDriverManager.chromedriver().setup()` needed.

### 2.3 Full properties block (versions)

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

    <selenide.version>7.3.2</selenide.version>
    <testng.version>7.8.0</testng.version>
    <allure.version>2.25.0</allure.version>
    <aspectj.version>1.9.20.1</aspectj.version>
    <lombok.version>1.18.30</lombok.version>
    <slf4j.version>2.0.9</slf4j.version>
</properties>
```

---

## Part 3 – Selenide Basics

### 3.1 Opening a page

```java
// Selenium
driver.get("https://the-internet.herokuapp.com/login");

// Selenide
open("https://the-internet.herokuapp.com/login");
// or with a relative path (if baseUrl is configured):
open("/login");
```

---

### 3.2 Finding elements — `$` and `$$`

```java
// Selenium
WebElement btn = driver.findElement(By.cssSelector("button.radius"));

// Selenide – using CSS shorthand
SelenideElement btn = $("button.radius");

// Selenide – using By locator (same as Selenium By)
SelenideElement btn = $(By.xpath("//button[contains(@class,'radius')]"));

// Multiple elements
ElementsCollection rows = $$("table#table1 tbody tr");
ElementsCollection rows = $$(By.xpath("//table[@id='table1']//tbody/tr"));
```

`$` and `$$` are static imports from `com.codeborne.selenide.Selenide`.

---

### 3.3 Conditions — `shouldBe` / `shouldHave`

```java
// Selenium
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
assertTrue(driver.findElement(By.id("flash")).isDisplayed());

// Selenide
$("#flash").shouldBe(visible);
```

```java
// Text assertion
// Selenium
assertEquals(driver.findElement(By.id("flash")).getText().trim(), "You logged in");

// Selenide
$("#flash").shouldHave(text("You logged in"));         // partial match
$("#flash").shouldHave(exactText("You logged in"));    // exact match
```

Common conditions:

| Condition | Meaning |
|---|---|
| `visible` | `isDisplayed() == true` |
| `hidden` | `isDisplayed() == false` |
| `enabled` | element is not disabled |
| `checked` | checkbox is ticked |
| `text("foo")` | `.getText()` contains "foo" |
| `exactText("foo")` | `.getText().trim().equals("foo")` |
| `value("foo")` | `value` attribute equals "foo" |
| `attribute("href", "/login")` | named attribute equals value |
| `cssClass("active")` | has CSS class |
| `empty` | text is blank |

---

### 3.4 Custom timeout

```java
// Wait up to 15 s for this element specifically
$("#slow-element").shouldBe(visible, Duration.ofSeconds(15));
```

---

### 3.5 Interactions

```java
$("input#username").val("tomsmith");        // clear + type (equivalent to clear+sendKeys)
$("input#username").setValue("tomsmith");   // same as val()
$("button.radius").click();
$("select#dropdown").selectOption("Option 1");
$("input[type=checkbox]").setSelected(true);
$("a[href='/logout']").click();
```

---

### 3.6 Getting values

```java
String text  = $("#flash").getText();
String value = $("input#username").getValue();
boolean shown = $("div.error").is(visible);       // non-throwing check
```

---

### 3.7 Collections — `$$`

```java
ElementsCollection rows = $$(By.xpath("//table[@id='table1']//tbody/tr"));
int count = rows.size();

// Assert all visible
rows.shouldHave(CollectionCondition.sizeGreaterThan(0));
rows.shouldHave(CollectionCondition.size(4));

// Get texts of all cells
List<String> texts = rows.texts();

// Filter
ElementsCollection checked = $$(By.xpath("//input[@type='checkbox']"))
        .filterBy(checked);
```

---

### 3.8 Selenide Configuration

Configure once — typically in a `@BeforeSuite` or a static initialiser:

```java
// Browser
Configuration.browser = "chrome";          // default
Configuration.browser = "firefox";

// Headless
Configuration.headless = true;

// Base URL (enables open("/login") shorthand)
Configuration.baseUrl = "https://the-internet.herokuapp.com";

// Global timeout for all auto-waits (ms)
Configuration.timeout = 10_000;

// Screenshot and report dir
Configuration.reportsFolder = "target/selenide-reports";
Configuration.savePageSource = false;
```

---

## Part 4 – Allure + Selenide

Add `AllureSelenide` listener once — in `BaseTest @BeforeSuite` or in `SelenideConfig`:

```java
SelenideLogger.addListener("allure", new AllureSelenide()
        .screenshots(true)          // attach screenshot to Allure on every failure
        .savePageSource(false));    // skip HTML source attachment (optional)
```

This replaces the manual `TakesScreenshot` code from Week 6 `BaseTest.tearDown()`.  
Selenide captures screenshots automatically; you no longer need to call `attachScreenshot()`.

---

## Part 5 – Migration Map (Week 6 → Week 8)

### BasePage

| Week 6 (Selenium) | Week 8 (Selenide) |
|---|---|
| `WebDriver driver` field | No driver field — use `$()` static methods |
| `WebDriverWait wait` field | No wait field — built into `$()` |
| `waitForVisible(By)` → returns `WebElement` | `$(By).shouldBe(visible)` |
| `clickWhenReady(By)` | `$(By).click()` |
| `typeInto(By, text)` | `$(By).val(text)` |
| `getText(By)` | `$(By).getText()` |
| `findAll(By)` → `List<WebElement>` | `$$(By)` → `ElementsCollection` |
| `isDisplayed(By)` | `$(By).is(visible)` |
| `isSelected(By)` | `$(By).is(checked)` |
| `selectByVisibleText(By, text)` | `$(By).selectOption(text)` |
| `getSelectedOption(By)` | `$(By).getSelectedOption().getText()` |

### DriverFactory / BaseTest

| Week 6 | Week 8 |
|---|---|
| `WebDriverManager.chromedriver().setup()` | Not needed |
| `new ChromeDriver(options)` | Handled by Selenide via `Configuration.browser` |
| `driver.manage().window().maximize()` | `Configuration.browserSize = "1920x1080"` |
| `driver.quit()` in `@AfterMethod` | `Closewebdriver()` or just call `closeWebDriver()` |
| Manual screenshot in `@AfterMethod` | `AllureSelenide` listener does it automatically |

---

## Part 6 – Key Gotchas

1. **Do not mix Selenide `$()` with raw `driver.findElement()`** in the same test run  
   unless you explicitly retrieve the driver via `WebDriverRunner.getWebDriver()`.

2. **`open()` must be called before any `$()`** — it triggers driver initialisation.

3. **`Configuration.baseUrl` must end without a slash**: `"https://the-internet.herokuapp.com"` ✅  
   `"https://the-internet.herokuapp.com/"` ❌ (double slash in combined URL).

4. **Selenide's `$()` is lazy** — the element is not found until you interact with it  
   (same as Page Factory proxy). A line like `SelenideElement btn = $("button")` does NOT fail  
   immediately even if the button doesn't exist yet.

5. **AllureSelenide listener must be added before `open()`** otherwise the first navigation  
   is not captured.

---

## Running

```bash
mvn clean test                        # full suite
mvn clean test -Dheadless=true        # headless via system property (read in SelenideConfig)
mvn allure:serve
```
