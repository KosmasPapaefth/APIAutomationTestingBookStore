package com.bookstore.stepdefinitions;

import com.bookstore.client.AuthorsApiClient;
import com.bookstore.client.BooksApiClient;
import com.bookstore.context.TestContext;
import com.bookstore.models.Author;
import com.bookstore.utils.AuthorDataTableMapper;
import com.bookstore.utils.ScenarioContextKeys;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;

/**
 * Step definitions for cross-entity scenarios that link books and authors through stored scenario ids.
 */
public class IntegrationSteps {

    private final TestContext testContext;
    private final BooksApiClient booksApiClient;
    private final AuthorsApiClient authorsApiClient;

    public IntegrationSteps(TestContext testContext, BooksApiClient booksApiClient, AuthorsApiClient authorsApiClient) {
        this.testContext = testContext;
        this.booksApiClient = booksApiClient;
        this.authorsApiClient = authorsApiClient;
    }

    @When("User retrieves the created book")
    public void userRetrievesTheCreatedBook() {
        testContext.setLastResponse(
                booksApiClient.getBookById(
                        testContext.getRequiredInteger(ScenarioContextKeys.CREATED_BOOK_ID)
                )
        );
    }

    @When("User retrieves the created author")
    public void userRetrievesTheCreatedAuthor() {
        testContext.setLastResponse(
                authorsApiClient.getAuthorById(
                        testContext.getRequiredInteger(ScenarioContextKeys.CREATED_AUTHOR_ID)
                )
        );
    }

    /**
     * Updates the author created earlier in the scenario using a DataTable-driven payload.
     *
     * @param dataTable DataTable containing the author fields to update
     */
    @When("User updates the created author with the following data:")
    public void userUpdatesTheCreatedAuthorWithTheFollowingData(DataTable dataTable) {
        Author requestAuthor = AuthorDataTableMapper.fromDataTable(dataTable, testContext);
        testContext.put(ScenarioContextKeys.REQUEST_AUTHOR, requestAuthor);
        testContext.setLastResponse(
                authorsApiClient.updateAuthor(
                        testContext.getRequiredInteger(ScenarioContextKeys.CREATED_AUTHOR_ID),
                        requestAuthor
                )
        );
    }

    @When("User deletes the created author")
    public void userDeletesTheCreatedAuthor() {
        testContext.setLastResponse(
                authorsApiClient.deleteAuthor(
                        testContext.getRequiredInteger(ScenarioContextKeys.CREATED_AUTHOR_ID)
                )
        );
    }

    @When("User deletes the created book")
    public void userDeletesTheCreatedBook() {
        testContext.setLastResponse(
                booksApiClient.deleteBook(
                        testContext.getRequiredInteger(ScenarioContextKeys.CREATED_BOOK_ID)
                )
        );
    }
}
