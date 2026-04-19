package com.bookstore.context;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Scenario-scoped storage for API responses and transient data shared across step definitions.
 *
 * <p>The class is intentionally not thread-safe because Cucumber creates a separate instance per
 * scenario through PicoContainer, avoiding shared mutable state across parallel executions.</p>
 */
public class TestContext {

    private Response lastResponse;
    private final Map<String, Object> scenarioData = new HashMap<>();

    /**
     * Returns the most recent API response stored for the current scenario.
     *
     * @return last response captured by a step definition, or {@code null} if none has been stored
     */
    public Response getLastResponse() {
        return lastResponse;
    }

    /**
     * Stores the latest API response for later assertions or follow-up steps.
     *
     * @param lastResponse response to store for the current scenario
     */
    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }

    /**
     * Stores a scenario-scoped value under the provided key.
     *
     * @param key logical key used to retrieve the value later in the scenario
     * @param value value to store
     */
    public void put(String key, Object value) {
        scenarioData.put(key, value);
    }

    /**
     * Retrieves a scenario-scoped value without enforcing type or presence checks.
     *
     * @param key logical key used to locate the value
     * @param <T> expected return type
     * @return stored value cast to the requested type, or {@code null} when absent
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) scenarioData.get(key);
    }

    /**
     * Retrieves a required scenario value and converts it to an integer when possible.
     *
     * @param key logical key used to locate the value
     * @return stored integer value
     */
    public int getRequiredInteger(String key) {
        Object value = scenarioData.get(key);
        if (value == null) {
            throw new IllegalStateException("No scenario value has been stored for key: " + key);
        }
        if (value instanceof Integer integerValue) {
            return integerValue;
        }
        if (value instanceof Number numericValue) {
            return numericValue.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Scenario value for key '" + key + "' is not a valid integer.", exception);
        }
    }

    /**
     * Clears the stored response and all scenario-scoped values before a new scenario starts.
     */
    public void clear() {
        lastResponse = null;
        scenarioData.clear();
    }
}
