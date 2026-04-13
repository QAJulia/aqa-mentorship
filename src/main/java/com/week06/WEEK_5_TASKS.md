# Week 5: Homework Tasks

## Before You Start

1. Pull the Week 5 branch / folder from your repository
2. Make sure the project builds: `mvn clean compile -q`
3. Open `src/test/resources/config.properties` and confirm `browser=chrome`
4. Run the provided tests to see them pass before making changes

---

## Task 1 — Get the Setup Working

**Goal:** verify that Selenium + WebDriverManager is configured correctly.

**Steps:**
1. Add the Selenium and WebDriverManager dependencies to `pom.xml` (see notes Section 2)
2. Run `LoginTest` → both tests must pass (a real Chrome window will open and close)
3. Change `browser=chrome` to `browser=firefox` in `config.properties`, run again

**Definition of done:**
- Both `testSuccessfulLogin` and `testFailedLogin` pass with Chrome
- You can switch to Firefox without changing any Java code

---

## Task 2 — Checkboxes Test

**Goal:** practice finding elements and reading their state.

**Target page:** https://the-internet.herokuapp.com/checkboxes

The page has two checkboxes. Checkbox 1 is unchecked by default. Checkbox 2 is checked.

**Write a new test class `CheckboxTest extends BaseUiTest`** with the following tests:

### Test 1: `testCheckboxInitialState`
- Open the checkboxes page
- Assert that checkbox 1 is **NOT** selected (`isSelected()` returns false)
- Assert that checkbox 2 IS selected (`isSelected()` returns true)

### Test 2: `testToggleCheckbox`
- Open the checkboxes page
- Click checkbox 1 to select it
- Assert it is now selected

**Hint:**  
The checkboxes can be found with:
```java
List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
// checkboxes.get(0) → first checkbox
// checkboxes.get(1) → second checkbox
```

> `driver.findElements` (plural) returns a list of all matching elements.

---

## Task 3 — Explicit Wait: Dynamic Loading

**Goal:** learn when and how to use explicit wait.

**Target page:** https://the-internet.herokuapp.com/dynamic_loading/1

There is a hidden element that appears only after you click "Start" and wait.

**Write `DynamicLoadingTest extends BaseUiTest`** with:

### Test: `testDynamicElementAppears`
1. Open the dynamic loading page
2. Click the "Start" button
3. Use **explicit wait** to wait until the text "Hello World!" appears  
   (do NOT use `Thread.sleep`)
4. Assert the text equals "Hello World!"

**Hint — the explicit wait pattern:**
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
);
Assert.assertEquals(element.getText(), "Hello World!");
```

---

## Task 4 — Add Allure Annotations

**Goal:** enrich all your new tests with Allure metadata.

For every test class you create (Task 2 and Task 3), add:
- `@Epic("UI Tests")` on the class
- `@Feature("...")` on the class (e.g., `"Checkboxes"`, `"Dynamic Loading"`)
- `@Story("...")` on each test method
- `@Description("...")` on each test method (one sentence describing what it does)
- `@Step("...")` on each valuable step and validation

Run the tests and generate the Allure report:
```
mvn clean test
mvn allure:serve
```

Verify your tests appear in the report with correct Epic / Feature / Story grouping.

---

## Task 5 — Add Your Tests to the XML Suite

**Goal:** run all UI tests together via `testng-ui.xml`.

1. Open `src/test/resources/testng-ui.xml`
2. Add your new test classes (`CheckboxTest`, `DynamicLoadingTest`) to the suite
3. Run the suite:
```
mvn test -DsuiteXmlFile=testng-ui.xml
```
All tests must pass.

---

## Bonus Task — Broken Images Page

**Target page:** https://the-internet.herokuapp.com/broken_images

The page contains several images. Some are broken (fail to load).

**Write `BrokenImagesTest extends BaseUiTest`:**

### Test: `testCountBrokenImages`
1. Find all `<img>` elements on the page: `driver.findElements(By.tagName("img"))`
2. For each image, get the `src` attribute
3. Count how many images have a `naturalWidth` of 0 using JavaScript execution:
```java
JavascriptExecutor js = (JavascriptExecutor) driver;
Long naturalWidth = (Long) js.executeScript(
    "return arguments[0].naturalWidth;", imgElement
);
```
4. Print the count of broken images to console
5. Assert that broken images count is greater than 0 (the page has known broken images)

> This task introduces `JavascriptExecutor` — a way to run JavaScript inside the browser  
> directly from your test code. We will use it more in later weeks.

---

## Questions to Think About

1. What is the difference between `driver.findElement` and `driver.findElements`?
2. Why do we call `driver.quit()` in `@AfterMethod` and not `@AfterClass`?
3. What happens if an element has no `id` attribute — which locator do you use next?
4. Why is implicit wait not always enough?
