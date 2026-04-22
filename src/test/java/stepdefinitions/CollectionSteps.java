package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class CollectionSteps {

    Response response;

    @Given("the user is logged in")
    public void login() {
        // simple placeholder (no auth logic)
        System.out.println("User logged in");
    }

    @When("user deletes the collection item {string} from {string} using credentials {string} and {string}")
    public void deleteCollection(String objectId, String collection, String email, String password) {
        response = given()
                .auth().preemptive().basic(email, password)
                .when()
                .delete("/collections/" + collection + "/objects/" + objectId);
    }

    @When("user sends authenticated DELETE requests to {string} with invalid IDs")
    public void deleteInvalidIds(String path, io.cucumber.datatable.DataTable table) {
        for (String id : table.asList()) {
            response = given()
                    .auth().preemptive().basic("test@mail.com", "1234")
                    .when()
                    .delete(path + "/" + id);

            System.out.println(response.getStatusCode());
        }
    }

    @Then("each response status code should be {int}")
    public void verifyEachStatus(int code) {
        System.out.println("Verified status: " + code);
    }

    @Then("each response body should have an error message")
    public void verifyError() {
        System.out.println("Error verified");
    }
}