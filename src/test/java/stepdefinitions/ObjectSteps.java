package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class ObjectSteps {

    Response response;

    @Given("the base API is configured")
    public void setup() {
        baseURI = "https://api.restful-api.dev";
    }

    @When("user sends GET to {string}")
    public void sendGet(String endpoint) {
        response = given().when().get(endpoint);
    }

    @Then("the status code should be {int}")
    public void verifyStatus(int code) {
        response.then().statusCode(code);
    }

    @Then("Response status line contains {string}")
    public void verifyStatusLine(String text) {
        System.out.println(response.getStatusLine());
    }

    @Then("the response body should contain field {string}")
    public void verifyField(String field) {
        System.out.println(response.getBody().asString());
    }

    // DELETE object
    @When("user deletes the object {string}")
    public void deleteObject(String id) {
        response = given().when().delete("/objects/" + id);
    }

    // Multiple DELETE (DataTable simplified)
    @When("user deletes already deleted objects")
    public void deleteMultiple(io.cucumber.datatable.DataTable dataTable) {
        for (String id : dataTable.asList()) {
            response = given().when().delete("/objects/" + id);
            System.out.println(response.getStatusCode());
        }
    }

    @When("user sends DELETE to {string}")
    public void deleteInvalid(String endpoint) {
        response = given().when().delete(endpoint);
    }

    @When("user sends GET requests to {string} with invalid IDs")
    public void multipleInvalidGet(String endpoint, io.cucumber.datatable.DataTable dataTable) {
        for (String id : dataTable.asList()) {
            response = given().when().get(endpoint + "/" + id);
            System.out.println(response.getStatusCode());
        }
    }
}