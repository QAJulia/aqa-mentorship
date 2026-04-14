package schema;

import base.BaseApiTest;
import com.week10.api.spec.BookSchemaValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * SchemaValidationTest – JSON Schema validation tests for BookStore endpoints.
 *
 * JSON SCHEMA VALIDATION OVERVIEW:
 *
 *   What is JSON Schema?
 *     A standard (json-schema.org, Draft 07) for describing the structure of JSON.
 *     It specifies: required fields, field types, string formats, array constraints,
 *     min/max values, and whether additional properties are allowed.
 *
 *   Why validate against a schema?
 *     - Catches API contract breaks: a field renamed, a type changed, a field removed
 *     - Works independently of business logic (complements value assertions)
 *     - Documents the expected API contract in a machine-readable way
 *     - Fails fast with a clear message: "$.books[0].pages: integer found, string expected"
 *
 *   REST Assured integration:
 *     Dependency: io.rest-assured:json-schema-validator
 *     Usage:
 *       response.then().body(matchesJsonSchemaInClasspath("schemas/books-list-schema.json"))
 *
 *   In this project:
 *     BookSchemaValidator centralises the file paths and wraps calls as @Step methods.
 *
 * SCHEMA FILES:
 *   src/main/resources/schemas/books-list-schema.json   → /Books endpoint
 *   src/main/resources/schemas/book-single-schema.json  → /Book?ISBN= endpoint
 */
@Epic("Week 10 – Advanced REST Assured")
@Feature("JSON Schema Validation")
@Owner("QA Mentorship")
public class SchemaValidationTest extends BaseApiTest {

    private static final String KNOWN_ISBN = "9781449325862";

    @Test(description = "GET /Books response conforms to books-list JSON schema",
          groups = {"smoke", "schema"})
    @Story("Schema validation – books list")
    @Severity(SeverityLevel.BLOCKER)
    @Description(
        "Validates the full /Books response against books-list-schema.json.\n" +
        "Schema enforces: required fields (isbn, title, author, pages, website), " +
        "correct types (pages=integer, website=uri), no additional properties."
    )
    public void booksListConformsToSchema() {
        Response response = booksClient.getBooksRawResponse();

        // Schema validation via BookSchemaValidator (@Step method → visible in Allure)
        BookSchemaValidator.assertBooksListSchema(response);
    }

    @Test(description = "GET /Book?ISBN= response conforms to book-single JSON schema",
          groups = {"smoke", "schema"})
    @Story("Schema validation – single book")
    @Severity(SeverityLevel.BLOCKER)
    @Description(
        "Validates a single book response against book-single-schema.json.\n" +
        "Checks isbn, title, author, pages (integer >= 1), website (uri format)."
    )
    public void singleBookConformsToSchema() {
        Response response = booksClient.getBookByIsbnRawResponse(KNOWN_ISBN);

        BookSchemaValidator.assertBookSingleSchema(response);
    }

    @Test(description = "All individual book objects in the list conform to the book schema",
          groups = {"regression", "schema"})
    @Story("Schema validation – each item in list")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "Validates the overall list schema and additionally confirms the list is non-empty. " +
        "The JSON schema itself validates every array item via the 'items' constraint."
    )
    public void everyBookInListConformsToSchema() {
        Response response = booksClient.getBooksRawResponse();

        // The schema's 'items.$ref' definition validates every array element
        BookSchemaValidator.assertBooksListSchema(response);

        // Additionally confirm the list is non-empty (minItems: 1 is also in schema,
        // but this explicit check gives a clearer failure message)
        int bookCount = response.jsonPath().getList("books").size();
        org.testng.Assert.assertTrue(bookCount >= 1,
                "Schema passed but books list was unexpectedly empty");
    }
}
