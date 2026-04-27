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

    Response res;
    RequestSpecification request;
    String Name;
    
    private String objectsUrl() {
        return FileUtility.get("base.url") + FileUtility.get("endpoint.objects");
    }

    private String objectByIdUrl() {
        return FileUtility.get("base.url") + FileUtility.get("endpoint.object.by.id");
    }
    //Author Barath
    @Given("object with id {int} exists")
    public void getUsingValidId(int id1) {
        request = RestAssured.given().pathParam("id", id1);
    }

    @Given("object with id {int} doesn't exists")
    public void getUsingInvalidId(int id1) {
        request = RestAssured.given().pathParam("id", id1);
    }

    @When("user sends GET to endpoint for single object")
    public void getSingleObject() {
        res = request.when().get(objectByIdUrl());
    }
    
    @And("the response should have id {int}")
    public void validateId(int value) {
        res.then().body("id", equalTo(String.valueOf(value)));
    }
    
    @When("DELETE is sent to object endpoint from config")
    public void deleteObjectFromConfig() {
        String objectId = FileUtility.get("object.single.id");
        res = RestAssured.given()
                .when()
                .delete(objectsUrl() + "/" + objectId);
    }
    
    @Then("the appropriate message {string} is present in response body")
    public void verifyMessage(String expectedMessage) {
        res.then().assertThat().body(containsString(expectedMessage));
    }
    
    @When("DELETE is sent to objects endpoint with invalid IDs")
    public void deleteWithInvalidIds(DataTable table) {
        List<String> ids = table.asList();
        for (String id : ids) {
            res = RestAssured.given()
                    .pathParam("id", id)
                    .when()
                    .delete(objectByIdUrl());
        }
    }
    
    @When("DELETE is sent to endpoint for reserved object")
    public void deleteReservedObject() {
        String reservedId = FileUtility.get("object.reserved.id");
        res = RestAssured.given()
                .when()
                .delete(objectsUrl() + "/" + reservedId);
    }
    }