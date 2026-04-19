package com.bookstore.client;

import com.bookstore.config.ConfigManager;
import com.bookstore.utils.TraceLogger;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Single API client for the Bookstore API resources used by the test suite.
 */
public class BookstoreApiClient {

    private static final String BOOKS_PATH = ConfigManager.getRequired("books.endpoint");
    private static final String AUTHORS_PATH = ConfigManager.getRequired("authors.endpoint");

    public Response getAllBooks() {
        return get(BOOKS_PATH);
    }

    public Response getBookById(int id) {
        return get(BOOKS_PATH + "/" + id);
    }

    public Response createBook(Object body) {
        return post(BOOKS_PATH, body);
    }

    public Response updateBook(int id, Object body) {
        return put(BOOKS_PATH + "/" + id, body);
    }

    public Response deleteBook(int id) {
        return delete(BOOKS_PATH + "/" + id);
    }

    public Response deleteBookByRawId(String rawId) {
        return delete(BOOKS_PATH + "/" + rawId);
    }

    public Response getAllAuthors() {
        return get(AUTHORS_PATH);
    }

    public Response getAuthorById(int id) {
        return get(AUTHORS_PATH + "/" + id);
    }

    public Response createAuthor(Object body) {
        return post(AUTHORS_PATH, body);
    }

    public Response updateAuthor(int id, Object body) {
        return put(AUTHORS_PATH + "/" + id, body);
    }

    public Response deleteAuthor(int id) {
        return delete(AUTHORS_PATH + "/" + id);
    }

    public Response deleteAuthorByRawId(String rawId) {
        return delete(AUTHORS_PATH + "/" + rawId);
    }

    private RequestSpecification request() {
        return RestAssured.given()
                .contentType(ConfigManager.getRequired("default.content.type"))
                .accept(ConfigManager.getRequired("default.accept"));
    }

    private Response get(String path) {
        return handleResponse(request()
                .when()
                .get(path)
                .then()
                .extract()
                .response(), path);
    }

    private Response post(String path, Object body) {
        return handleResponse(request()
                .body(body)
                .when()
                .post(path)
                .then()
                .extract()
                .response(), path);
    }

    private Response put(String path, Object body) {
        return handleResponse(request()
                .body(body)
                .when()
                .put(path)
                .then()
                .extract()
                .response(), path);
    }

    private Response delete(String path) {
        return handleResponse(request()
                .when()
                .delete(path)
                .then()
                .extract()
                .response(), path);
    }

    private Response handleResponse(Response response, String path) {
        TraceLogger.logIfServerError(response, path);
        return response;
    }
}
