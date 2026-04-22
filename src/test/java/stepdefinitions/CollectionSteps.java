package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;

public class CollectionSteps {

    private RequestSpecification request;
    private Response response;
    private String requestedId;


    @Given("The user is already registered and their API key is valid")
    public void setValidApiKey() {
        request = RestAssured.given()
                .header("Authorization", "Bearer valid_api_key_123");
    }
    
    @When("GET request is sent for collection {string} and object ID {string}")
    public void sendGetRequest(String collection, String objectId) {
        requestedId = objectId;
        response = request.when()
                .get("/" + collection + "/" + objectId);
    }

    @Then("the response status code should be {int}")
	public void checkResponseCode(int code) {
		response.then().statusCode(code);
	}

    @Then("the value of {string} field in response should match with that in request")
    public void verifyIdMatch(String fieldName) {
        response.then()
                .body(fieldName, equalTo(requestedId));
    }

    @Given("The API key is invalid")
    public void setInvalidApiKey() {
        request = RestAssured.given()
                .header("Authorization", "Bearer invalid_key_abc");
    }

    @Then("the error message in response body should contain {string}")
    public void verifyErrorMessage(String expectedMessage) {
        response.then()
            .assertThat()
            .body("error", containsString(expectedMessage));
    }

    @Given("The API key is valid")
    public void setGenericValidKey() {
        setValidApiKey(); 
    }
}
