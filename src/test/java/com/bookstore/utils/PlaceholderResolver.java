package com.bookstore.utils;

import com.bookstore.context.TestContext;

import java.util.Locale;

/**
 * Resolves placeholder values used in DataTable-driven scenarios.
 *
 * <p>The resolver supports generated ids, values captured earlier in the scenario, timestamps,
 * empty strings, and explicit {@code null} handling.</p>
 */
public final class PlaceholderResolver {

    private PlaceholderResolver() {
    }

    /**
     * Resolves a placeholder or raw value for a book payload field.
     *
     * @param rawValue raw value from the feature DataTable
     * @param testContext scenario context used when a placeholder depends on previously stored values
     * @return resolved value ready for book mapping
     */
    public static String resolveBookValue(String rawValue, TestContext testContext) {
        String normalizedValue = normalize(rawValue);
        return switch (normalizedValue) {
            case "AUTO_ID" -> String.valueOf(TestDataFactory.nextBookId());
            default -> resolveSharedValue(rawValue, testContext);
        };
    }

    /**
     * Resolves a placeholder or raw value for an author payload field.
     *
     * @param rawValue raw value from the feature DataTable
     * @param testContext scenario context used when a placeholder depends on previously stored values
     * @return resolved value ready for author mapping
     */
    public static String resolveAuthorValue(String rawValue, TestContext testContext) {
        String normalizedValue = normalize(rawValue);
        return switch (normalizedValue) {
            case "AUTO_ID" -> String.valueOf(TestDataFactory.nextAuthorId());
            case "AUTO_BOOK_ID" -> String.valueOf(TestDataFactory.defaultAuthorBookId());
            default -> resolveSharedValue(rawValue, testContext);
        };
    }

    private static String resolveSharedValue(String rawValue, TestContext testContext) {
        if (rawValue == null) {
            return null;
        }

        String normalizedValue = normalize(rawValue);
        return switch (normalizedValue) {
            case "NOW" -> TestDataFactory.currentTimestamp();
            case "EMPTY" -> "";
            case "NULL" -> null;
            case "CREATED_BOOK_ID" -> String.valueOf(getRequiredInteger(testContext, ScenarioContextKeys.CREATED_BOOK_ID, normalizedValue));
            case "CREATED_AUTHOR_ID" -> String.valueOf(getRequiredInteger(testContext, ScenarioContextKeys.CREATED_AUTHOR_ID, normalizedValue));
            default -> rawValue;
        };
    }

    private static int getRequiredInteger(TestContext testContext, String key, String placeholderName) {
        if (testContext == null) {
            throw new IllegalStateException("Test context is required to resolve placeholder: " + placeholderName);
        }
        return testContext.getRequiredInteger(key);
    }

    private static String normalize(String rawValue) {
        return rawValue == null ? "" : rawValue.trim().toUpperCase(Locale.ROOT);
    }
}
