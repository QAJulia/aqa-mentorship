# Week 3 Homework: Testing Product Discount Calculator with Allure

## Goal
Test a product discount calculation system using TestNG, Allure reporting, and custom listeners. 
Practice positive/negative testing with detailed Allure steps and attachments.
Here you can find code examples for every step, but try to not use them before you try to write it yourself.

---

## What You're Building

**Scenario:** E-commerce system applies discounts to products. You need to test the discount calculation logic.

**Domain Models:**
1. `Product` - represents a product with price
2. `Discount` - discount rules (percentage or fixed amount)
3. `DiscountResult` - calculation result with savings

**Business Logic:** `DiscountCalculator.applyDiscount()` - calculates final price after discount

---

## Project Structure

```
src/
├── main/java/com/week03/
│   ├── models/
│   │   ├── Product.java 
│   │   ├── Discount.java
│   │   └── DiscountResult.java
│   ├── services/
│   │   └── DiscountCalculator.java
│   ├── steps/
│   │   └── DiscountTestSteps.java
│   └── listeners/
│       └── TestExecutionListener.java
└── test/
    ├── java/week03/
    │   └── DiscountCalculatorTests.java
    └── resources/week03/
        ├── testng.xml
        ├── allure.properties
        └── testData/
            └── discounts.csv
```

---

## Task 1: Create Domain Models

### 1.1 Product Model

**Location:** `src/main/java/com/week03/models/Product.java`
Create a Product model with fields: int id, String name, double price, String category.

```java
package com.week03.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int id;
    private String name;
    private double price;
    private String category;
}
```

### 1.2 Discount Model

**Location:** `src/main/java/com/week03/models/Discount.java`
Create a Discount model with fields: double value, String description, enum DiscountType type

```java
package com.week03.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
    private DiscountType type;
    private double value;
    private String description;
    
    public enum DiscountType {
        PERCENTAGE,  // e.g., 10% off
        FIXED        // e.g., $15 off
    }
}
```

### 1.3 DiscountResult Model

**Location:** `src/main/java/com/week03/models/DiscountResult.java`
Create a model for DiscountResult with fields: double originalPrice, double discountedPrice, double savedAmount, 
boolean isValid, String message

```java
package com.week03.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResult {
    private double originalPrice;
    private double discountedPrice;
    private double savedAmount;
    private boolean isValid;
    private String message;
}
```

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

**Example implementation:**

```java
package com.week03.services;

import com.week03.models.Discount;
import com.week03.models.DiscountResult;
import com.week03.models.Product;

public class DiscountCalculator {
    
    public DiscountResult applyDiscount(Product product, Discount discount) {
        double originalPrice = product.getPrice();
        
        // Validate discount value
        if (discount.getValue() <= 0) {
            return new DiscountResult(
                originalPrice, 
                originalPrice, 
                0, 
                false, 
                "Discount value must be positive"
            );
        }
        
        double discountedPrice;
        double savedAmount;
        
        switch (discount.getType()) {
            case PERCENTAGE:
                if (discount.getValue() > 100) {
                    return new DiscountResult(
                        originalPrice, 
                        originalPrice, 
                        0, 
                        false, 
                        "Percentage discount cannot exceed 100%"
                    );
                }
                savedAmount = originalPrice * (discount.getValue() / 100);
                discountedPrice = originalPrice - savedAmount;
                break;
                
            case FIXED:
                if (discount.getValue() > originalPrice) {
                    return new DiscountResult(
                        originalPrice, 
                        originalPrice, 
                        0, 
                        false, 
                        "Fixed discount cannot exceed product price"
                    );
                }
                savedAmount = discount.getValue();
                discountedPrice = originalPrice - savedAmount;
                break;
                
            default:
                return new DiscountResult(
                    originalPrice, 
                    originalPrice, 
                    0, 
                    false, 
                    "Unknown discount type"
                );
        }
        
        return new DiscountResult(
            originalPrice, 
            discountedPrice, 
            savedAmount, 
            true, 
            "Discount applied successfully"
        );
    }
}
```

---

## Task 3: Create Allure Step Methods

**Location:** `src/main/java/com/week03/steps/DiscountTestSteps.java`

