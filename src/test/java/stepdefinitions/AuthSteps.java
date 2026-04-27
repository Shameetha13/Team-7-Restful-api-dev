package stepdefinitions;

import java.util.List;
import java.util.Map;
import org.testng.Assert;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import pojoclass.AuthRequest;
import utils.FileUtility;
import utils.RestUtility;

// Author Shameetha Ravikumar
public class AuthSteps {

    private Response response;

    @Given("the API URL is accessible with a valid API key")
    public void setup() {
       
        String apiKey = FileUtility.get("api.key");
        Assert.assertNotNull(apiKey, "API key must not be null in config.properties");
        Assert.assertFalse(apiKey.isEmpty(), "API key must not be empty in config.properties");
    }

    @When("I send a POST request with email {string}, password {string} and name {string}")
    public void sendPostRequest(String email, String password, String name) {
    	String registerEndpoint = FileUtility.get("endpoint.register");
        AuthRequest request = new AuthRequest();
        request.setEmail(email);
        request.setPassword(password);
        if (name != null && !name.isEmpty()) {
            request.setName(name);
        }
        response = RestUtility.post(registerEndpoint, request);
    }

    @Then("validate the response status code {int}")
    public void validateStatusCode(int statusCode) {
        response.then().log().all().statusCode(statusCode);
    }

    @And("the status message should contain {string}")
    public void validateStatusMessage(String expectedStatus) {
        String statusLine = response.getStatusLine();
        Assert.assertNotNull(statusLine, "Status line is null");
        Assert.assertTrue(statusLine.contains(expectedStatus),
                "Expected status message: " + expectedStatus + " but got: " + statusLine);
    }

    @And("the response should contain {string}")
    public void validateEmail(String expectedEmail) {
        String actualEmail = response.jsonPath().getString("user.email");
        Assert.assertNotNull(actualEmail, "Email is null in response");
        Assert.assertTrue(actualEmail.contains(expectedEmail),
                "Expected email: " + expectedEmail + " but got: " + actualEmail);
    }

    @And("the response name should contain {string}")
    public void validateName(String expectedName) {
        String actualName = response.jsonPath().getString("user.name");
        Assert.assertNotNull(actualName, "Name is null in response");
        Assert.assertTrue(actualName.contains(expectedName),
                "Expected name: " + expectedName + " but got: " + actualName);
    }

    @And("the response time should be within {int} ms")
    public void validateResponseTime(int maxTime) {
        long time = response.getTime();
        Assert.assertTrue(time <= maxTime, "Response time exceeded: " + time + " ms");
    }

    @When("I login with following details")
    public void loginWithDetails(io.cucumber.datatable.DataTable dataTable) {
        AuthRequest request = new AuthRequest();
        Map<String, String> data = dataTable.asMaps().get(0);
        request.setEmail(data.getOrDefault("email", ""));
        request.setPassword(data.getOrDefault("password", ""));
        response = RestUtility.post(FileUtility.get("endpoint.login"), request);
    }

    @When("I login with following details without API key")
    public void loginWithoutApiKey(io.cucumber.datatable.DataTable dataTable) {
        AuthRequest request = new AuthRequest();
        request.setEmail(dataTable.asMaps().get(0).get("email"));
        request.setPassword(dataTable.asMaps().get(0).get("password"));
        response = RestUtility.postNoAuth(FileUtility.get("endpoint.login"), request);
    }

    @And("the response should contain JWT token")
    public void validateJwtToken() {
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token is null");
        Assert.assertFalse(token.trim().isEmpty(), "Token is empty");
    }

    @And("the response Content-Type should contain {string}")
    public void validateContentType(String contentType) {
        String actual = response.getContentType();
        Assert.assertTrue(actual.contains(contentType),
                "Expected Content-Type to contain: " + contentType + " but got: " + actual);
    }

    @And("the response should contain user email")
    public void validateUserEmail(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        String expectedEmail = data.get(0).get("email");
        String actualEmail = response.jsonPath().getString("user.email");
        Assert.assertEquals(actualEmail, expectedEmail);
    }
    
}