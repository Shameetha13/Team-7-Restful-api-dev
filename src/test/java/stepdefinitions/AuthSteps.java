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
import utils.JavaUtility;
import utils.RestUtility;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

// Author Shameetha Ravikumar
public class AuthSteps {
    private static String lastRegisteredEmail;
    private static String loginEmail;
    private Response response;

    @Given("the API URL is accessible with a valid API key")
    public void setup() {
       
        String apiKey = FileUtility.get("api.key");
        Assert.assertNotNull(apiKey, "API key must not be null in config.properties");
        Assert.assertFalse(apiKey.isEmpty(), "API key must not be empty in config.properties");
    }

    @When("I send a POST request with email {string}, password {string} and name {string}")
    public void sendPostRequest(String email, String password, String name) {

    if (email.equals("<random>")) {
        email = JavaUtility.getRandomEmail();
        lastRegisteredEmail = email; 
    } 
    
    else if (email.equals("<duplicate>")) {
        email = lastRegisteredEmail;
    }

    AuthRequest requestBody = new AuthRequest(email, password, name);
    response = RestUtility.post(FileUtility.get("endpoint.register"), requestBody);
    }

    @Then("validate the response status code {int}")
    public void validateStatusCode(int statusCode) {
        response.then().log().all().statusCode(statusCode);
    }

    @And("the status message should contain {string}")
    public void validateStatusMessage(String expectedStatus) {
        String statusLine = response.getStatusLine();
        Assert.assertNotNull(statusLine);
        Assert.assertTrue(statusLine.contains(expectedStatus));
    }

   @And("the response should contain {string}")
    public void validateEmail(String expectedEmail) {
    String actualEmail = response.jsonPath().getString("user.email");
    Assert.assertNotNull(actualEmail);
    String emailToVerify = expectedEmail.equals("<random>") ? lastRegisteredEmail : expectedEmail;
    Assert.assertEquals(actualEmail,emailToVerify);
    }

    @And("the response name should contain {string}")
    public void validateName(String expectedName) {
        String actualName = response.jsonPath().getString("user.name");
        Assert.assertNotNull(actualName);
        Assert.assertEquals(actualName,expectedName);
    }

    @And("the response time should be within {int} ms")
    public void validateResponseTime(int maxTime) {
    assertThat(response.getTime(), lessThan((long) maxTime));
    }

    @When("I login with following details")
    public void loginWithDetails(io.cucumber.datatable.DataTable dataTable) {
    Map<String, String> data = dataTable.asMaps().get(0);
    
   
    String email = data.get("email"); 
    String password = data.getOrDefault("password", "");
    
    if (email != null && email.equals("<random>")) {
        loginEmail = JavaUtility.getRandomEmail();
        String name = data.getOrDefault("name", JavaUtility.getRandomName());
        
        AuthRequest registerRequest = new AuthRequest(loginEmail, password, name);
        RestUtility.post(FileUtility.get("endpoint.register"), registerRequest);
        email = loginEmail; 
    } else {
        loginEmail = email; 
    }
    
    AuthRequest loginRequest = new AuthRequest();
    loginRequest.setEmail(loginEmail);
    loginRequest.setPassword(password);
    
    response = RestUtility.post(FileUtility.get("endpoint.login"), loginRequest);
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
        Assert.assertNotNull(token);
        Assert.assertFalse(token.trim().isEmpty(), "Token is empty");
    }

    @And("the response Content-Type should contain {string}")
    public void validateContentType(String contentType) {
        String actual = response.getContentType();
        Assert.assertTrue(actual.contains(contentType));
    }

    @And("the response should contain user email")
    public void validateUserEmail(io.cucumber.datatable.DataTable dataTable) {
    Map<String, String> data = dataTable.asMaps().get(0);
    String emailFromTable = data.get("email");
    String actualEmail = response.jsonPath().getString("user.email");
    String emailToVerify;
        if (emailFromTable.equals("<verifyEmail>")) {
            emailToVerify = loginEmail; 
        } else {
            emailToVerify = emailFromTable;
        }
    Assert.assertNotNull(actualEmail);
    Assert.assertEquals(actualEmail, emailToVerify);
    }
}