package com.bookstore.utils;

import com.bookstore.context.TestContext;
import com.bookstore.models.Author;
import com.bookstore.models.Book;
import io.cucumber.datatable.DataTable;

import java.util.Locale;

/**
 * Converts Cucumber DataTables into Bookstore API payload objects and resolves supported placeholders.
 */
public final class DataTableMapper {

    private DataTableMapper() {
    }

    public static Book toBook(DataTable dataTable, TestContext testContext) {
        Book book = new Book();
        dataTable.asMap(String.class, String.class)
                .forEach((field, value) -> applyBookField(book, field, value, testContext));
        return book;
    }

    public static Author toAuthor(DataTable dataTable, TestContext testContext) {
        Author author = new Author();
        dataTable.asMap(String.class, String.class)
                .forEach((field, value) -> applyAuthorField(author, field, value, testContext));
        return author;
    }

    private static void applyBookField(Book book, String field, String rawValue, TestContext testContext) {
        switch (field) {
            case "id" -> book.setId(parseInteger(field, resolveBookValue(rawValue, testContext)));
            case "title" -> book.setTitle(resolveBookValue(rawValue, testContext));
            case "description" -> book.setDescription(resolveBookValue(rawValue, testContext));
            case "pageCount" -> book.setPageCount(parseInteger(field, resolveBookValue(rawValue, testContext)));
            case "excerpt" -> book.setExcerpt(resolveBookValue(rawValue, testContext));
            case "publishDate" -> book.setPublishDate(resolveBookValue(rawValue, testContext));
            default -> throw new IllegalArgumentException("Unsupported book field in DataTable: " + field);
        }
    }

    private static void applyAuthorField(Author author, String field, String rawValue, TestContext testContext) {
        switch (field) {
            case "id" -> author.setId(parseInteger(field, resolveAuthorValue(rawValue, testContext)));
            case "idBook" -> author.setIdBook(parseInteger(field, resolveAuthorValue(rawValue, testContext)));
            case "firstName" -> author.setFirstName(resolveAuthorValue(rawValue, testContext));
            case "lastName" -> author.setLastName(resolveAuthorValue(rawValue, testContext));
            default -> throw new IllegalArgumentException("Unsupported author field in DataTable: " + field);
        }
    }

    private static String resolveBookValue(String rawValue, TestContext testContext) {
        return switch (normalize(rawValue)) {
            case "AUTO_ID" -> String.valueOf(TestDataFactory.nextBookId());
            default -> resolveSharedValue(rawValue, testContext);
        };
    }

    private static String resolveAuthorValue(String rawValue, TestContext testContext) {
        return switch (normalize(rawValue)) {
            case "AUTO_ID" -> String.valueOf(TestDataFactory.nextAuthorId());
            case "AUTO_BOOK_ID" -> String.valueOf(TestDataFactory.defaultAuthorBookId());
            default -> resolveSharedValue(rawValue, testContext);
        };
    }

    private static String resolveSharedValue(String rawValue, TestContext testContext) {
        if (rawValue == null) {
            return null;
        }

        return switch (normalize(rawValue)) {
            case "NOW" -> TestDataFactory.currentTimestamp();
            case "EMPTY" -> "";
            case "NULL" -> null;
            case "CREATED_BOOK_ID" -> String.valueOf(requiredContextInteger(testContext, ScenarioContextKeys.CREATED_BOOK_ID));
            case "CREATED_AUTHOR_ID" -> String.valueOf(requiredContextInteger(testContext, ScenarioContextKeys.CREATED_AUTHOR_ID));
            default -> rawValue;
        };
    }

    private static Integer parseInteger(String field, String resolvedValue) {
        if (resolvedValue == null) {
            return null;
        }

        try {
            return Integer.valueOf(resolvedValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid integer value for field '" + field + "': " + resolvedValue, exception);
        }
    }

    private static int requiredContextInteger(TestContext testContext, String key) {
        if (testContext == null) {
            throw new IllegalStateException("Test context is required to resolve scenario placeholder: " + key);
        }
        return testContext.getRequiredInteger(key);
    }

    private static String normalize(String rawValue) {
        return rawValue == null ? "" : rawValue.trim().toUpperCase(Locale.ROOT);
    }
}
