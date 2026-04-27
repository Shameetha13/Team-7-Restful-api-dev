package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;
import io.cucumber.datatable.DataTable;
import utils.FileUtility;
import utils.RestUtility;
import java.util.List;
import java.util.Map;

public class ObjectSteps {

//Author Kamala Kannan
    @When("user sends GET to endpoint")
    public void getAllObjects() {
        res = RestUtility.getNoAuth(objectsUrl());
    }
    
    @Then("the status code should be {int}")
    public void checkResponseCode(int code) {
        res.then().log().all().statusCode(code);
    }
    
    @And("each object contains the field {string}")
    public void fieldPresence(String field) {
        res.then().body("$", everyItem(hasKey(field)));
    }
    
    @And("the response time is below {int} ms")
    public void verifyResponseTime(int time) {
        res.then().assertThat().time(lessThan((long) time));
    }

    @And("the response header {string} should be {string}")
    public void verifyHeader(String headerName, String headerValue) {
        res.then().header(headerName, containsString(headerValue));
    }
    
    @Given("object with id {int} and {int} exists")
    public void setQueryParameter(int id1, int id2) {
        request = RestAssured.given()
                .queryParam("id", id1)
                .queryParam("id", id2);
    }
    
    @When("user sends GET to endpoint with query params")
    public void getObjectsWithQueryParams() {
        res = request.when().get(objectsUrl());
    }
    
    @Given("object with id {string} doesn't exists")
    public void setParamString(String oId) {
        request = RestAssured.given().queryParam("id", oId);
    }
    
    @Given("the {string} of the request body is set to {string}")
    public void setHeader(String headerName, String type) {
        request = RestAssured.given().header(headerName, type);
    }

    @When("I send a PATCH request to update the price for an object:")
    public void patchObjectFromConfig(DataTable dataTable) {
        String objectId = FileUtility.get("object.patch.id");
        String price = dataTable.asMaps().get(0).get("newPrice");
        String patchBody = "{\n"
                + "  \"data\": {\n"
                + "    \"price\": \"" + price + "\"\n"
                + "  }\n"
                + "}";
        res = request.given()
                .body(patchBody)
                .when()
                .patch(objectsUrl() + "/" + objectId);
    }
    
    @And("the {string} in the response should match {string}")
    public void verifyNestedAttribute(String path, String expectedValue) {
        float expectedPrice = Integer.parseInt(expectedValue);
        res.then().body(path, equalTo(expectedPrice));
    }
    
    @And("the appropriate error message {string} is present in response body")
    public void verifyErrorMessage(String expectedMessage) {
        res.then().assertThat().body(containsString(expectedMessage));
    }
    
    @When("I send a PATCH request to endpoint with invalid object id {string} and price {string}")
    public void patchObjectWithExplicitId(String objectId, String price) {
        String patchBody = "{\n"
                + "  \"data\": {\n"
                + "    \"price\": \"" + price + "\"\n"
                + "  }\n"
                + "}";
        res = request.given()
                .body(patchBody)
                .when()
                .patch(objectsUrl() + "/" + objectId);
    }
}