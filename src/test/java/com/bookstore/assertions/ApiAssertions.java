package com.bookstore.assertions;

import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
     * Maps a human-readable delete expectation to the shared delete validation logic.
     *
     * @param response response returned by the delete request
     * @param expectation scenario expectation text from Gherkin
     * @param unsupportedMessage message prefix used when the expectation is not recognized
     */
    public static void assertDeleteExpectation(Response response, String expectation, String unsupportedMessage) {
        String normalizedExpectation = normalizeExpectation(expectation);

        switch (normalizedExpectation) {
            case "complete successfully",
                 "still return success",
                 "successful deletion",
                 "non existing delete still succeeds" -> assertEmptyBody(
                    response,
                    "Expected an empty response body for a successful delete."
            );
            default -> throw new IllegalArgumentException(unsupportedMessage + expectation);
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
