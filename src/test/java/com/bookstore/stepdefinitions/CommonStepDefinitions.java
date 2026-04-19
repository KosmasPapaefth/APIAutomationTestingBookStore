package com.bookstore.stepdefinitions;

import com.bookstore.assertions.ApiAssertions;
import com.bookstore.config.ConfigManager;
import com.bookstore.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class CommonStepDefinitions {

    private final TestContext testContext;

    public CommonStepDefinitions(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("the Online Bookstore API is available")
    public void theOnlineBookstoreApiIsAvailable() {
        assertFalse(ConfigManager.getBaseUrl().isBlank(), "The API base URL is not configured.");
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatusCode) {
        ApiAssertions.assertStatusCode(testContext.getLastResponse(), expectedStatusCode);
    }

    @Then("the delete response should {string}")
    public void theDeleteResponseShould(String expectation) {
        ApiAssertions.assertDeleteExpectation(testContext.getLastResponse(), expectation);
    }
}
