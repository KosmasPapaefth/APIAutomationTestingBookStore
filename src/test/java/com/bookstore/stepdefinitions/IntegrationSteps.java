package com.bookstore.stepdefinitions;

import com.bookstore.client.BookstoreApiClient;
import com.bookstore.context.TestContext;
import com.bookstore.models.Author;
import com.bookstore.utils.DataTableMapper;
import com.bookstore.utils.ScenarioContextKeys;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;

/**
 * Step definitions for cross-entity scenarios that link books and authors through stored scenario ids.
 */
public class IntegrationSteps {

    private final TestContext testContext;
    private final BookstoreApiClient client;

    public IntegrationSteps(TestContext testContext, BookstoreApiClient client) {
        this.testContext = testContext;
        this.client = client;
    }

    @When("User retrieves the created book")
    public void userRetrievesTheCreatedBook() {
        int bookId = testContext.getRequiredInteger(ScenarioContextKeys.CREATED_BOOK_ID);
        testContext.setLastResponse(client.getBookById(bookId));
    }

    @When("User retrieves the created author")
    public void userRetrievesTheCreatedAuthor() {
        int authorId = testContext.getRequiredInteger(ScenarioContextKeys.CREATED_AUTHOR_ID);
        testContext.setLastResponse(client.getAuthorById(authorId));
    }

    /**
     * Updates the author created earlier in the scenario using a DataTable-driven payload.
     *
     * @param dataTable DataTable containing the author fields to update
     */
    @When("User updates the created author with the following data:")
    public void userUpdatesTheCreatedAuthorWithTheFollowingData(DataTable dataTable) {
        Author requestAuthor = DataTableMapper.toAuthor(dataTable, testContext);
        int authorId = testContext.getRequiredInteger(ScenarioContextKeys.CREATED_AUTHOR_ID);

        testContext.put(ScenarioContextKeys.REQUEST_AUTHOR, requestAuthor);
        testContext.setLastResponse(client.updateAuthor(authorId, requestAuthor));
    }

    @When("User deletes the created author")
    public void userDeletesTheCreatedAuthor() {
        int authorId = testContext.getRequiredInteger(ScenarioContextKeys.CREATED_AUTHOR_ID);
        testContext.setLastResponse(client.deleteAuthor(authorId));
    }

    @When("User deletes the created book")
    public void userDeletesTheCreatedBook() {
        int bookId = testContext.getRequiredInteger(ScenarioContextKeys.CREATED_BOOK_ID);
        testContext.setLastResponse(client.deleteBook(bookId));
    }
}
