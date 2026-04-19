package com.bookstore.utils;

import com.bookstore.models.Book;
import com.bookstore.context.TestContext;
import io.cucumber.datatable.DataTable;

import java.util.Map;

/**
 * Maps Cucumber DataTables to {@link Book} payloads used by create and update scenarios.
 *
 * <p>Field values can include dynamic placeholders such as {@code AUTO_ID}, {@code NOW},
 * {@code EMPTY}, {@code NULL}, and ids created earlier in the scenario.</p>
 */
public final class BookDataTableMapper {

    private BookDataTableMapper() {
    }

    /**
     * Converts a two-column DataTable into a {@link Book} and resolves scenario-aware placeholders.
     *
     * @param dataTable DataTable containing book fields and values
     * @param testContext scenario context used to resolve created ids when needed
     * @return mapped book payload
     */
    public static Book fromDataTable(DataTable dataTable, TestContext testContext) {
        return fromMap(dataTable.asMap(String.class, String.class), testContext);
    }

    /**
     * Converts a raw map of book fields into a {@link Book} and resolves supported placeholders.
     *
     * @param bookData key-value pairs representing a book payload
     * @param testContext scenario context used to resolve created ids when needed
     * @return mapped book payload
     */
    public static Book fromMap(Map<String, String> bookData, TestContext testContext) {
        Book book = new Book();

        for (Map.Entry<String, String> entry : bookData.entrySet()) {
            applyField(book, entry.getKey(), entry.getValue(), testContext);
        }

        return book;
    }

    private static void applyField(Book book, String fieldName, String rawValue, TestContext testContext) {
        switch (fieldName) {
            case "id" -> book.setId(parseIntegerField(fieldName, rawValue, testContext));
            case "title" -> book.setTitle(parseStringValue(rawValue, testContext));
            case "description" -> book.setDescription(parseStringValue(rawValue, testContext));
            case "pageCount" -> book.setPageCount(parseIntegerField(fieldName, rawValue, testContext));
            case "excerpt" -> book.setExcerpt(parseStringValue(rawValue, testContext));
            case "publishDate" -> book.setPublishDate(parseDateValue(rawValue, testContext));
            default -> throw new IllegalArgumentException("Unsupported book field in DataTable: " + fieldName);
        }
    }

    private static Integer parseIntegerField(String fieldName, String rawValue, TestContext testContext) {
        String resolvedValue = PlaceholderResolver.resolveBookValue(rawValue, testContext);
        if (resolvedValue == null) {
            return null;
        }

        try {
            return Integer.valueOf(resolvedValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer value for field '" + fieldName + "': " + rawValue, exception);
        }
    }

    private static String parseDateValue(String rawValue, TestContext testContext) {
        return PlaceholderResolver.resolveBookValue(rawValue, testContext);
    }

    private static String parseStringValue(String rawValue, TestContext testContext) {
        return PlaceholderResolver.resolveBookValue(rawValue, testContext);
    }
}
