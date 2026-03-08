# Week 4: API Testing with REST Assured

## What You'll Learn

By the end of this week, you will:
1. Understand HTTP basics (methods, status codes, headers, body)
2. Write REST API tests using Rest Assured
3. Model request/response with POJOs
4. Use Lombok for cleaner models
5. Serialize/deserialize JSON with Jackson
6. Execute SOAP requests
7. Integrate API tests with Allure reporting

We will use https://petstore.swagger.io/ for this week.

---

## HTTP Basics Refresher

### HTTP Methods

| Method | Purpose | Has Body | Example |
|--------|---------|----------|---------|
| **GET** | Retrieve resource | No | Get list of pets |
| **POST** | Create resource | Yes | Create new pet |
| **PUT** | Update/Replace | Yes | Update pet info |
| **PATCH** | Partial update | Yes | Update pet name only |
| **DELETE** | Remove resource | No | Delete pet by ID |

### Common Status Codes

| Code | Meaning | When You See It |
|------|---------|-----------------|
| **200** | OK | Successful GET/PUT/PATCH |
| **201** | Created | Successful POST |
| **204** | No Content | Successful DELETE |
| **400** | Bad Request | Invalid data sent |
| **404** | Not Found | Resource doesn't exist |
| **500** | Server Error | Backend problem |

### Request Components

```
GET /api/pet/123 HTTP/1.1
Host: petstore.swagger.io
Content-Type: application/json
Authorization: Bearer token123

{
  "key": "value"
}
```

1. **URL** - endpoint address
2. **Headers** - metadata (content type, auth)
3. **Body** - data payload (POST/PUT)
4. **Query params** - `?status=available&limit=10`
5. **Path params** - `/pet/{petId}`

---

## Rest Assured Setup

### Add Dependencies to pom.xml

```xml
<!-- Rest Assured -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
</dependency>

<!-- JSON serialization -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.16.1</version>
</dependency>

<!-- Allure Rest Assured integration -->
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-rest-assured</artifactId>
    <version>2.25.0</version>
</dependency>
```

---

## Rest Assured Syntax

### Given-When-Then Pattern

```java
given()
    .baseUri("https://petstore.swagger.io/v2")
    .header("Content-Type", "application/json")
    .pathParam("petId", 123)
.when()
    .get("/pet/{petId}")
.then()
    .statusCode(200)
    .body("name", equalTo("Fluffy"));
```

**Structure:**
- **given()** - setup (base URL, headers, params, body)
- **when()** - action (HTTP method + endpoint)
- **then()** - assertions (status, response body)

---

## POJOs for Request/Response

### What is POJO?

**Plain Old Java Object** - simple class with fields, getters, setters.  
Used to model JSON structure.

### Example: Pet API

**JSON:**
```json
{
  "id": 23,
  "category": {
    "id": 0,
    "name": "string"
  },
  "name": "doggie",
  "photoUrls": [
    "string"
  ],
  "tags": [
    {
      "id": 0,
      "name": "string"
    }
  ],
  "status": "available"
}
```

**POJO:**
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pet {
    private Long id;
    private Category category;
    private String name;

    @JsonProperty("photoUrls")  // JSON field is "photoUrls", not "photo_urls"
    private List<String> photoUrls;

    private List<Tag> tags;
    private String status;  // available, pending, sold
}
```

---

## Serialization/Deserialization

TODO: add details about what is it and explanation on simplified examples

### Serialization (POJO → JSON)

```java
Pet pet = new Pet(123L, "Fluffy", "available");

given()
    .contentType(ContentType.JSON)
    .body(pet)  // Rest Assured auto-serializes with Jackson
.when()
    .post("/pet");
```

### Deserialization (JSON → POJO)

```java
Pet pet = given()
    .pathParam("petId", 123)
.when()
    .get("/pet/{petId}")
.then()
    .statusCode(200)
    .extract()
    .as(Pet.class);  // Rest Assured auto-deserializes

System.out.println(pet.getName());  // "Fluffy"
```

---

## Common Rest Assured Patterns

### 1. GET Request - List All

```java
Response response = given()
    .baseUri("https://petstore.swagger.io")
    .queryParam("status", "available")
.when()
    .get("/pet/findByStatus")
.then()
    .statusCode(200)
    .extract().response();

List<Pet> pets = response.jsonPath().getList("", Pet.class);
```

### 2. GET Request - By ID

```java
Pet pet = given()
    .baseUri("https://petstore.swagger.io")
    .pathParam("petId", 123)
.when()
    .get("/pet/{petId}")
.then()
    .statusCode(200)
    .body("name", equalTo("Fluffy"))
    .extract().as(Pet.class);
```

### 3. POST Request - Create

```java
Pet newPet = Pet.builder()
    .id(999L)
    .name("Max")
    .status("available")
    .build();

