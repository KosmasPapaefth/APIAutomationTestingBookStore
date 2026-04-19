package com.bookstore.hooks;

import com.bookstore.config.ConfigManager;
import com.bookstore.context.TestContext;
import io.cucumber.java.Before;
import io.restassured.RestAssured;

/**
 * Scenario hooks that prepare Rest Assured and reset shared scenario state before execution.
 */
public class Hooks {

    private final TestContext testContext;

    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    /**
     * Resets Rest Assured, applies the configured base URI and logging behavior, and clears scenario data.
     */
    @Before
    public void beforeScenario() {
        RestAssured.reset();
        RestAssured.baseURI = ConfigManager.getBaseUrl();
        if (ConfigManager.getBoolean("logging.enabled")) {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        }
        testContext.clear();
    }
}
