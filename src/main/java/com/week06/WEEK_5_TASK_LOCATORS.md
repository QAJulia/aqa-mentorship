# WEEK 5 – Locators Tasks

## Training web pages

Use this site for practice:
- https://the-internet.herokuapp.com/  
  You can also use:
- https://demoqa.com/ for extra practice.

## General rules

- Write locators as if you were using Selenium Java with TestNG.
- Prefer stable, readable locators.
- For each task, prepare at least:
    - 1 locator by `id` or `name` (if possible).
    - 1 locator by `cssSelector`.
    - 1 locator by `xpath`.

---

## Task 1 – Login form locators

**Page:** https://the-internet.herokuapp.com/login

1. Create locators for:
    - Username input.
    - Password input.
    - Login button.
    - Error message shown after invalid login.
2. For each element, write:
    - `By.id` / `By.name` (if available).
    - `By.cssSelector`.
    - `By.xpath` (relative, not absolute).
3. For XPath, add one version using an axis (for example `following-sibling` or `descendant`).

---

## Task 2 – Checkboxes

**Page:** https://the-internet.herokuapp.com/checkboxes

1. Create locators for both checkboxes.
2. Create one locator that selects all checkboxes on the page.
3. Create an XPath using position or index for the second checkbox.
4. Create an XPath using an axis (for example `descendant` or `following-sibling`).

---

## Task 3 – Dropdown

**Page:** https://the-internet.herokuapp.com/dropdown

1. Create a locator for the dropdown element itself.
2. Create locators for option 1 and option 2 using:
    - `cssSelector`.
    - `xpath` with attributes.
3. Create an XPath that selects an option by visible text using the `text()` function.

---

## Task 4 – Dynamic content with axes

**Page:** https://the-internet.herokuapp.com/large

1. Use DevTools to inspect a cell in the middle of the large table.
2. Build an XPath that:
    - Starts from that cell and finds its parent row using `parent::` or `ancestor::`.
    - From that row, selects the next row using `following-sibling::`.
3. Explain in comments which axis you used and why.

---

## Task 5 – Using DevTools to refine XPath

**Page:** Any page from https://the-internet.herokuapp.com/

1. Pick one element with a long or complex auto-generated XPath from DevTools.
2. Copy the generated absolute XPath.
3. Manually simplify it to a shorter relative XPath using:
    - `id` or `name` attributes.
    - `data-*` attributes if they exist.
    - A combination of attributes and axes (for example `ancestor::div[@class='example']`).
4. Write down the original absolute XPath and your improved relative XPath.

---

## Bonus Task – Data-test attributes

If a demo project provides custom `data-test` or `data-qa` attributes (for example on https://demoqa.com/):

1. Find 3 elements that have `data-*` attributes.
2. Create locators for them using:
    - `By.cssSelector("[data-test='value']")`.
    - `By.xpath("//*[@data-test='value']")`.
3. Explain why `data-test` attributes are useful for automated tests (stability, not visible to users).
