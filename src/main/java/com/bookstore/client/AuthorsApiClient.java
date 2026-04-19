package com.bookstore.client;

import com.bookstore.config.ConfigManager;
import io.restassured.response.Response;

/**
 * API client for the FakeRestAPI authors resource.
 */
public class AuthorsApiClient extends BaseApiClient {

    private static final String AUTHORS_PATH = ConfigManager.getRequired("authors.endpoint");

    /**
     * Retrieves the full authors collection.
     *
     * @return response containing the authors list payload
     */
    public Response getAllAuthors() {
        return get(AUTHORS_PATH);
    }

    /**
     * Retrieves a single author by identifier.
     *
     * @param id unique author identifier used in the request path
     * @return response containing the author payload or the API error response
     */
    public Response getAuthorById(int id) {
        return get(AUTHORS_PATH + "/" + id);
    }

    /**
     * Creates an author using the supplied request body.
     *
     * @param body request payload to be serialized and posted to the authors endpoint
     * @return response returned by the API after the create request
     */
    public Response createAuthor(Object body) {
        return post(AUTHORS_PATH, body);
    }

    /**
     * Updates the author identified by the given id.
     *
     * @param id unique author identifier used in the request path
     * @param body request payload representing the desired author state
     * @return response returned by the API after the update request
     */
    public Response updateAuthor(int id, Object body) {
        return put(AUTHORS_PATH + "/" + id, body);
    }

    /**
     * Deletes the author identified by the given id.
     *
     * @param id unique author identifier used in the request path
     * @return response returned by the API after the delete request
     */
    public Response deleteAuthor(int id) {
        return delete(AUTHORS_PATH + "/" + id);
    }
}
