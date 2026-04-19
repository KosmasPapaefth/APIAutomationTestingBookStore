package com.bookstore.client;

import com.bookstore.config.ConfigManager;
import io.restassured.response.Response;

/**
 * API client for the FakeRestAPI books resource.
 */
public class BooksApiClient extends BaseApiClient {

    private static final String BOOKS_PATH = ConfigManager.getRequired("books.endpoint");

    /**
     * Retrieves the full books collection.
     *
     * @return response containing the books list payload
     */
    public Response getAllBooks() {
        return get(BOOKS_PATH);
    }

    /**
     * Retrieves a single book by identifier.
     *
     * @param id unique book identifier used in the request path
     * @return response containing the book payload or the API error response
     */
    public Response getBookById(int id) {
        return get(BOOKS_PATH + "/" + id);
    }

    /**
     * Creates a book using the supplied request body.
     *
     * @param body request payload to be serialized and posted to the books endpoint
     * @return response returned by the API after the create request
     */
    public Response createBook(Object body) {
        return post(BOOKS_PATH, body);
    }

    /**
     * Updates the book identified by the given id.
     *
     * @param id unique book identifier used in the request path
     * @param body request payload representing the desired book state
     * @return response returned by the API after the update request
     */
    public Response updateBook(int id, Object body) {
        return put(BOOKS_PATH + "/" + id, body);
    }

    /**
     * Deletes the book identified by the given id.
     *
     * @param id unique book identifier used in the request path
     * @return response returned by the API after the delete request
     */
    public Response deleteBook(int id) {
        return delete(BOOKS_PATH + "/" + id);
    }
}
