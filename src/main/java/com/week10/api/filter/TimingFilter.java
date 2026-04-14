package com.week10.api.filter;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TimingFilter – a custom REST Assured Filter that measures and records response time.
 *
 * WHAT IS A REST ASSURED FILTER?
 *   A Filter is a component that sits in the request/response pipeline.
 *   It can inspect or modify both the request before it is sent and the
 *   response after it is received — similar to Servlet filters or OkHttp interceptors.
 *
 *   Filter pipeline:
 *     Test → Filter1 → Filter2 → (HTTP) → Filter2 → Filter1 → Test
 *
 *   Filters are chained via FilterContext.next(request, response).
 *   If you don't call next(), the HTTP call never happens.
 *
 * WHAT THIS FILTER DOES:
 *   1. Records start time before passing to the next filter
 *   2. After the response returns, calculates elapsed milliseconds
 *   3. Logs the timing to SLF4J
 *   4. Attaches timing as a text note in the Allure report
 *
 * REGISTRATION:
 *   .addFilter(new TimingFilter())  in RequestSpecFactory.defaultSpec()
 *
 * WHY NOT USE restAssured's built-in time()?
 *   The built-in time() measures only the network round-trip.
 *   A custom filter can measure the full filter chain, including serialisation time.
 */
public class TimingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TimingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        long startMs = System.currentTimeMillis();

        // Pass control to the next filter in the chain (or the actual HTTP call)
        Response response = ctx.next(requestSpec, responseSpec);

        long elapsedMs = System.currentTimeMillis() - startMs;

        String message = String.format("%s %s → %d  [%d ms]",
                requestSpec.getMethod(),
                requestSpec.getURI(),
                response.statusCode(),
                elapsedMs);

        log.info(message);

        // Attach timing to the current Allure step
        Allure.addAttachment("Response Time", "text/plain", message);

        return response;
    }
}
