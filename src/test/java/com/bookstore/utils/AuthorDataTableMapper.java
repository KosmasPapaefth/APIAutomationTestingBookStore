package com.bookstore.utils;

import com.bookstore.models.Author;
import com.bookstore.context.TestContext;
import io.cucumber.datatable.DataTable;

import java.util.Map;

/**
 * Maps Cucumber DataTables to {@link Author} payloads used by create and update scenarios.
 *
 * <p>Supported placeholders include {@code AUTO_ID}, {@code AUTO_BOOK_ID}, {@code EMPTY},
 * {@code NULL}, and ids produced earlier in the scenario.</p>
 */
public final class AuthorDataTableMapper {

    private AuthorDataTableMapper() {
    }

    /**
     * Converts a two-column DataTable into an {@link Author} and resolves scenario-aware placeholders.
     *
     * @param dataTable DataTable containing author fields and values
     * @param testContext scenario context used to resolve created ids when needed
     * @return mapped author payload
     */
    public static Author fromDataTable(DataTable dataTable, TestContext testContext) {
        return fromMap(dataTable.asMap(String.class, String.class), testContext);
    }

    /**
     * Converts a raw map of author fields into an {@link Author} and resolves supported placeholders.
     *
     * @param authorData key-value pairs representing an author payload
     * @param testContext scenario context used to resolve created ids when needed
     * @return mapped author payload
     */
    public static Author fromMap(Map<String, String> authorData, TestContext testContext) {
        Author author = new Author();

        for (Map.Entry<String, String> entry : authorData.entrySet()) {
            applyField(author, entry.getKey(), entry.getValue(), testContext);
        }

        return author;
    }

    private static void applyField(Author author, String fieldName, String rawValue, TestContext testContext) {
        switch (fieldName) {
            case "id" -> author.setId(parseIntegerField(fieldName, rawValue, testContext));
            case "idBook" -> author.setIdBook(parseIntegerField(fieldName, rawValue, testContext));
            case "firstName" -> author.setFirstName(PlaceholderResolver.resolveAuthorValue(rawValue, testContext));
            case "lastName" -> author.setLastName(PlaceholderResolver.resolveAuthorValue(rawValue, testContext));
            default -> throw new IllegalArgumentException("Unsupported author field in DataTable: " + fieldName);
        }
    }

    private static Integer parseIntegerField(String fieldName, String rawValue, TestContext testContext) {
        String resolvedValue = PlaceholderResolver.resolveAuthorValue(rawValue, testContext);
        if (resolvedValue == null) {
            return null;
        }

        try {
            return Integer.valueOf(resolvedValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer value for field '" + fieldName + "': " + rawValue, exception);
        }
    }
}
