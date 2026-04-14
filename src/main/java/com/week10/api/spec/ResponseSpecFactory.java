package com.week10.api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.*;

/**
 * ResponseSpecFactory – factory for shared REST Assured ResponseSpecification objects.
 *
 * A ResponseSpecification defines WHAT we expect from a response.
 * By centralising these expectations, we:
 *   - Remove duplicated status code / content type assertions from every test
 *   - Make the contract explicit in one place
 *   - Allow tests to focus on the business-level assertions, not the HTTP contract
 *
 * USAGE in tests (via BooksClient):
 *
 *   // Instead of:
 *   .then().statusCode(200).contentType(ContentType.JSON).body("books", notNullValue())
 *
 *   // Write:
 *   .then().spec(ResponseSpecFactory.ok200WithJson())
 *
 * USAGE with raw given/when/then (for spec demonstration tests):
 *
 *   Response response = given(spec).get("/path");
 *   response.then().spec(ResponseSpecFactory.ok200WithJson());
 */
public final class ResponseSpecFactory {

    private ResponseSpecFactory() {}

    /**
     * Expects HTTP 200 with JSON body.
     * Used for all successful GET endpoints.
     */
    public static ResponseSpecification ok200WithJson() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    /**
     * Expects HTTP 200, JSON body, and a response time under 3 seconds.
     * Use for performance-sensitive endpoints.
     */
    public static ResponseSpecification ok200WithJsonFast() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectResponseTime(lessThan(3000L))
                .build();
    }

    /**
     * Expects HTTP 400 Bad Request with JSON body.
     * Use for negative tests with invalid input.
     */
    public static ResponseSpecification badRequest400() {
        return new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectContentType(ContentType.JSON)
                .build();
    }

    /**
     * Expects HTTP 404 Not Found.
     * Use for tests with non-existent resource identifiers.
     */
    public static ResponseSpecification notFound404() {
        return new ResponseSpecBuilder()
                .expectStatusCode(404)
                .build();
    }

    /**
     * Expects HTTP 401 Unauthorized.
     * Use for tests with missing or invalid credentials.
     */
    public static ResponseSpecification unauthorized401() {
        return new ResponseSpecBuilder()
                .expectStatusCode(401)
                .build();
    }
}
