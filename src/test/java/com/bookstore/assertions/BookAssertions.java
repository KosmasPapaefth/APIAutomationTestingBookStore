package com.bookstore.assertions;

import com.bookstore.models.Book;
import io.restassured.response.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Reusable assertions for books API responses and mapped {@link Book} payloads.
 */
public final class BookAssertions {

    private BookAssertions() {
    }

    /**
     * Verifies that the response body contains at least one book and that the first record is well formed.
     *
     * @param response response expected to contain a JSON array of books
     */
    public static void assertBooksListIsNotEmpty(Response response) {
        ApiAssertions.assertResponsePresent(response);
        List<Book> books = response.jsonPath().getList("$", Book.class);

        assertFalse(books.isEmpty(), "Expected the books list to contain at least one record.");
        assertBookHasCoreFields(books.get(0));
    }

    /**
     * Verifies that the response contains the expected book id and a valid book payload.
     *
     * @param response response expected to contain a single book object
     * @param expectedId expected book identifier
     */
    public static void assertBookId(Response response, int expectedId) {
        ApiAssertions.assertResponsePresent(response);
        Book actualBook = response.as(Book.class);

        assertAll(
                () -> assertEquals(expectedId, actualBook.getId()),
                () -> assertBookHasCoreFields(actualBook)
        );
    }

    /**
     * Verifies that an API response echoes the submitted book payload.
     *
     * @param expected request payload stored in the test context
     * @param actual mapped response payload
     */
    private static void assertBookMatchesRequestPayload(Book expected, Book actual) {
        assertNotNull(expected, "Request payload was not stored in the test context.");
        assertNotNull(actual, "The API response did not contain a book payload.");
        assertBookMatches(expected, actual);
    }

    /**
     * Verifies the title value of a mapped book payload.
     *
     * @param actual mapped response payload
     * @param expectedTitle expected title value
     */
    private static void assertBookTitle(Book actual, String expectedTitle) {
        assertNotNull(actual, "The API response did not contain a book payload.");
        assertEquals(expectedTitle, actual.getTitle());
    }

    /**
     * Verifies the page count value of a mapped book payload.
     *
     * @param actual mapped response payload
     * @param expectedPageCount expected page count value
     */
    private static void assertBookPageCount(Book actual, int expectedPageCount) {
        assertNotNull(actual, "The API response did not contain a book payload.");
        assertEquals(expectedPageCount, actual.getPageCount());
    }

    /**
     * Interprets a scenario-level expectation and routes it to the appropriate book assertion.
     *
     * @param response response under validation
     * @param expected request payload when the expectation requires comparison against the request
     * @param expectation human-readable expectation text from the feature file
     */
    public static void assertScenarioExpectation(Response response, Book expected, String expectation) {
        ApiAssertions.assertResponsePresent(response);
        String normalizedExpectation = ApiAssertions.normalizeExpectation(expectation);
        Book actual = response.as(Book.class);

        switch (normalizedExpectation) {
            case "match the request payload",
                 "match request payload",
                 "match the created payload",
                 "match the updated payload" -> assertBookMatchesRequestPayload(requireExpected(expected, expectation), actual);
            case "preserve the title from the request payload",
                 "preserve title from request payload" -> assertBookTitle(actual, requireExpected(expected, expectation).getTitle());
            case "preserve the page count from the request payload",
                 "preserve page count from request payload" -> assertBookPageCount(actual, requireExpected(expected, expectation).getPageCount());
            case "contain a valid book" -> assertBookHasCoreFields(actual);
            default -> throw new IllegalArgumentException("Unsupported book scenario expectation: " + expectation);
        }
    }

    private static Book requireExpected(Book expected, String expectation) {
        if (expected == null) {
            throw new IllegalStateException(
                    "A request payload is required to assert book expectation: " + expectation
            );
        }
        return expected;
    }

    private static void assertBookMatches(Book expected, Book actual) {
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getTitle(), actual.getTitle()),
                () -> assertEquals(expected.getDescription(), actual.getDescription()),
                () -> assertEquals(expected.getPageCount(), actual.getPageCount()),
                () -> assertEquals(expected.getExcerpt(), actual.getExcerpt()),
                () -> assertEquals(expected.getPublishDate(), actual.getPublishDate())
        );
    }

    private static void assertBookHasCoreFields(Book book) {
        assertNotNull(book, "The API response did not contain a book payload.");
        assertAll(
                () -> assertNotNull(book.getId()),
                () -> assertNotNull(book.getTitle()),
                () -> assertNotNull(book.getDescription()),
                () -> assertNotNull(book.getPageCount()),
                () -> assertNotNull(book.getExcerpt()),
                () -> assertNotNull(book.getPublishDate())
        );
    }
}
