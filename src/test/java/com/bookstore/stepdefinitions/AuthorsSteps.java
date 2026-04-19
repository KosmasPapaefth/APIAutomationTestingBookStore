package com.bookstore.stepdefinitions;

import com.bookstore.assertions.AuthorAssertions;
import com.bookstore.client.AuthorsApiClient;
import com.bookstore.context.TestContext;
import com.bookstore.models.Author;
import com.bookstore.utils.AuthorDataTableMapper;
import com.bookstore.utils.ScenarioContextKeys;
import com.bookstore.utils.TestDataFactory;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AuthorsSteps {

    private final TestContext testContext;
    private final AuthorsApiClient authorsApiClient;

    public AuthorsSteps(TestContext testContext, AuthorsApiClient authorsApiClient) {
        this.testContext = testContext;
        this.authorsApiClient = authorsApiClient;
    }

    @When("User retrieves all authors")
    public void userRetrievesAllAuthors() {
        testContext.setLastResponse(authorsApiClient.getAllAuthors());
    }

    @When("User retrieves the author with id {int}")
    public void userRetrievesTheAuthorWithId(int authorId) {
        testContext.setLastResponse(authorsApiClient.getAuthorById(authorId));
    }

    @When("User creates an author with the following data:")
    public void userCreatesAnAuthorWithTheFollowingData(DataTable dataTable) {
        Author requestAuthor = AuthorDataTableMapper.fromDataTable(dataTable, testContext);
        storeRequestAuthor(requestAuthor);
        testContext.setLastResponse(authorsApiClient.createAuthor(requestAuthor));
        rememberCreatedAuthorId();
    }

    @When("User updates the author with id {int} using the following data:")
    public void userUpdatesTheAuthorWithIdUsingTheFollowingData(int authorId, DataTable dataTable) {
        Author requestAuthor = AuthorDataTableMapper.fromDataTable(dataTable, testContext);
        storeRequestAuthor(requestAuthor);
        testContext.setLastResponse(authorsApiClient.updateAuthor(authorId, requestAuthor));
    }

    @When("User deletes the author with id {int}")
    public void userDeletesTheAuthorWithId(int authorId) {
        testContext.setLastResponse(authorsApiClient.deleteAuthor(authorId));
    }

    @When("User retrieves a non-existing author")
    public void userRetrievesANonExistingAuthor() {
        testContext.setLastResponse(authorsApiClient.getAuthorById(TestDataFactory.nextNonExistingAuthorId()));
    }

    @When("User deletes a non-existing author")
    public void userDeletesANonExistingAuthor() {
        testContext.setLastResponse(authorsApiClient.deleteAuthor(TestDataFactory.nextNonExistingAuthorId()));
    }

    @Then("the response should contain a non-empty list of authors")
    public void theResponseShouldContainANonEmptyListOfAuthors() {
        AuthorAssertions.assertAuthorsListIsNotEmpty(testContext.getLastResponse());
    }

    @Then("the response should contain the requested author with id {int}")
    public void theResponseShouldContainTheRequestedAuthorWithId(int expectedAuthorId) {
        AuthorAssertions.assertAuthorId(testContext.getLastResponse(), expectedAuthorId);
    }

    @Then("the author response should satisfy {string}")
    public void theAuthorResponseShouldSatisfy(String expectation) {
        AuthorAssertions.assertScenarioExpectation(testContext.getLastResponse(), testContext.get(ScenarioContextKeys.REQUEST_AUTHOR), expectation);
    }

    @Then("the author should be linked to book id {int}")
    public void theAuthorShouldBeLinkedToBookId(int expectedBookId) {
        AuthorAssertions.assertAuthorLinkedToBook(testContext.getLastResponse(), expectedBookId);
    }

    @Then("the author response should have linked book id {int}")
    public void theAuthorResponseShouldHaveLinkedBookId(int expectedBookId) {
        AuthorAssertions.assertAuthorBookId(testContext.getLastResponse().as(Author.class), expectedBookId);
    }

    private void storeRequestAuthor(Author requestAuthor) {
        testContext.put(ScenarioContextKeys.REQUEST_AUTHOR, requestAuthor);
    }

    private void rememberCreatedAuthorId() {
        if (testContext.getLastResponse().statusCode() == 200 && !testContext.getLastResponse().asString().isBlank()) {
            Author createdAuthor = testContext.getLastResponse().as(Author.class);
            if (createdAuthor.getId() != null) {
                testContext.put(ScenarioContextKeys.CREATED_AUTHOR_ID, createdAuthor.getId());
            }
        }
    }
}
