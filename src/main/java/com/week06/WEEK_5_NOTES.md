# Week 5: Selenium WebDriver Basics

## What You'll Learn

By the end of this week, you will:
1. Understand what Selenium WebDriver is and how it controls a browser
2. Set up Selenium + WebDriverManager in your Maven project
3. Use the most important locators: `id`, `name`, `className`, `cssSelector`
4. Handle waits: implicit and explicit
5. Write 2–3 real UI test scenarios against a demo web app
6. Create a reusable `BaseUiTest` class so setup/teardown is not repeated
7. Read browser and URL settings from a config file

> **Note on XPath:** XPath is covered in a separate dedicated session. This week focuses on  
> `id`, `name`, `className`, and `cssSelector` locators only.

---

## 1. What is Selenium WebDriver?

Selenium WebDriver is a Java library that lets your code **control a real browser** — Chrome,  
Firefox, Edge — just like a human would: open a URL, click a button, type text, read content.

```
Your Java test
      ↓
Selenium WebDriver API
      ↓
ChromeDriver / GeckoDriver (browser-specific driver)
      ↓
Real Browser (Chrome / Firefox)
```

The browser driver is a separate executable that Selenium talks to. Managing it manually is  
tedious — that is why we use **WebDriverManager**, which downloads and configures the right  
driver automatically.

---

## 2. Dependencies — What to Add to pom.xml

```xml
<!-- Selenium WebDriver -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.20.0</version>
</dependency>

<!-- WebDriverManager — downloads ChromeDriver / GeckoDriver automatically -->
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.8.0</version>
</dependency>
```

> **Why WebDriverManager?**  
> Without it you need to manually download `chromedriver.exe`, keep it updated, and set the  
> system property path. WebDriverManager does all of that in one line of code.  
> 📖 [WebDriverManager GitHub](https://github.com/bonigarcia/webdrivermanager)

---

## 3. Your First WebDriver Test (step by step)

```java
// 1. Ask WebDriverManager to set up ChromeDriver
WebDriverManager.chromedriver().setup();

// 2. Create a Chrome browser instance
WebDriver driver = new ChromeDriver();

// 3. Open a URL
driver.get("https://the-internet.herokuapp.com/login");

// 4. Find an element and interact with it
driver.findElement(By.id("username")).sendKeys("tomsmith");
driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
driver.findElement(By.cssSelector("button[type='submit']")).click();

// 5. Assert something
String successText = driver.findElement(By.cssSelector(".flash.success")).getText();
Assert.assertTrue(successText.contains("You logged into a secure area!"));

// 6. Close the browser
driver.quit();
```

This is the raw form. Later we move setup/teardown into `BaseUiTest` so tests stay clean.

---

## 4. Locators — How to Find Elements

A **locator** tells Selenium *which element on the page* you want to interact with.

### 4.1 `By.id`

The fastest and most reliable locator. Use it when the element has a unique `id` attribute.

```html
<input id="username" type="text">
```
```java
driver.findElement(By.id("username")).sendKeys("tomsmith");
```

### 4.2 `By.name`

Used when the element has a `name` attribute (common in HTML forms).

```html
<input name="email" type="email">
```
```java
driver.findElement(By.name("email")).sendKeys("test@example.com");
```

### 4.3 `By.className`

Finds elements by their CSS class. If an element has multiple classes, match any one of them.

```html
<div class="flash success">You logged in!</div>
```
```java
// Works fine if the class name is unique enough
driver.findElement(By.className("success")).getText();
```

> ⚠️ `By.className` does NOT work with compound class names that have a space,  
> e.g. `By.className("flash success")` will throw an error. Use `cssSelector` instead.

### 4.4 `By.cssSelector`

The most flexible locator (after XPath). CSS selectors are the same syntax used in CSS stylesheets.

| Pattern | Meaning | Example |
|---------|---------|---------|
| `#id` | by id | `#username` |
| `.class` | by class | `.success` |
| `tag` | by tag | `button` |
| `tag[attr='val']` | by attribute | `button[type='submit']` |
| `.parent .child` | descendant | `.form-group input` |
| `tag.class` | tag with class | `div.flash` |

```java
// Find a submit button by its type attribute
driver.findElement(By.cssSelector("button[type='submit']")).click();

// Find an input inside a specific div
driver.findElement(By.cssSelector("#login-form input[name='username']")).sendKeys("tom");

// Find an element with two classes
driver.findElement(By.cssSelector(".flash.success")).getText();
```

📖 [CSS Selectors reference – MDN](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_selectors)  
📖 [CSS Selectors cheatsheet](https://www.w3schools.com/cssref/css_selectors.php)

### Locator Priority (rule of thumb)

```
id  →  name  →  cssSelector  →  xpath (covered separately)
```

Always prefer the simplest stable locator. If you have an `id`, use it.

---

## 5. Basic Interactions

```java
WebElement element = driver.findElement(By.id("username"));

element.sendKeys("text");       // type into input
element.click();                // click button/link/checkbox
element.clear();                // clear an input field
element.getText();              // read element's visible text
element.getAttribute("href");   // read an HTML attribute value
element.isDisplayed();          // is element visible? returns boolean
element.isEnabled();            // is element enabled? returns boolean
```

### Navigation

```java
driver.get("https://example.com");    // open URL
driver.navigate().back();             // browser back button
driver.navigate().forward();          // browser forward button
driver.navigate().refresh();          // refresh page
driver.getTitle();                    // get page title
driver.getCurrentUrl();               // get current URL
```

---

## 6. Waits — Why Your Tests Might Fail Without Them

Modern web pages load content dynamically (JavaScript). If Selenium tries to find an element  
before it appears on the page, it throws `NoSuchElementException`.

There are two main types of waits:

### 6.1 Implicit Wait

Set once, applies globally to every `findElement` call. Tells the driver:  
*"wait up to N seconds for an element to appear before throwing an exception."*

```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
```

**Where to set it:** in `BaseUiTest`, after creating the driver.

**Downside:** it makes every failed lookup wait the full timeout — can slow down negative  
path tests.

### 6.2 Explicit Wait (introduction)

Waits for a *specific condition* on a *specific element*.

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait until element is visible
WebElement message = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".flash.success"))
);
```

Common `ExpectedConditions`:

| Condition | Meaning |
|-----------|---------|
| `visibilityOfElementLocated(by)` | Element is in DOM and visible |
| `elementToBeClickable(by)` | Element is visible and enabled |
| `textToBePresentInElementLocated(by, text)` | Element contains text |
| `urlContains(text)` | Current URL contains text |

📖 [Selenium Waits – official docs](https://www.selenium.dev/documentation/webdriver/waits/)

### ❌ Never do this

```java
Thread.sleep(3000); // Hard-coded sleep — slow, fragile, unprofessional
```

---

## 7. BaseUiTest — The Reusable Foundation

Instead of writing `WebDriverManager.chromedriver().setup()` and `driver.quit()` in every  
test, we put it in a base class. All UI test classes extend it.

```java
public class BaseUiTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        String browser = ConfigReader.get("browser");   // read from config.properties
        driver = DriverFactory.createDriver(browser);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();  // always close the browser, even if test failed
        }
    }
}
```

**Key rules:**
- `@BeforeMethod` → opens a fresh browser before each test
- `@AfterMethod` → closes the browser after each test, even if the test fails
- `driver` is `protected` so child test classes can use it

---

## 8. DriverFactory — Supporting Multiple Browsers

```java
public class DriverFactory {

