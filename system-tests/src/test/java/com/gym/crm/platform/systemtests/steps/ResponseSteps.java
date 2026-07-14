package com.gym.crm.platform.systemtests.steps;

import com.gym.crm.platform.systemtests.support.TestContext;
import io.cucumber.java.en.Then;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseSteps {

    private final TestContext context;

    public ResponseSteps(TestContext context) {
        this.context = context;
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int status) {
        assertThat(context.getLastResponse().statusCode()).isEqualTo(status);
    }

    @Then("the response contains generated credentials")
    public void theResponseContainsGeneratedCredentials() {
        assertThat(context.getLastResponse().jsonPath().getString("username")).isNotBlank();
        assertThat(context.getLastResponse().jsonPath().getString("password")).isNotBlank();
    }

    @Then("the response contains an error body")
    public void theResponseContainsAnErrorBody() {
        assertThat(context.getLastResponse().asString()).contains("error");
    }
}