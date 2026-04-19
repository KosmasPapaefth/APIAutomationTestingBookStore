package com.bookstore.assertions;

import com.bookstore.models.Author;
import io.restassured.response.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Reusable assertions for authors API responses and mapped {@link Author} payloads.
 */
public final class AuthorAssertions {

    private AuthorAssertions() {
    }

    /**
     * Verifies that the response body contains at least one author and that the first record is well formed.
     *
     * @param response response expected to contain a JSON array of authors
     */
    public static void assertAuthorsListIsNotEmpty(Response response) {
        ApiAssertions.assertResponsePresent(response);
        List<Author> authors = response.jsonPath().getList("$", Author.class);

        assertFalse(authors.isEmpty(), "Expected at least one author in the response.");
        assertAuthorHasCoreFields(authors.get(0));
    }

    /**
     * Verifies that the response contains the expected author id and a valid author payload.
     *
     * @param response response expected to contain a single author object
     * @param expectedId expected author identifier
     */
    public static void assertAuthorId(Response response, int expectedId) {
        ApiAssertions.assertResponsePresent(response);
        Author actualAuthor = response.as(Author.class);

        assertAll(
                () -> assertEquals(expectedId, actualAuthor.getId()),
                () -> assertAuthorHasCoreFields(actualAuthor)
        );
    }

    /**
     * Verifies that an API response echoes the submitted author payload.
     *
     * @param expected request payload stored in the test context
     * @param actual mapped response payload
     */
    private static void assertAuthorMatchesRequestPayload(Author expected, Author actual) {
        assertNotNull(expected, "Request payload was not stored in the test context.");
        assertNotNull(actual, "The API response did not contain an author payload.");
        assertAuthorMatches(expected, actual);
    }

    /**
     * Verifies that the response author references the expected book id.
     *
     * @param response response expected to contain a single author object
     * @param expectedBookId expected linked book identifier
     */
    public static void assertAuthorLinkedToBook(Response response, int expectedBookId) {
        ApiAssertions.assertResponsePresent(response);
        Author actualAuthor = response.as(Author.class);

        assertAll(
                () -> assertAuthorHasCoreFields(actualAuthor),
                () -> assertEquals(expectedBookId, actualAuthor.getIdBook())
        );
    }

    /**
     * Verifies the first name value of a mapped author payload.
     *
     * @param actual mapped response payload
     * @param expectedFirstName expected first name value
     */
    private static void assertAuthorFirstName(Author actual, String expectedFirstName) {
        assertNotNull(actual, "The API response did not contain an author payload.");
        assertEquals(expectedFirstName, actual.getFirstName());
    }

    /**
     * Verifies the last name value of a mapped author payload.
     *
     * @param actual mapped response payload
     * @param expectedLastName expected last name value
     */
    private static void assertAuthorLastName(Author actual, String expectedLastName) {
        assertNotNull(actual, "The API response did not contain an author payload.");
        assertEquals(expectedLastName, actual.getLastName());
    }

    /**
     * Verifies the linked book id value of a mapped author payload.
     *
     * @param expectedIdBook expected linked book identifier
     */
    public static void assertAuthorBookId(Response response, int expectedIdBook) {
        ApiAssertions.assertResponsePresent(response);
        assertAuthorBookId(response.as(Author.class), expectedIdBook);
    }

    private static void assertAuthorBookId(Author actual, Integer expectedIdBook) {
        assertNotNull(actual, "The API response did not contain an author payload.");
        assertEquals(expectedIdBook, actual.getIdBook());
    }

    /**
     * Interprets a scenario-level expectation and routes it to the appropriate author assertion.
     *
     * @param response response under validation
     * @param expected request payload when the expectation requires comparison against the request
     * @param expectation human-readable expectation text from the feature file
     */
    public static void assertScenarioExpectation(Response response, Author expected, String expectation) {
        ApiAssertions.assertResponsePresent(response);
        String normalizedExpectation = ApiAssertions.normalizeExpectation(expectation);
        Author actual = response.as(Author.class);

        switch (normalizedExpectation) {
            case "match the request payload",
                 "match request payload",
                 "match the created payload",
                 "match the updated payload" -> assertAuthorMatchesRequestPayload(requireExpected(expected, expectation), actual);
            case "preserve the first name from the request payload",
                 "preserve first name from request payload" -> assertAuthorFirstName(actual, requireExpected(expected, expectation).getFirstName());
            case "preserve the last name from the request payload",
                 "preserve last name from request payload" -> assertAuthorLastName(actual, requireExpected(expected, expectation).getLastName());
            case "preserve the linked book id from the request payload",
                 "preserve linked book id from request payload" -> assertAuthorBookId(actual, requireExpected(expected, expectation).getIdBook());
            case "contain a valid author" -> assertAuthorHasCoreFields(actual);
            default -> throw new IllegalArgumentException("Unsupported author scenario expectation: " + expectation);
        }
    }

    private static Author requireExpected(Author expected, String expectation) {
        if (expected == null) {
            throw new IllegalStateException(
                    "A request payload is required to assert author expectation: " + expectation
            );
        }
        return expected;
    }

    private static void assertAuthorMatches(Author expected, Author actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getIdBook(), actual.getIdBook()),
                () -> assertEquals(expected.getFirstName(), actual.getFirstName()),
                () -> assertEquals(expected.getLastName(), actual.getLastName())
        );
    }

    private static void assertAuthorHasCoreFields(Author author) {
        assertNotNull(author, "The API response did not contain an author payload.");
        assertAll(
                () -> assertNotNull(author.getId()),
                () -> assertNotNull(author.getIdBook()),
                () -> assertNotNull(author.getFirstName()),
                () -> assertNotNull(author.getLastName())
        );
    }
}
