package com.bookstore.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

/**
 * Centralized access point for framework configuration loaded from {@code config.properties}.
 *
 * <p>Values can be overridden via JVM system properties or environment variables, allowing the
 * same build to run against different environments without code changes.</p>
 */
public final class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final String ENV_KEY = "env";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigManager() {
    }

    /**
     * Resolves the base URL for the active environment.
     *
     * @return environment-specific base URL
     */
    public static String getBaseUrl() {
        String environment = getRequired(ENV_KEY).trim().toLowerCase(Locale.ROOT);
        return getRequired("base.url." + environment);
    }

    /**
     * Returns a required configuration value.
     *
     * @param key property key to resolve
     * @return resolved configuration value
     */
    public static String getRequired(String key) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration value for key: " + key);
        }
        return value;
    }

    /**
     * Returns a configuration value as a boolean.
     *
     * @param key property key to resolve
     * @return parsed boolean value
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getRequired(key));
    }

    /**
     * Returns a configuration value as an integer.
     *
     * @param key property key to resolve
     * @return parsed integer value
     */
    public static int getInt(String key) {
        String value = getRequired(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Invalid integer configuration value for key: " + key, exception);
        }
    }

    /**
     * Resolves a configuration value from system properties, environment variables, or the loaded file.
     *
     * @param key property key to resolve
     * @return resolved value, or {@code null} when the key is not configured
     */
    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String environmentKey = key.toUpperCase(Locale.ROOT).replace('.', '_');
        String environmentValue = System.getenv(environmentKey);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return PROPERTIES.getProperty(key);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (Objects.isNull(inputStream)) {
                throw new IllegalStateException("Could not load " + CONFIG_FILE + " from the classpath.");
            }
            properties.load(inputStream);
            validateRequiredProperties(properties);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load configuration file: " + CONFIG_FILE, exception);
        }
    }

    private static void validateRequiredProperties(Properties properties) {
        requireProperty(properties, ENV_KEY);
        requireProperty(properties, "default.content.type");
        requireProperty(properties, "default.accept");
        requireProperty(properties, "logging.enabled");
        requireProperty(properties, "timeout.connection");
        requireProperty(properties, "timeout.socket");
        requireProperty(properties, "xray.enabled");
        requireProperty(properties, "xray.results.path");
        requireProperty(properties, "books.endpoint");
        requireProperty(properties, "authors.endpoint");

        String environment = properties.getProperty(ENV_KEY);
        if (environment == null || environment.isBlank()) {
            throw new IllegalStateException("Missing required configuration value for key: " + ENV_KEY);
        }

        requireProperty(properties, "base.url." + environment.trim().toLowerCase(Locale.ROOT));
    }

    private static void requireProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration value for key: " + key);
        }
    }
}
