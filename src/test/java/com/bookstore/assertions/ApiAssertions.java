package com.bookstore.assertions;

import io.restassured.response.Response;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Shared assertions for generic API response characteristics.
 */
public final class ApiAssertions {

    private ApiAssertions() {
    }

    /**
     * Verifies that the response status code matches the expected value.
     *
     * @param response response stored in the current scenario context
     * @param expectedStatusCode expected HTTP status code
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        assertNotNull(response, "No API response has been stored in the test context.");
        assertEquals(expectedStatusCode, response.statusCode());
    }

    /**
     * Verifies that a response has been stored before a more specific assertion runs.
     *
     * @param response response stored in the current scenario context
     */
    public static void assertResponsePresent(Response response) {
        assertNotNull(response, "No API response has been stored in the test context.");
    }

    /**
     * Verifies that the response body is blank.
     *
     * @param response response stored in the current scenario context
     * @param message assertion message used when the body is not blank
     */
    public static void assertEmptyBody(Response response, String message) {
        assertResponsePresent(response);
        assertTrue(response.asString().isBlank(), message);
    }

    /**
     * Verifies the validation-style error payload returned by ASP.NET model binding failures.
     *
     * @param response response expected to contain a validation error body
     */
    public static void assertValidationFailure(Response response) {
        assertResponsePresent(response);

        String type = response.jsonPath().getString("type");
        String title = response.jsonPath().getString("title");
        Number status = response.jsonPath().get("status");
        Object errors = response.jsonPath().get("errors");

        assertAll(
                () -> assertEquals(400, response.statusCode(), "Expected HTTP 400 for validation failure."),
                () -> assertEquals(400, status == null ? null : status.intValue(), "Expected error payload status to be 400."),
                () -> assertNotNull(type, "Expected error response to include 'type'."),
                () -> assertFalse(type.isBlank(), "Expected error response 'type' to be non-empty."),
                () -> assertNotNull(title, "Expected error response to include 'title'."),
                () -> assertTrue(
                        normalizeExpectation(title).contains("validation"),
                        "Expected error title to describe a validation failure."
                ),
                () -> assertNotNull(errors, "Expected error response to include 'errors'."),
                () -> assertTrue(errors instanceof Map<?, ?>, "Expected 'errors' to be a JSON object."),
                () -> assertFalse(((Map<?, ?>) errors).isEmpty(), "Expected validation errors to be non-empty.")
        );
    }

    /**
     * Verifies that the error response contains a non-empty trace identifier.
     *
     * @param response response expected to contain a validation error body
     */
    public static void assertResponseContainsTraceId(Response response) {
        assertResponsePresent(response);

        String traceId = response.jsonPath().getString("traceId");
        assertAll(
                () -> assertNotNull(traceId, "Expected error response to include 'traceId'."),
                () -> assertFalse(traceId.isBlank(), "Expected error response 'traceId' to be non-empty.")
        );
    }

    /**
     * Verifies the common validation error contract, including the trace identifier used for diagnostics.
     *
     * @param response response expected to contain a validation error body with trace metadata
     */
    public static void assertValidationFailureWithTraceId(Response response) {
        assertValidationFailure(response);
        assertResponseContainsTraceId(response);
    }

    /**
     * Verifies that the error response contains a validation error entry for the requested field.
     *
     * @param response response expected to contain a validation error body
     * @param key field name expected under the errors object
     */
    public static void assertResponseContainsErrorKey(Response response, String key) {
        assertResponsePresent(response);

        Object errorEntry = response.jsonPath().get("errors." + key);
        assertNotNull(errorEntry, "Expected validation errors to include key: " + key);
    }

    /**
     * Maps a human-readable error expectation to shared error validation logic.
     *
     * @param response response returned by the API
     * @param expectation scenario expectation text from Gherkin
     */
    public static void assertErrorResponseExpectation(Response response, String expectation) {
        String normalizedExpectation = normalizeExpectation(expectation);

        switch (normalizedExpectation) {
            case "validation failure",
                 "validation error",
                 "bad request validation failure" -> assertValidationFailure(response);
            default -> throw new IllegalArgumentException("Unsupported API error expectation: " + expectation);
        }
    }

    /**
     * Maps a human-readable delete expectation to the shared delete validation logic.
     *
     * @param response response returned by the delete request
     * @param expectation scenario expectation text from Gherkin
     */
    public static void assertDeleteExpectation(Response response, String expectation) {
        String normalizedExpectation = normalizeExpectation(expectation);

        switch (normalizedExpectation) {
            case "complete successfully",
                 "still return success",
                 "successful deletion",
                 "non existing delete still succeeds" -> assertEmptyBody(
                    response,
                    "Expected an empty response body for a successful delete."
            );
            default -> throw new IllegalArgumentException("Unsupported delete expectation: " + expectation);
        }
    }

    /**
     * Normalizes scenario expectation text so equivalent phrases can share one assertion mapping.
     *
     * @param expectation raw expectation text from Gherkin
     * @return normalized expectation value used by assertion interpreters
     */
    public static String normalizeExpectation(String expectation) {
        return expectation == null ? "" : expectation.trim().toLowerCase().replace('-', ' ');
    }
}
