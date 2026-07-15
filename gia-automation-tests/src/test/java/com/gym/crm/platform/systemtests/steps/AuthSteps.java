package com.gym.crm.platform.systemtests.steps;

import com.gym.crm.platform.systemtests.client.ApiClient;
import com.gym.crm.platform.systemtests.config.TestProperties;
import com.gym.crm.platform.systemtests.support.Payloads;
import com.gym.crm.platform.systemtests.support.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthSteps {

    private final TestContext context;
    private final ApiClient coreClient;

    public AuthSteps(TestContext context) {
        this.context = context;
        this.coreClient = new ApiClient(TestProperties.coreBaseUrl());
    }

    @Given("an authenticated gym user")
    public void anAuthenticatedGymUser() {
        ensureRegisteredUser();
        Response response = coreClient.post("/auth/login", null, Payloads.login(context.getString("authUsername"), context.getString("authPassword")));

        assertThat(response.statusCode()).isEqualTo(200);
        context.setToken(response.jsonPath().getString("token"));
        assertThat(context.getToken()).isNotBlank();
    }

    @When("the user logs in with valid credentials")
    public void theUserLogsInWithValidCredentials() {
        context.setLastResponse(coreClient.post("/auth/login", null, Payloads.login(TestProperties.defaultUsername(), TestProperties.defaultPassword())));
    }

    @When("the user logs in with invalid credentials")
    public void theUserLogsInWithInvalidCredentials() {
        context.setLastResponse(coreClient.post("/auth/login", null, Payloads.login("unknown.user", "wrong-password")));
    }

    private void ensureRegisteredUser() {
        if (context.getString("authUsername") != null) {
            return;
        }

        Response response = coreClient.post("/trainees/register", null, Payloads.trainee(uniqueName("Auth"), uniqueName("User")));

        assertThat(response.statusCode()).isEqualTo(200);
        context.put("authUsername", response.jsonPath().getString("username"));
        context.put("authPassword", response.jsonPath().getString("password"));
    }

    private String uniqueName(String prefix) {
        return (prefix + System.nanoTime()).toLowerCase(Locale.ROOT);
    }
}