**Requirements:**
Create methods with `@Step` annotation for Creating product, creating discount, apply discount, verify discounted price,
verify is result is valid. For every Step add attachments with Objects of used Products, Discounts etc. For every 
validation in Allure must be visible expected and actual results.

```java
package com.week03.steps;

import com.week03.models.Discount;
import com.week03.models.DiscountResult;
import com.week03.models.Product;
import com.week03.services.DiscountCalculator;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class DiscountTestSteps {
    
    private DiscountCalculator calculator = new DiscountCalculator();
    
    @Step("Create product: {name} with price ${price}")
    public Product createProduct(int id, String name, double price, String category) {
        Product product = new Product(id, name, price, category);
        Allure.addAttachment("Product Details", 
            String.format("ID: %d\nName: %s\nPrice: $%.2f\nCategory: %s", 
                id, name, price, category));
        return product;
    }
    
    @Step("Create {type} discount: {value}")
    public Discount createDiscount(Discount.DiscountType type, double value, String description) {
        Discount discount = new Discount(type, value, description);
        Allure.addAttachment("Discount Details", 
            String.format("Type: %s\nValue: %.2f\nDescription: %s", 
                type, value, description));
        return discount;
    }
    
    @Step("Apply discount to product")
    public DiscountResult applyDiscount(Product product, Discount discount) {
        DiscountResult result = calculator.applyDiscount(product, discount);
        
        // Attach calculation details
        String calculation = String.format(
            "Original Price: $%.2f\n" +
            "Discount Applied: %s %.2f\n" +
            "Final Price: $%.2f\n" +
            "Saved: $%.2f\n" +
            "Valid: %s\n" +
            "Message: %s",
            result.getOriginalPrice(),
            discount.getType(),
            discount.getValue(),
            result.getDiscountedPrice(),
            result.getSavedAmount(),
            result.isValid(),
            result.getMessage()
        );
        
        Allure.addAttachment("Calculation Result", "text/plain", calculation);
        return result;
    }
    
    @Step("Verify discounted price is ${expectedPrice}")
    public void verifyDiscountedPrice(DiscountResult result, double expectedPrice) {
        Allure.addAttachment("Expected Price", String.format("$%.2f", expectedPrice));
        Allure.addAttachment("Actual Price", String.format("$%.2f", result.getDiscountedPrice()));
        
        if (Math.abs(result.getDiscountedPrice() - expectedPrice) > 0.01) {
            throw new AssertionError(
                String.format("Price mismatch! Expected: $%.2f, Actual: $%.2f", 
                    expectedPrice, result.getDiscountedPrice())
            );
        }
    }
    
    @Step("Verify calculation is valid")
    public void verifyValid(DiscountResult result, boolean expectedValid) {
        Allure.addAttachment("Expected Valid", String.valueOf(expectedValid));
        Allure.addAttachment("Actual Valid", String.valueOf(result.isValid()));
        
        if (result.isValid() != expectedValid) {
            throw new AssertionError(
                String.format("Validity mismatch! Expected: %s, Actual: %s", 
                    expectedValid, result.isValid())
            );
        }
    }
}
```

---

## Task 4: Create Test Execution Listener

**Location:** `src/main/java/com/week03/listeners/TestExecutionListener.java`

**Requirements:**
- Implement `ITestListener`
- Track: test start, test success, test failure, test skipped
- Print to console with status symbols: ▶ (start), ✓ (pass), ✗ (fail), ⊘ (skip)
- On failure: attach error to Allure

```java
package com.week03.listeners;

import io.qameta.allure.Allure;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestExecutionListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("▶ STARTING: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✓ PASSED: " + result.getMethod().getMethodName() + 
            " (Duration: " + (result.getEndMillis() - result.getStartMillis()) + "ms)");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("✗ FAILED: " + result.getMethod().getMethodName());
        
        // Attach failure details to Allure
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Allure.addAttachment("Failure Reason", throwable.getMessage());
            
            // Attach stack trace
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            Allure.addAttachment("Stack Trace", "text/plain", sw.toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⊘ SKIPPED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("TEST EXECUTION SUMMARY");
        System.out.println("=".repeat(50));
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size());
        System.out.println("=".repeat(50) + "\n");
    }
}
```

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

