package com.bookstore.client;

import com.bookstore.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Base Rest Assured client that applies the framework's default request configuration.
 *
 * <p>Concrete API clients inherit these helpers to keep endpoint-specific classes focused on
 * resource operations rather than HTTP setup.</p>
 */
public abstract class BaseApiClient {

    /**
     * Builds a request specification with the default content negotiation headers from configuration.
     *
     * @return configured request specification for the current test run
     */
    protected RequestSpecification request() {
        return RestAssured.given()
                .contentType(ConfigManager.getRequired("default.content.type"))
                .accept(ConfigManager.getRequired("default.accept"));
    }

    /**
     * Sends a GET request to the supplied relative path.
     *
     * @param path endpoint path relative to the configured base URI
     * @return extracted HTTP response
     */
    protected Response get(String path) {
        return request()
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    /**
     * Sends a POST request with the provided body.
     *
     * @param path endpoint path relative to the configured base URI
     * @param body request payload serialized by Rest Assured
     * @return extracted HTTP response
     */
    protected Response post(String path, Object body) {
        return request()
                .body(body)
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }

    /**
     * Sends a PUT request with the provided body.
     *
     * @param path endpoint path relative to the configured base URI
     * @param body request payload serialized by Rest Assured
     * @return extracted HTTP response
     */
    protected Response put(String path, Object body) {
        return request()
                .body(body)
                .when()
                .put(path)
                .then()
                .extract()
                .response();
    }

    /**
     * Sends a DELETE request to the supplied relative path.
     *
     * @param path endpoint path relative to the configured base URI
     * @return extracted HTTP response
     */
    protected Response delete(String path) {
        return request()
                .when()
                .delete(path)
                .then()
                .extract()
                .response();
    }
}
