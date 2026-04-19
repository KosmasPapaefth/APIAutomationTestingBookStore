package com.bookstore.utils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataFactory {

    private static final int ID_MIN = 100_000;
    private static final int ID_MAX = 999_999;

    private TestDataFactory() {
    }

    public static int nextBookId() {
        return ThreadLocalRandom.current().nextInt(ID_MIN, ID_MAX);
    }

    public static int nextAuthorId() {
        return ThreadLocalRandom.current().nextInt(ID_MIN, ID_MAX);
    }

    public static int nextNonExistingBookId() {
        return ThreadLocalRandom.current().nextInt(1_000_000, 9_999_999);
    }

    public static int nextNonExistingAuthorId() {
        return ThreadLocalRandom.current().nextInt(1_000_000, 9_999_999);
    }

    public static int defaultAuthorBookId() {
        return 1;
    }

    public static String currentTimestamp() {
        return OffsetDateTime.now(ZoneOffset.UTC).withNano(0).toString();
    }
}