Pet created = given()
    .baseUri("https://petstore.swagger.io")
    .contentType(ContentType.JSON)
    .body(newPet)
.when()
    .post("/pet")
.then()
    .statusCode(200)
    .extract().as(Pet.class);

Assert.assertEquals(created.getName(), "Max");
```

### 4. PUT Request - Update

```java
Pet updatedPet = Pet.builder()
    .id(123L)
    .name("Fluffy Updated")
    .status("sold")
    .build();

given()
    .baseUri("https://petstore.swagger.io")
    .contentType(ContentType.JSON)
    .body(updatedPet)
.when()
    .put("/pet")
.then()
    .statusCode(200)
    .body("name", equalTo("Fluffy Updated"));
```

### 5. DELETE Request

```java
given()
    .baseUri("https://petstore.swagger.io")
    .pathParam("petId", 123)
.when()
    .delete("/pet/{petId}")
.then()
    .statusCode(200);

// Verify deleted
given()
    .pathParam("petId", 123)
.when()
    .get("/pet/{petId}")
.then()
    .statusCode(404);  // Not found
```

### 6. Negative Test - 404

```java
given()
    .baseUri("https://petstore.swagger.io")
    .pathParam("petId", 999999)
.when()
    .get("/pet/{petId}")
.then()
    .statusCode(404)
    .body("message", equalTo("Pet not found"));
```

---

## Response Body Validation

### Using Hamcrest Matchers

```java
import static org.hamcrest.Matchers.*;

given()
    .pathParam("petId", 123)
.when()
    .get("/pet/{petId}")
.then()
    .body("id", equalTo(123))
    .body("name", notNullValue())
    .body("status", anyOf(equalTo("available"), equalTo("sold")))
    .body("tags.size()", greaterThan(0));
```

### JsonPath Extraction

```java
Response response = get("/pet/123");

String name = response.jsonPath().getString("name");
int id = response.jsonPath().getInt("id");
List<String> tags = response.jsonPath().getList("tags.name");
```

---

## SOAP Testing with Rest Assured

SOAP uses XML instead of JSON and requires XML body.

### Example: Number Conversion Service

```java
@Test
public void testSoapRequest() {
    String soapBody = """
        <?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <NumberToWords xmlns="http://www.dataaccess.com/webservicesserver/">
              <ubiNum>123</ubiNum>
            </NumberToWords>
          </soap:Body>
        </soap:Envelope>
        """;

    Response response = given()
        .baseUri("https://www.dataaccess.com/webservicesserver/")
        .contentType("text/xml; charset=utf-8")
        .body(soapBody)
    .when()
        .post("NumberConversion.wso")
    .then()
        .statusCode(200)
        .extract().response();

    String result = response.xmlPath()
        .getString("Envelope.Body.NumberToWordsResponse.NumberToWordsResult");
    
    System.out.println("Result: " + result);  // "one hundred and twenty three"
}
```

---

## Allure Integration

### Add Allure Filter to Requests

```java
import io.qameta.allure.restassured.AllureRestAssured;

given()
    .filter(new AllureRestAssured())  // Auto-attach request/response
    .baseUri("https://petstore.swagger.io")
    .pathParam("petId", 123)
.when()
    .get("/pet/{petId}")
.then()
    .statusCode(200);
```

This automatically attaches:
- Request URL, method, headers, body
- Response status, headers, body

---

## Base Test Class Pattern

Create base class to avoid repeating configuration:

```java
public class BaseApiTest {
    protected RequestSpecification baseRequest;

    @BeforeClass
    public void setup() {
        baseRequest = given()
            .filter(new AllureRestAssured())
            .baseUri("https://petstore.swagger.io")
            .contentType(ContentType.JSON);
    }
}
```

Usage:
```java
public class PetTests extends BaseApiTest {
    @Test
    public void testGetPet() {
        baseRequest
            .pathParam("petId", 123)
        .when()
            .get("/pet/{petId}")
        .then()
            .statusCode(200);
    }
}
```

---

## Key Takeaways

| Concept | What | Why |
|---------|------|-----|
| **REST** | Architecture using HTTP methods | Standard for web APIs |
| **POJO** | Java class modeling JSON | Type-safe request/response |
| **Rest Assured** | Java library for API testing | Clean DSL, integrates with TestNG |
| **Lombok** | Annotation-based code generation | Reduces boilerplate |
| **Jackson** | JSON ↔ Java serialization | Automatic with Rest Assured |
| **SOAP** | XML-based protocol | Legacy systems still use it |
| **Allure filter** | Auto-attach request/response | Better debugging in reports |

---

## Resources

- [Rest Assured Documentation](https://rest-assured.io/)
- [Petstore API Swagger](https://petstore.swagger.io/)
- [HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
- [Jackson Documentation](https://github.com/FasterXML/jackson-docs)
- [Hamcrest Matchers](http://hamcrest.org/JavaHamcrest/javadoc/2.2/)