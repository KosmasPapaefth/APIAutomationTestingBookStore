package com.bookstore.stepdefinitions;

import com.bookstore.assertions.BookAssertions;
import com.bookstore.client.BookstoreApiClient;
import com.bookstore.context.TestContext;
import com.bookstore.models.Book;
import com.bookstore.utils.DataTableMapper;
import com.bookstore.utils.ScenarioContextKeys;
import com.bookstore.utils.TestDataFactory;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BooksSteps {

    private final TestContext testContext;
    private final BookstoreApiClient client;

    public BooksSteps(TestContext testContext, BookstoreApiClient client) {
        this.testContext = testContext;
        this.client = client;
    }

    @When("User retrieves all books")
    public void userRetrievesAllBooks() {
        testContext.setLastResponse(client.getAllBooks());
    }

    @When("User retrieves the book with id {int}")
    public void userRetrievesTheBookWithId(int bookId) {
        testContext.setLastResponse(client.getBookById(bookId));
    }

    @When("User creates a book with the following data:")
    public void userCreatesABookWithTheFollowingData(DataTable dataTable) {
        Book requestBook = DataTableMapper.toBook(dataTable, testContext);
        testContext.put(ScenarioContextKeys.REQUEST_BOOK, requestBook);
        testContext.setLastResponse(client.createBook(requestBook));
        rememberCreatedBookId();
    }

    @When("User updates the book with id {int} using the following data:")
    public void userUpdatesTheBookWithIdUsingTheFollowingData(int bookId, DataTable dataTable) {
        Book requestBook = DataTableMapper.toBook(dataTable, testContext);
        testContext.put(ScenarioContextKeys.REQUEST_BOOK, requestBook);
        testContext.setLastResponse(client.updateBook(bookId, requestBook));
    }

    @When("User deletes the existing book with id {int}")
    public void userDeletesTheExistingBookWithId(int bookId) {
        testContext.setLastResponse(client.deleteBook(bookId));
    }

    @When("User retrieves a non-existing book")
    public void userRetrievesANonExistingBook() {
        testContext.setLastResponse(client.getBookById(TestDataFactory.nextNonExistingBookId()));
    }

    @When("User deletes a non-existing book")
    public void userDeletesANonExistingBook() {
        testContext.setLastResponse(client.deleteBook(TestDataFactory.nextNonExistingBookId()));
    }

    @Then("the response should contain a non-empty list of books")
    public void theResponseShouldContainANonEmptyListOfBooks() {
        BookAssertions.assertBooksListIsNotEmpty(testContext.getLastResponse());
    }

    @Then("the response should contain the requested book with id {int}")
    public void theResponseShouldContainTheRequestedBookWithId(int expectedId) {
        BookAssertions.assertBookId(testContext.getLastResponse(), expectedId);
    }

    @Then("the book response should satisfy {string}")
    public void theBookResponseShouldSatisfy(String expectation) {
        BookAssertions.assertScenarioExpectation(testContext.getLastResponse(), testContext.get(ScenarioContextKeys.REQUEST_BOOK), expectation);
    }

    private void rememberCreatedBookId() {
        if (testContext.getLastResponse().statusCode() == 200 && !testContext.getLastResponse().asString().isBlank()) {
            Book createdBook = testContext.getLastResponse().as(Book.class);
            if (createdBook.getId() != null) {
                testContext.put(ScenarioContextKeys.CREATED_BOOK_ID, createdBook.getId());
            }
        }
    }
}