```java
package week03;

import com.week03.models.Discount;
import com.week03.models.DiscountResult;
import com.week03.models.Product;
import com.week03.steps.DiscountTestSteps;
import io.qameta.allure.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Epic("E-commerce Platform")
@Feature("Discount Calculation")
public class DiscountCalculatorTests {

    private DiscountTestSteps steps;

    @BeforeClass
    public void setUp() {
        steps = new DiscountTestSteps();
    }

    @DataProvider(name = "validDiscounts")
    public Object[][] validDiscounts() {
        List<Object[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader("src/test/resources/week03/testData/discounts.csv"))) {
            
            String line;
            br.readLine(); // Skip header
            
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(new Object[]{
                    Integer.parseInt(values[0]),           // productId
                    values[1],                             // productName
                    Double.parseDouble(values[2]),         // price
                    values[3],                             // category
                    Discount.DiscountType.valueOf(values[4]), // discountType
                    Double.parseDouble(values[5]),         // discountValue
                    Double.parseDouble(values[6])          // expectedPrice
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.toArray(new Object[0][]);
    }

    @Test(dataProvider = "validDiscounts", groups = {"smoke", "positive"})
    @Story("Apply valid discount")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test discount calculation with valid percentage and fixed discounts")
    public void testValidDiscount(int id, String name, double price, String category,
                                   Discount.DiscountType type, double value, double expectedPrice) {
        // Create product
        Product product = steps.createProduct(id, name, price, category);
        
        // Create discount
        Discount discount = steps.createDiscount(type, value, "Test discount");
        
        // Apply discount
        DiscountResult result = steps.applyDiscount(product, discount);
        
        // Verify result
        steps.verifyValid(result, true);
        steps.verifyDiscountedPrice(result, expectedPrice);
    }

    @Test(groups = {"negative"})
    @Story("Handle excessive discount")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify system handles discount larger than product price")
    public void testDiscountTooLarge() {
        // Create product
        Product product = steps.createProduct(5, "Keyboard", 50.0, "Accessories");
        
        // Create discount larger than price
        Discount discount = steps.createDiscount(Discount.DiscountType.FIXED, 100.0, "Invalid discount");
        
        // Apply discount
        DiscountResult result = steps.applyDiscount(product, discount);
        
        // Verify discount is rejected
        steps.verifyValid(result, false);
    }

    @Test(groups = {"negative"})
    @Story("Handle invalid discount value")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify system rejects negative or zero discount values")
    public void testInvalidDiscountValue() {
        // Create product
        Product product = steps.createProduct(6, "Monitor", 300.0, "Electronics");
        
        // Create invalid discount (negative value)
        Discount discount = steps.createDiscount(Discount.DiscountType.PERCENTAGE, -10.0, "Negative discount");
        
        // Apply discount
        DiscountResult result = steps.applyDiscount(product, discount);
        
        // Verify discount is rejected
        steps.verifyValid(result, false);
    }

    @Test(groups = {"regression"})
    @Story("Apply percentage discount")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test intentionally fails to demonstrate failure reporting in Allure")
    public void testPercentageDiscountWrongExpectation() {
        // Create product
        Product product = steps.createProduct(7, "Tablet", 400.0, "Electronics");
        
        // Create 20% discount
        Discount discount = steps.createDiscount(Discount.DiscountType.PERCENTAGE, 20.0, "20% off");
        
        // Apply discount
        DiscountResult result = steps.applyDiscount(product, discount);
        
        // Verify with WRONG expected price (should be 320, but we expect 350)
        steps.verifyValid(result, true);
        steps.verifyDiscountedPrice(result, 350.0);  // ❌ This will fail!
    }
}
```

---

## Task 7: Create Configuration Files

### 7.1 allure.properties

**Location:** `src/test/resources/allure.properties`

```properties
allure.results.directory=target/allure-results
```

### 7.2 testng.xml

**Location:** `src/test/resources/week03/testng.xml`
Create few xml runners (for positive, for negative and for all runs)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Discount Calculator Test Suite" verbose="2">
    
    <listeners>
        <listener class-name="com.week03.listeners.TestExecutionListener"/>
    </listeners>
    
    <test name="Smoke Tests">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <classes>
            <class name="week03.DiscountCalculatorTests"/>
        </classes>
    </test>
    
</suite>
```
