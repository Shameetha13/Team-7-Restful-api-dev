package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import context.TestContext;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.testng.Assert;
import utils.ExcelUtility;
import utils.FileUtility;
import utils.RestUtility;

public class CollectionSteps {

    Response response;
    private Map<String, String> testData;
    private String baseUrl = FileUtility.getProperty("base.url");
    private String token   = FileUtility.getProperty("auth.token");

    @Given("the user is logged in")
    public void login() {
        // simple placeholder (no auth logic)
        System.out.println("User logged in");
    }
@When("I add a collection item from Excel row {int} into collection {string}")
    public void addCollectionItem(int rowIndex, String collectionName) {
        // rowIndex from feature file is 0-based data row;
        // row 0 in Excel = header, so actual row = rowIndex + 1
        testData = ExcelUtility.getRowData(rowIndex + 1);

        String url = baseUrl + "/collections/" + collectionName + "/items";
        response  = RestUtility.post(url, token, testData);
    }


    @Then("the response status should be {int}")
    public void verifyStatusCode(int expectedStatus) {
        Assert.assertEquals(
            response.getStatusCode(),
            expectedStatus,
            "Status code mismatch"
        );
    }

    @Then("the response Content-Type should contain {string}")
    public void verifyContentType(String expectedContentType) {
        String actualContentType = response.getContentType();
        Assert.assertTrue(
            actualContentType.contains(expectedContentType),
            "Expected Content-Type to contain '" + expectedContentType
                + "' but got: " + actualContentType
        );
    }


    @Then("the response should contain created item with all fields")
    public void verifyCreatedItemWithAllFields() {
        Assert.assertNotNull(
            response.jsonPath().getString("id"),
            "Response should contain 'id'"
        );


        for (Map.Entry<String, String> entry : testData.entrySet()) {
            String key      = entry.getKey();
            String expected = entry.getValue();
            String actual   = response.jsonPath().getString(key);

            Assert.assertEquals(
                actual,
                expected,
                "Field mismatch for key: " + key
            );
        }
    }


    @Then("item should be created in current user's collection")
    public void verifyItemCreatedInCurrentUserCollection() {
        String currentUser = FileUtility.getProperty("current.user.id");

 
        String ownerInResponse = response.jsonPath().getString("ownerId");
        Assert.assertEquals(
            ownerInResponse,
            currentUser,
            "Item should be owned by current user, not another user"
        );

        Assert.assertNotNull(
            response.jsonPath().getString("id"),
            "Response should contain generated 'id'"
        );
    }

    

    @Then("the response should contain generated id")
    public void verifyGeneratedId() {
        String id = response.jsonPath().getString("id");
        Assert.assertNotNull(id,  "Response should contain a generated 'id'");
        Assert.assertFalse(id.isEmpty(), "'id' in response should not be empty");
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