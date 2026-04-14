package spec;

import base.BaseApiTest;
import com.week10.api.client.BooksClient;
import com.week10.api.spec.RequestSpecFactory;
import com.week10.api.spec.ResponseSpecFactory;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * RequestSpecTest – demonstrates Request and Response Specification reuse.
 *
 * THIS CLASS IS EDUCATIONAL:
 *   In production all clients use BooksClient (which applies specs internally).
 *   This class shows the RAW spec API so mentees understand what's happening inside.
 *
 * TWO PROBLEMS SOLVED BY SPECS:
 *
 *   Problem 1 – Duplicated setup:
 *     Every test that calls a given endpoint needs the same base URI, headers, and content type.
 *     Without specs:
 *       given().baseUri("https://demoqa.com").header("Accept","application/json").get("/Books")
 *       given().baseUri("https://demoqa.com").header("Accept","application/json").get("/Books")  ← duplicate
 *
 *     With specs:
 *       RequestSpecification spec = RequestSpecFactory.defaultSpec();
 *       given(spec).get("/Books")
 *       given(spec).get("/Books")   ← same one-liner
 *
 *   Problem 2 – Duplicated assertions:
 *     Every successful test checks statusCode(200) and contentType(JSON).
 *     Without response spec:
 *       .then().statusCode(200).contentType("application/json").body(...)
 *     With response spec:
 *       .then().spec(ResponseSpecFactory.ok200WithJson()).body(...)
 *
 * FILTER DEMONSTRATION:
 *   This class also shows how filters appear in the chain:
 *     TimingFilter   → logs response time + attaches to Allure
 *     CorrelationIdFilter → adds X-Correlation-ID header
 *     AllureRestAssured   → attaches full request/response body
 */
@Epic("Week 10 – Advanced REST Assured")
@Feature("Request & Response Specifications")
@Owner("QA Mentorship")
public class RequestSpecTest extends BaseApiTest {

    // =========================================================================
    // RequestSpecification demonstrations
    // =========================================================================

    @Test(description = "defaultSpec applies base URI, content type, and filters",
          groups = {"smoke", "spec"})
    @Story("Default RequestSpec")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Calls /Books using the defaultSpec() directly (without BooksClient wrapper). " +
        "Demonstrates that the spec correctly sets base URI, Accept/Content-Type headers."
    )
    public void defaultSpecSetsHeadersCorrectly() {
        RequestSpecification spec = RequestSpecFactory.defaultSpec();

        Response response = RestAssured.given(spec)
                .get("/BookStore/v1/Books");

        assertEquals(response.statusCode(), 200,
                "Expected 200 OK with defaultSpec");
        assertTrue(response.contentType().contains("application/json"),
                "Expected JSON content type");
    }

    @Test(description = "ResponseSpec ok200WithJson validates status and content type",
          groups = {"smoke", "spec"})
    @Story("Response Specification reuse")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Shows ResponseSpecFactory.ok200WithJson() applied to a raw given/when/then chain. " +
        "The spec replaces individual statusCode() + contentType() calls."
    )
    public void responseSpecValidatesStatusAndContentType() {
        RequestSpecification spec = RequestSpecFactory.defaultSpec();

        // ResponseSpec applied inline – no need to repeat statusCode(200) in every test
        RestAssured.given(spec)
                .get("/BookStore/v1/Books")
                .then()
                .spec(ResponseSpecFactory.ok200WithJson())
                .body("books", notNullValue())
                .body("books.size()", greaterThan(0));
    }

    @Test(description = "minimalSpec skips Allure and timing filters",
          groups = {"regression", "spec"})
    @Story("Minimal spec – no filters")
    @Severity(SeverityLevel.MINOR)
    @Description(
        "BooksClient.withMinimalSpec() creates a client without AllureRestAssured " +
        "or TimingFilter. The response still works but nothing is attached to the report. " +
        "Useful for performance-sensitive or high-volume contract checks."
    )
    public void minimalSpecStillReturnsValidResponse() {
        BooksClient minimalClient = BooksClient.withMinimalSpec();

        // Response is still a valid 200 JSON response
        Response response = minimalClient.getBooksRawResponse();

        assertEquals(response.statusCode(), 200,
                "Minimal spec should still return 200");
        assertFalse(response.jsonPath().getList("books").isEmpty(),
                "Books list should not be empty even with minimal spec");
    }

    // =========================================================================
    // Response time via ResponseSpec
    // =========================================================================

    @Test(description = "ok200WithJsonFast spec enforces < 3s response time",
          groups = {"regression", "spec", "performance"})
    @Story("Response time spec")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "ResponseSpecFactory.ok200WithJsonFast() includes an expectResponseTime() constraint. " +
        "The test fails if the endpoint responds in more than 3000 ms."
    )
    public void booksEndpointRespondsWithin3Seconds() {
        RequestSpecification spec = RequestSpecFactory.defaultSpec();

        RestAssured.given(spec)
                .get("/BookStore/v1/Books")
                .then()
                .spec(ResponseSpecFactory.ok200WithJsonFast()); // includes time constraint
    }

    // =========================================================================
    // Hamcrest body assertions (complement to schema validation)
    // =========================================================================

    @Test(description = "Response body has expected top-level structure",
          groups = {"regression", "spec"})
    @Story("Body structure – Hamcrest assertions")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Uses Hamcrest matchers directly in then().body() to assert structure. " +
        "Complements schema validation with specific value checks."
    )
    public void responsBodyHasExpectedStructure() {
        RequestSpecification spec = RequestSpecFactory.defaultSpec();

        RestAssured.given(spec)
                .get("/BookStore/v1/Books")
                .then()
                .spec(ResponseSpecFactory.ok200WithJson())
                .body("books",            notNullValue())
                .body("books.size()",     greaterThanOrEqualTo(8))
                .body("books[0].isbn",    notNullValue())
                .body("books[0].title",   notNullValue())
                .body("books[0].author",  notNullValue())
                .body("books[0].pages",   greaterThan(0));
    }
}
