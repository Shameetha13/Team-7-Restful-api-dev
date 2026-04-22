package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.*;

public class ObjectSteps {

    RequestSpecification request;
    Response response;

    String baseUrl;
    String endpoint;
    String apiKey;

    static Map<String, String> collectionVars = new HashMap<>();

    // =========================
    // BACKGROUND
    // =========================
    @Given("the base API is configured")
    public void setupBaseAPI() {
        request = RestAssured.given();
        baseUrl = ConfigReader.get("BASE_URL");
    }

    // =========================
    // API KEY (Scenario Outline + Excel DDT)
    // =========================
    @Given("the API key is {string}")
    public void setApiKey(String key) {

        apiKey = resolveApiKey(key);

        if (apiKey != null && !apiKey.isEmpty()) {
            request.header("x-api-key", apiKey);
        }
    }

    // =========================
    // BASE + ENDPOINT
    // =========================
    @Given("the base URL is {string} and endpoint is {string}")
    public void setBaseAndEndpoint(String base, String end) {

        baseUrl = resolveValue(base, "BASE_URL");
        endpoint = resolveValue(end, "ENDPOINT_1");
    }

    // =========================
    // GET REQUESTS
    // =========================
    @When("user sends GET to {string}/{string}")
    public void sendGET(String base, String end) {

        baseUrl = resolveValue(base, "BASE_URL");
        endpoint = resolveValue(end, "ENDPOINT_1");

        response = request.when().get(baseUrl + endpoint);
    }

    @When("user sends GET to {string}/{string} with param {string}")
    public void sendGETWithParams(String base, String end, String param) {

        baseUrl = resolveValue(base, "BASE_URL");
        endpoint = resolveValue(end, "ENDPOINT_1");

        param = resolveCollectionVars(param);

        String[] params = param.split("&");

        for (String p : params) {
            String[] kv = p.split("=");
            request.queryParam(kv[0], kv[1]);
        }

        response = request.when().get(baseUrl + endpoint);
    }

    @When("user sends GET to {string}/{string} without auth header")
    public void sendGETWithoutAuth(String base, String end) {

        baseUrl = resolveValue(base, "BASE_URL");
        endpoint = resolveValue(end, "ENDPOINT_1");

        request = RestAssured.given(); // reset (no header)

        response = request.when().get(baseUrl + endpoint);
    }

    // =========================
    // PATCH
    // =========================
    @Given("a temporary object is created for testing")
    public void createTempObject() {

        Response res = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"data\":{\"name\":\"temp\",\"price\":100}}")
                .post(baseUrl + "/objects");

        String id = res.jsonPath().getString("id");
        collectionVars.put("tempId", id);
    }

    @When("user sends PATCH to the test object with body:")
    public void patchTestObject(String body) {

        String id = collectionVars.get("tempId");

        response = request
                .header("Content-Type", "application/json")
                .body(body)
                .patch(baseUrl + "/objects/" + id);
    }

    @When("user sends PATCH to {string} with body:")
    public void sendPATCH(String path, String body) {

        path = resolveCollectionVars(path);

        response = request
                .header("Content-Type", "application/json")
                .body(body)
                .patch(baseUrl + path);
    }

    // =========================
    // VALIDATIONS (TestNG)
    // =========================
    @Then("the status code should be {int}")
    public void validateStatus(int code) {
        Assert.assertEquals(response.getStatusCode(), code, "Status code mismatch");
    }

    @Then("the response body should be a JSON array")
    public void validateJSONArray() {
        Assert.assertTrue(response.getBody().asString().trim().startsWith("["),
                "Response is not a JSON array");
    }

    @Then("the response body should be an empty JSON array")
    public void validateEmptyArray() {
        Assert.assertEquals(response.getBody().asString().trim(), "[]",
                "Response is not empty array");
    }

    @Then("the JSON array should only contain ids {string} and {string}")
    public void validateIds(String id1, String id2) {

        List<Integer> ids = response.jsonPath().getList("id");

        Assert.assertTrue(ids.contains(Integer.parseInt(id1)), "Missing id1");
        Assert.assertTrue(ids.contains(Integer.parseInt(id2)), "Missing id2");
    }

    @Then("the response schema should validate {string} as string and {string} as string")
    public void validateSchema(String f1, String f2) {

        List<Map<String, Object>> list = response.jsonPath().getList("");

        for (Map<String, Object> obj : list) {
            Assert.assertTrue(obj.get(f1) instanceof String, f1 + " not string");
            Assert.assertTrue(obj.get(f2) instanceof String, f2 + " not string");
        }
    }

    @Then("the collection variable {string} is set to the first object's id")
    public void storeFirstId(String varName) {

        String id = response.jsonPath().getString("[0].id");
        collectionVars.put(varName, id);
    }

    @Then("the collection variable {string} is set to last object's id plus 1")
    public void storeNonExistId(String varName) {

        List<Integer> ids = response.jsonPath().getList("id");
        int last = ids.get(ids.size() - 1);

        collectionVars.put(varName, String.valueOf(last + 1));
    }

    @Then("the response time should be below {int} ms")
    public void validateTime(int time) {
        Assert.assertTrue(response.getTime() < time,
                "Response time exceeded: " + response.getTime());
    }

    @Then("the response body should have an error message")
    public void validateError() {
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("error") || body.contains("message"),
                "No error message found");
    }

    @Then("the response body should contain field {string}")
    public void validateField(String field) {
        Assert.assertNotNull(response.jsonPath().get(field),
                field + " not present");
    }

    @Then("the response body should match the PATCH response schema")
    public void validatePatchSchema() {

        Assert.assertNotNull(response.jsonPath().get("id"));
        Assert.assertNotNull(response.jsonPath().get("name"));
        Assert.assertNotNull(response.jsonPath().get("data"));
    }

    // =========================
    // HELPERS
    // =========================
    private String resolveValue(String value, String key) {
        if (value.startsWith("<")) {
            return ConfigReader.get(key);
        }
        return value;
    }

    private String resolveApiKey(String key) {

        if (key.equals("<API_Key>")) {
            return ExcelReader.getData("Sheet1", "API_KEY");
        }

        return key;
    }

    private String resolveCollectionVars(String input) {

        for (String key : collectionVars.keySet()) {
            input = input.replace("<" + key + ">", collectionVars.get(key));
        }
        return input;
    }
}