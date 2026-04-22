package stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import utils.FileUtility;
import utils.RestUtility;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class AuthSteps {

    private Response response;
    private String baseUrl;
    private String apiKey;
    private long startTime;


    @Given("the authentication API is accessible with a valid API key")
    public void theAuthenticationAPIIsAccessibleWithAValidAPIKey() {
        baseUrl = FileUtility.getProperty("base.url");
        apiKey  = FileUtility.getProperty("api.key");
        assertNotNull(baseUrl, "Base URL must not be null");
        assertNotNull(apiKey,  "API Key must not be null");
    }

    @When("I send a POST request to {string} with email {string}, password {string} and name {string}")
    public void iSendAPostRequestToRegisterWithEmailPasswordAndName(
            String endpoint, String email, String password, String name) {

        String url = baseUrl + endpoint;

        Map<String, String> payload = new java.util.HashMap<>();
        payload.put("email",    email);
        payload.put("password", password);
        if (name != null && !name.isEmpty()) {
            payload.put("name", name);
        }

        startTime = System.currentTimeMillis();
        response  = RestUtility.post(url, apiKey, payload);
    }

    @Then("the register response status should be {int}")
    public void theRegisterResponseStatusShouldBe(int expectedStatus) {
        assertEquals(response.getStatusCode(), expectedStatus,
                "Expected status " + expectedStatus + " but got " + response.getStatusCode());
    }


    @Then("the response Content-Type should contain {string}")
    public void theResponseContentTypeShouldContain(String expectedContentType) {
        String actualContentType = response.getContentType();
        assertTrue(actualContentType.contains(expectedContentType),
                "Expected Content-Type to contain '" + expectedContentType
                        + "' but got '" + actualContentType + "'");
    }

    @Then("the response time should be within {int} ms")
    public void theResponseTimeShouldBeWithinMs(int maxMs) {
        long elapsed = System.currentTimeMillis() - startTime;
        assertTrue(elapsed <= maxMs,
                "Response time " + elapsed + "ms exceeded limit of " + maxMs + "ms");
    }


    @Given("the test user is registered")
    public void theTestUserIsRegistered() {
        String url = baseUrl + "/register";

        Map<String, String> payload = new java.util.HashMap<>();
        payload.put("email",    FileUtility.getProperty("test.user.email"));
        payload.put("password", FileUtility.getProperty("test.user.password"));
        payload.put("name",     FileUtility.getProperty("test.user.name"));

        Response reg = RestUtility.post(url, apiKey, payload);
        // 200 = newly created, 409 = already exists — both are acceptable preconditions
        assertTrue(reg.getStatusCode() == 200 || reg.getStatusCode() == 409,
                "Pre-condition setup failed. Status: " + reg.getStatusCode());
    }


    @When("I login with following details")
    public void iLoginWithFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> credentials = rows.get(0);

        Map<String, String> payload = new java.util.HashMap<>();

        if (credentials.containsKey("email")) {
            payload.put("email", credentials.get("email"));
        }
        if (credentials.containsKey("password")) {
            payload.put("password", credentials.get("password"));
        }

        String url = baseUrl + "/login";
        startTime  = System.currentTimeMillis();
        response   = RestUtility.post(url, apiKey, payload);
    }

    @When("I login with following details without API key")
    public void iLoginWithFollowingDetailsWithoutAPIKey(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> credentials = rows.get(0);

        Map<String, String> payload = new java.util.HashMap<>();
        payload.put("email",    credentials.get("email"));
        payload.put("password", credentials.get("password"));

        String url = baseUrl + "/login";
        startTime  = System.currentTimeMillis();

        response = RestUtility.post(url, "", payload);
    }



    @Then("the login response status should be {int}")
    public void theLoginResponseStatusShouldBe(int expectedStatus) {
        assertEquals(response.getStatusCode(), expectedStatus,
                "Expected status " + expectedStatus + " but got " + response.getStatusCode());
    }

    @Then("the response should contain JWT token")
    public void theResponseShouldContainJWTToken() {
    
        String body = response.getBody().asString();
        assertTrue(
                body.contains("token") || body.contains("accessToken") || body.contains("jwt"),
                "Response body does not contain a JWT token field. Body: " + body
        );
    }
}