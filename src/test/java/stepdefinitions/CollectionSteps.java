package stepdefinitions;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given; 
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import utils.FileUtility;

import java.util.Map;

import org.testng.Assert;

public class CollectionSteps {

    RequestSpecification request;
    private RequestSpecification request;
    private Response response;
    private String requestedId;


    @Given("The user is already registered and their API key is valid")
    public void setValidApiKey() {
        request = RestAssured.given()
                .header("Authorization", "Bearer valid_api_key_123");
    String baseUrl;
    String endpoint;
    String apiKey;

    // =========================
    // Background
    // =========================
    @Given("the base API is configured")
    public void setupBaseAPI() {
        request = RestAssured.given();
        baseUrl = FileUtility.getProperty("BASE_URL");
    }

    // =========================
    // SCENARIO OUTLINE + DDT (Excel)
    // =========================
    @Given("the API key is {string}")
    public void setApiKey(String key) {

        switch (key) {

            case "<API_KEY>":
                apiKey = ExcelReader.getData("Sheet1", "API_KEY");
                break;

            case "<no_col_API>":
                apiKey = ExcelReader.getData("Sheet1", "NO_COL_API");
                break;

            case "<invalid_API>":
                apiKey = "invalid_key_123";
                break;

            default:
                apiKey = key;
        }

        request.header("x-api-key", apiKey);
    }

    @Given("no API key header is sent")
    public void noApiKey() {
        // no header added
    }

    // =========================
    // DATATABLE SUPPORT
    // =========================
    @Given("the request details:")
    public void setRequestDetails(DataTable table) {

        Map<String, String> data = table.asMaps().get(0);

        String base = data.get("base_url");
        String end = data.get("endpoint");
        String key = data.get("api_key");

        baseUrl = resolveValue(base, "BASE_URL");
        endpoint = resolveValue(end, "ENDPOINT_2");
        apiKey = resolveApiKey(key);

        request.header("x-api-key", apiKey);
    }

    // =========================
    // SEND REQUEST
    // =========================
    @When("user sends GET to {string}/{string}")
    public void sendGET(String base, String end) {

        baseUrl = resolveValue(base, "BASE_URL");
        endpoint = resolveValue(end, "ENDPOINT_2");

        response = request
                .when()
                .get(baseUrl + endpoint);
    }

    @When("user sends GET request")
    public void sendGETWithoutParams() {
        response = request.when().get(baseUrl + endpoint);
    }

    // =========================
    // VALIDATIONS
    // =========================
    @Then("the status code should be {int}")
    public void validateStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Status code mismatch");
    }

    @Then("the response header {string} should be present")
    public void validateHeader(String headerName) {
        Assert.assertNotNull(response.getHeader(headerName), "Header missing: " + headerName);
    }

    @Then("the response time should be below {int} ms")
    public void validateResponseTime(int time) {
        Assert.assertTrue(response.getTime() < time,
                "Response time exceeded: " + response.getTime());
    }

    @Then("the response status text should be {string}")
    public void validateStatusText(String expected) {
        String actual = response.getStatusLine().split(" ", 3)[2];
        Assert.assertEquals(actual, expected, "Status text mismatch");
    }

    @Then("the response body should have an error message")
    public void validateErrorMessage() {
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("error") || body.contains("message"),
                "No error message found");
    }

    @Then("the response body should be an empty JSON array")
    public void validateEmptyJSONArray() {
        int size = response.jsonPath().getList("$").size();
        Assert.assertEquals(size, 0, "Array is not empty");
    }

    @Then("the response body should match the collections schema with {string} and {string}")
    public void validateSchema(String field1, String field2) {

        Assert.assertNotNull(response.jsonPath().get(field1),
                field1 + " not present");

        Assert.assertNotNull(response.jsonPath().get(field2),
                field2 + " not present");
    }

    // =========================
    // COMMON RESOLVERS
    // =========================
    private String resolveValue(String value, String configKey) {

        if (value.startsWith("<") && value.endsWith(">")) {
            return FileUtility.getProperty(configKey);
        }
        return value;
    }

    private String resolveApiKey(String key) {

        switch (key) {
            case "<API_KEY>":
                return ExcelReader.getData("Sheet1", "API_KEY");

            case "<no_col_API>":
                return ExcelReader.getData("Sheet1", "NO_COL_API");

            case "<invalid_API>":
                return "invalid_key_123";

            default:
                return key;
        }
    } 

    // =========================
    // AUTH / DELETE APIs
    // =========================
    @Given("the user is logged in")
    public void login() {
        System.out.println("User logged in");
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
    @When("user sends authenticated DELETE requests to {string} with invalid IDs")
    public void deleteInvalidIds(String path, DataTable table) {
        for (String id : table.asList()) {
            response = given()
                    .auth().preemptive().basic("test@mail.com", "1234")
                    .when()
                    .delete(path + "/" + id);

            System.out.println(response.getStatusCode());
        }
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
