package com.gym.crm.platform.systemtests.steps;

import com.gym.crm.platform.systemtests.client.ApiClient;
import com.gym.crm.platform.systemtests.config.TestProperties;
import com.gym.crm.platform.systemtests.support.Payloads;
import com.gym.crm.platform.systemtests.support.TestContext;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Locale;

public class CoreSteps {

    private final TestContext context;
    private final ApiClient coreClient;

    public CoreSteps(TestContext context) {
        this.context = context;
        this.coreClient = new ApiClient(TestProperties.coreBaseUrl());
    }

    @When("trainee is registered through core service")
    public void aTraineeIsRegisteredThroughCoreService() {
        Response response = coreClient.post("/trainees/register", null, Payloads.trainee(uniqueName("Trainee"), uniqueName("User")));

        context.setLastResponse(response);
        rememberCredentials("trainee", response);
    }

    @When("training types are requested without authorization")
    public void trainingTypesAreRequestedWithoutAuthorization() {
        context.setLastResponse(coreClient.get("/trainings/types", null, java.util.Map.of()));
    }

    private void rememberCredentials(String prefix, Response response) {
        if (response.statusCode() == 200) {
            context.put(String.format("%sUsername", prefix), response.jsonPath().getString("username"));
            context.put(String.format("%sPassword", prefix), response.jsonPath().getString("password"));
        }
    }

    private String uniqueName(String prefix) {
        return (prefix + System.nanoTime()).toLowerCase(Locale.ROOT);
    }
}