# Week 3 Homework: Testing Product Discount Calculator with Allure

## Goal
Test a product discount calculation system using TestNG, Allure reporting, and custom listeners. 
Practice positive/negative testing with detailed Allure steps and attachments.

## Pay attention
This is your 1st output project in this program, create a fresh repo for it. It must sum up all knowledge you got from weeks 1-3. 
You will have to use your knowledge in testng (pom, tests, xml, before/after hooks), listeners, 
allure reporting, data providers. What will be important and discussed in a feedback:
1. Project Structure - appropriate folder structure, files and classes naming
2. Used hooks and test listeners (IListeners nd Before-After hooks). You can use only 1, but do it in a correct place
3. For sure data provider and tests must work :) 
4. Allure reporting structure and correctness

---

## What You're Building

**Scenario:** E-commerce system applies discounts to products. You need to test the discount calculation logic.

**Domain Models:**
1. `Product` - represents a product with price
2. `Discount` - discount rules (percentage or fixed amount)
3. `DiscountResult` - calculation result with savings

**Business Logic:** `DiscountCalculator.applyDiscount()` - calculates final price after discount


## Task 1: Create Domain Models

### 1.1 Product Model

**Location:** `src/main/java/com/week03/models/Product.java`
Create a Product model with fields: int id, String name, double price, String category.

### 1.2 Discount Model

**Location:** `src/main/java/com/week03/models/Discount.java`
Create a Discount model with fields: double value, String description, enum DiscountType type

### 1.3 DiscountResult Model

**Location:** `src/main/java/com/week03/models/DiscountResult.java`
Create a model for DiscountResult with fields: double originalPrice, double discountedPrice, double savedAmount, 
boolean isValid, String message

---

## Task 2: Create DiscountCalculator Service

**Location:** `src/main/java/com/week03/services/DiscountCalculator.java`

**Requirements:**
- Method: `applyDiscount(Product product, Discount discount)` returns `DiscountResult`
- For `PERCENTAGE`: final price = price * (1 - value/100)
- For `FIXED`: final price = price - value
- Validation:
  - Discount value must be positive
  - Final price cannot be negative (discount too large)
  - If invalid, return result with `isValid = false` and error message


## Task 3: Create Allure Step Methods

**Location:** `src/main/java/com/week03/steps/DiscountTestSteps.java`

**Requirements:**
Create methods with `@Step` annotation for Creating product, creating discount, apply discount, verify discounted price,
verify is result is valid. For every Step add attachments with Objects of used Products, Discounts etc. For every 
validation in Allure must be visible expected and actual results.

---

## Task 4: Create Test Execution Listener

**Location:** `src/main/java/com/week03/listeners/TestExecutionListener.java`

**Requirements:**
- Implement `ITestListener`
- Track: test start, test success, test failure, test skipped
- Print to console with status symbols: ▶ (start), ✓ (pass), ✗ (fail), ⊘ (skip)
- On failure: attach error to Allure

---

## Task 5: Create Test Data File

**Location:** `src/test/resources/week03/testData/discounts.csv`
Example of csv (you can use whatever you want):

```csv
productId,productName,price,category,discountType,discountValue,expectedPrice
1,Laptop,1000.0,Electronics,PERCENTAGE,10,900.0
2,Phone,500.0,Electronics,FIXED,50,450.0
3,Headphones,200.0,Electronics,PERCENTAGE,25,150.0
4,Mouse,30.0,Accessories,FIXED,5,25.0
```

---

## Task 6: Create Test Class

**Location:** `src/test/java/week03/DiscountCalculatorTests.java`

**Requirements:**
- Use `@Epic`, `@Feature`, `@Story`, `@Severity` annotations
- Create at least 5 tests (or more):
  - at least 2 positive (percentage discount, fixed discount) - using DataProvider
  - at least 2 negative (discount too large, invalid discount value)
  - at least 1 intentionally failing test (wrong expected result)

BONUS: Create few csvs and in xml put it's name as a parameter (so the test could work with different files every run)


## Task 7: Create Configuration Files

### 7.1 allure.properties

**Location:** `src/test/resources/allure.properties`

```properties
allure.results.directory=target/allure-results
```

### 7.2 testng.xml

**Location:** `src/test/resources/week03/testng.xml`
Create few xml runners (for positive, for negative and for all runs)

## Task 8: 
Create a README file with explanation of your framework and commands how to run it