    public static WebDriver createDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver();
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            default:
                throw new IllegalArgumentException("Unknown browser: " + browser);
        }
    }
}
```

This means switching from Chrome to Firefox is just changing one line in `config.properties`.

---

## 9. ConfigReader — Reading Settings from a File

Hard-coding URLs or browser names in tests is bad practice. Use a properties file instead.

`src/test/resources/config.properties`:
```properties
browser=chrome
base.url=https://the-internet.herokuapp.com
```

`ConfigReader.java`:
```java
public class ConfigReader {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load config.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
```

Usage in tests:
```java
driver.get(ConfigReader.get("base.url") + "/login");
```

---

## 10. Demo Application

This week we test against **[The Internet – Heroku app](https://the-internet.herokuapp.com)**,  
a site built specifically for practicing Selenium.

Pages we will use:

| Page | URL | What to test |
|------|-----|--------------|
| Login | `/login` | Fill form, assert success/failure |
| Checkboxes | `/checkboxes` | Check/uncheck, assert state |
| Dynamic Loading | `/dynamic_loading/1` | Explicit wait practice |

---

## 11. Full Scenario Example — Login Test

```java
@Epic("UI Tests")
@Feature("Login")
public class LoginTest extends BaseUiTest {

    private static final String BASE_URL = ConfigReader.get("base.url");

    @Test
    @Story("Successful login")
    @Description("User logs in with valid credentials")
    public void testSuccessfulLogin() {
        driver.get(BASE_URL + "/login");

        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String message = driver.findElement(By.cssSelector(".flash.success")).getText();
        Assert.assertTrue(message.contains("You logged into a secure area!"),
                "Expected success message not found");
    }

    @Test
    @Story("Failed login")
    @Description("User logs in with wrong password")
    public void testFailedLogin() {
        driver.get(BASE_URL + "/login");

        driver.findElement(By.id("username")).sendKeys("tomsmith");
        driver.findElement(By.id("password")).sendKeys("wrongpassword");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        String message = driver.findElement(By.cssSelector(".flash.error")).getText();
        Assert.assertTrue(message.contains("Your password is invalid!"),
                "Expected error message not found");
    }
}
```

---

## 12. Selenium WebDriver — Big Picture

```
config.properties  →  ConfigReader
                            ↓
                       DriverFactory  →  ChromeDriver / FirefoxDriver
                            ↓
                        BaseUiTest  (@BeforeMethod / @AfterMethod)
                            ↓
                   LoginTest / CheckboxTest / ...  (your test classes)
```

Each test class:
1. Extends `BaseUiTest` — gets a fresh `driver` automatically
2. Reads base URL from `ConfigReader`
3. Uses Allure annotations for reporting

---

## Key Takeaways

| Concept | What | Why |
|---------|------|-----|
| **WebDriver** | Controls real browser | End-to-end testing |
| **WebDriverManager** | Auto-downloads browser driver | No manual setup |
| **By.id / By.cssSelector** | Find elements on page | Core Selenium skill |
| **Implicit wait** | Global timeout for element lookup | Avoids flaky `NoSuchElement` errors |
| **Explicit wait** | Wait for specific condition | Precise control over dynamic content |
| **BaseUiTest** | Shared setup/teardown | DRY — Don't Repeat Yourself |
| **DriverFactory** | Creates driver by name | Easy browser switching |
| **ConfigReader** | Reads browser/URL from file | No hardcoded values in tests |

---

## Resources

- [Selenium WebDriver official docs](https://www.selenium.dev/documentation/webdriver/)
- [WebDriverManager GitHub](https://github.com/bonigarcia/webdrivermanager)
- [The Internet – demo app for Selenium practice](https://the-internet.herokuapp.com)
- [CSS Selectors – MDN](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_selectors)
- [Selenium Waits – official docs](https://www.selenium.dev/documentation/webdriver/waits/)
- [ExpectedConditions JavaDoc](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/ui/ExpectedConditions.html)
