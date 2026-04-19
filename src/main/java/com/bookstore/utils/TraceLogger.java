package com.bookstore.utils;

import com.bookstore.config.ConfigManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Logs trace-related diagnostics for server-side API failures.
 *
 * <p>This utility prepares the framework for future observability integrations by extracting
 * trace and correlation identifiers from failed API responses without introducing an Elastic
 * dependency or external logging client.</p>
 */
public final class TraceLogger {

    private static final Logger LOGGER = Logger.getLogger(TraceLogger.class.getName());
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String TRACE_LOGGING_ENABLED_KEY = "trace.logging.enabled";
    private static final Set<String> TRACE_ID_KEYS = Set.of(
            "traceid",
            "xtraceid",
            "xb3traceid",
            "traceparent"
    );
    private static final Set<String> CORRELATION_ID_KEYS = Set.of(
            "correlationid",
            "xcorrelationid"
    );
    private static final Set<String> REQUEST_ID_KEYS = Set.of(
            "requestid",
            "xrequestid"
    );

    private TraceLogger() {
    }

    /**
     * Logs structured diagnostics when the API response status code is 5xx.
     *
     * @param response HTTP response returned by Rest Assured
     * @param endpoint endpoint path used for the request
     */
    public static void logIfServerError(Response response, String endpoint) {
        if (response == null || !isTraceLoggingEnabled() || !isServerError(response.statusCode())) {
            return;
        }

        String responseBody = safeResponseBody(response);
        TraceDetails traceDetails = extractTraceDetails(response, responseBody);
        LOGGER.severe(formatLogMessage(response.statusCode(), endpoint, traceDetails, responseBody));
    }

    private static boolean isTraceLoggingEnabled() {
        return Boolean.parseBoolean(ConfigManager.getRequired(TRACE_LOGGING_ENABLED_KEY));
    }

    private static boolean isServerError(int statusCode) {
        return statusCode >= 500 && statusCode <= 599;
    }

    private static TraceDetails extractTraceDetails(Response response, String responseBody) {
        String traceId = firstNonBlank(
                extractHeader(response, TRACE_ID_KEYS),
                extractJsonField(responseBody, TRACE_ID_KEYS)
        );
        String correlationId = firstNonBlank(
                extractHeader(response, CORRELATION_ID_KEYS),
                extractJsonField(responseBody, CORRELATION_ID_KEYS)
        );
        String requestId = firstNonBlank(
                extractHeader(response, REQUEST_ID_KEYS),
                extractJsonField(responseBody, REQUEST_ID_KEYS)
        );

        return new TraceDetails(traceId, correlationId, requestId);
    }

    private static String extractHeader(Response response, Set<String> acceptedKeys) {
        for (Header header : response.headers().asList()) {
            if (acceptedKeys.contains(normalizeKey(header.getName()))) {
                return blankToNull(header.getValue());
            }
        }
        return null;
    }

    private static String extractJsonField(String responseBody, Set<String> acceptedKeys) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(responseBody);
            return findFieldValue(rootNode, acceptedKeys);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String findFieldValue(JsonNode node, Set<String> acceptedKeys) {
        if (node == null || node.isNull()) {
            return null;
        }

        if (node.isObject()) {
            var fields = node.fields();
            while (fields.hasNext()) {
                var field = fields.next();
                if (acceptedKeys.contains(normalizeKey(field.getKey()))) {
                    return blankToNull(field.getValue().asText());
                }

                String nestedValue = findFieldValue(field.getValue(), acceptedKeys);
                if (nestedValue != null) {
                    return nestedValue;
                }
            }
        }

        if (node.isArray()) {
            for (JsonNode childNode : node) {
                String nestedValue = findFieldValue(childNode, acceptedKeys);
                if (nestedValue != null) {
                    return nestedValue;
                }
            }
        }

        return null;
    }

    private static String formatLogMessage(
            int statusCode,
            String endpoint,
            TraceDetails traceDetails,
            String responseBody
    ) {
        String bodyLogValue = traceDetails.hasAnyIdentifier()
                ? "<omitted because trace identifiers were found>"
                : blankToPlaceholder(responseBody);

        return System.lineSeparator()
                + "[ERROR] API FAILURE DETECTED" + System.lineSeparator()
                + "Status: " + statusCode + System.lineSeparator()
                + "Endpoint: " + blankToPlaceholder(endpoint) + System.lineSeparator()
                + "TraceId: " + blankToPlaceholder(traceDetails.traceId()) + System.lineSeparator()
                + "CorrelationId: " + blankToPlaceholder(traceDetails.correlationId()) + System.lineSeparator()
                + "RequestId: " + blankToPlaceholder(traceDetails.requestId()) + System.lineSeparator()
                + "ResponseBody: " + bodyLogValue;
    }

    private static String safeResponseBody(Response response) {
        try {
            return response.asString();
        } catch (Exception exception) {
            return "<unable to read response body: " + exception.getMessage() + ">";
        }
    }

    private static String firstNonBlank(String firstValue, String secondValue) {
        return firstValue != null ? firstValue : secondValue;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private static String blankToPlaceholder(String value) {
        return value == null || value.isBlank() ? "<not available>" : value;
    }

    private static String normalizeKey(String key) {
        return key == null
                ? ""
                : key.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "").replace(".", "");
    }

    private record TraceDetails(String traceId, String correlationId, String requestId) {

        private boolean hasAnyIdentifier() {
            return traceId != null || correlationId != null || requestId != null;
        }
    }
}
