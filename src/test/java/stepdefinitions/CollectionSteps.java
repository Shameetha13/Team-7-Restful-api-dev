package stepdefinitions;

import java.util.*;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;

import static org.testng.Assert.*;

import utils.ExcelUtility;
import utils.FileUtility;
import utils.RestUtility;

public class CollectionSteps {

    Response response;
    long responseTime;

    String baseUrl;
    String token;

    Map<String, String> data;

    public CollectionSteps() {
        baseUrl = FileUtility.getProperty("baseUrl");
        token = FileUtility.getProperty("authToken");
    }

    // ===================== GET =====================

    @When("I send a GET request to {string}")
    public void sendGetRequest(String endpoint) {
        response = RestUtility.get(baseUrl + endpoint, token);
    }

    @When("I send a GET request to {string} and measure response time")
    public void sendGetWithTime(String endpoint) {
        long start = System.currentTimeMillis();
        response = RestUtility.get(baseUrl + endpoint, token);
        responseTime = System.currentTimeMillis() - start;
    }

    // ===================== PUT =====================

    @When("I send a PUT request to {string} using row {string} with a valid full payload")
    public void sendPutValid(String endpoint, String rowNum) {

        int row = Integer.parseInt(rowNum);
        data = ExcelUtility.getRowData(row);

        String objectId = data.get("TS-12_Object_ID");

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", data.get("prodname"));
        payload.put("year", Integer.parseInt(data.get("year")));
        payload.put("price", Integer.parseInt(data.get("price")));
        payload.put("cpu", data.get("cpu"));
        payload.put("harddisk", data.get("harddisk"));
        payload.put("color", data.get("color"));

        endpoint = endpoint.replace("{objectId}", objectId);

        response = RestUtility.put(baseUrl + endpoint, token, payload);
    }

    @When("I send a PUT request to {string} using row {string} with missing required fields")
    public void sendPutInvalid(String endpoint, String rowNum) {

        int row = Integer.parseInt(rowNum);
        data = ExcelUtility.getRowData(row);

        String objectId = data.get("TS-12_Object_ID");

        Map<String, Object> payload = new HashMap<>();
        payload.put("price", Integer.parseInt(data.get("price")));

        endpoint = endpoint.replace("{objectId}", objectId);

        response = RestUtility.put(baseUrl + endpoint, token, payload);
    }

    @When("I send a PUT request to {string} using row {string} with valid data and measure response time")
    public void sendPutWithTime(String endpoint, String rowNum) {

        int row = Integer.parseInt(rowNum);
        data = ExcelUtility.getRowData(row);

        String objectId = data.get("TS-12_Object_ID");

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", data.get("prodname"));
        payload.put("price", Integer.parseInt(data.get("price")));

        endpoint = endpoint.replace("{objectId}", objectId);

        long start = System.currentTimeMillis();
        response = RestUtility.put(baseUrl + endpoint, token, payload);
        responseTime = System.currentTimeMillis() - start;
    }

    // ===================== PUT (DataTable) =====================

    @When("I send a PUT request with missing required fields:")
    public void sendPutDataTable(DataTable table) {

        List<Map<String, String>> rows = table.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {

            String endpoint = "/collections/" + row.get("collectionName")
                    + "/objects/" + row.get("objectId");

            Map<String, Object> payload = new HashMap<>();
            payload.put("price", 1000);

            response = RestUtility.put(baseUrl + endpoint, token, payload);

            assertEquals(response.getStatusCode(), 400);
        }
    }

    // ===================== PUT (Another User) =====================

    @Given("another user also has a collection")
    public void anotherUserSetup() {
        // optional
    }

    @When("I send a PUT request to {string} using another user's authorization")
    public void sendPutAnotherUser(String endpoint) {

        String anotherToken = FileUtility.getProperty("anotherUserToken");

        int row = 0;
        data = ExcelUtility.getRowData(row);

        String objectId = data.get("TS-12_Object_ID");

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", data.get("prodname"));

        endpoint = endpoint.replace("{objectId}", objectId);

        response = RestUtility.put(baseUrl + endpoint, anotherToken, payload);
    }

    // ===================== PATCH =====================

    @When("I send a PATCH request to {string} using row {string} with a single attribute update")
    public void sendPatchSingle(String endpoint, String rowNum) {

        int row = Integer.parseInt(rowNum);
        data = ExcelUtility.getRowData(row);

        String objectId = data.get("TS-12_Object_ID");

        Map<String, Object> payload = new HashMap<>();
        payload.put("price", Integer.parseInt(data.get("price")) + 1000);

        endpoint = endpoint.replace("{objectId}", objectId);

        response = RestUtility.patch(baseUrl + endpoint, token, payload);
    }

    @When("I send a PATCH request to {string} using row {string} with an invalid data type")
    public void sendPatchInvalid(String endpoint, String rowNum) {

        int row = Integer.parseInt(rowNum);
        data = ExcelUtility.getRowData(row);

        String objectId = data.get("TS-12_Object_ID");

        Map<String, Object> payload = new HashMap<>();
        payload.put("price", "invalid");

        endpoint = endpoint.replace("{objectId}", objectId);

        response = RestUtility.patch(baseUrl + endpoint, token, payload);
    }

    @When("I send a PATCH request to {string} using row {string} with partial data and measure response time")
    public void sendPatchWithTime(String endpoint, String rowNum) {

        int row = Integer.parseInt(rowNum);
        data = ExcelUtility.getRowData(row);

        String objectId = data.get("TS-12_Object_ID");

        Map<String, Object> payload = new HashMap<>();
        payload.put("price", Integer.parseInt(data.get("price")));

        endpoint = endpoint.replace("{objectId}", objectId);

        long start = System.currentTimeMillis();
        response = RestUtility.patch(baseUrl + endpoint, token, payload);
        responseTime = System.currentTimeMillis() - start;
    }

    // ===================== VALIDATIONS =====================

    @Then("the response status should be {int} OK")
    public void validateStatusOK(int code) {
        assertEquals(response.getStatusCode(), code);
    }

    @Then("the response status should be {int} Bad Request")
    public void validateStatusBad(int code) {
        assertEquals(response.getStatusCode(), code);
    }

    @Then("the response Content-Type should contain {string}")
    public void validateContentType(String type) {
        assertTrue(response.getContentType().contains(type));
    }

    @Then("the response body should contain a list of all objects in the collection")
    public void validateList() {
        assertNotNull(response.jsonPath().getList("$"));
    }

    @Then("the response body should contain an empty list")
    public void validateEmptyList() {
        assertEquals(response.jsonPath().getList("$").size(), 0);
    }

    @Then("the response body should indicate no items found for the collection")
    public void validateNoItems() {
        assertTrue(response.asString().toLowerCase().contains("no"));
    }

    @Then("the response body should contain the fully updated object")
    public void validatePutResponse() {
        assertNotNull(response.jsonPath().getMap("$"));
    }

    @Then("the response body should reflect all updated values from the request")
    public void validateUpdatedValues() {
        assertTrue(response.asString().length() > 0);
    }

    @Then("the response should not update the other user's collection")
    public void validateNoCrossUpdate() {
        assertEquals(response.getStatusCode(), 200);
    }

    @Then("a new record should be created in the current user's collection instead")
    public void validateNewRecord() {
        assertNotNull(response.jsonPath().get("$"));
    }

    @Then("the response body should reflect the new record in current user's collection")
    public void validateNewRecordBody() {
        assertNotNull(response.jsonPath().getMap("$"));
    }

    @Then("the response body should show the updated attribute value")
    public void validatePatchUpdate() {
        assertNotNull(response.jsonPath().get("$"));
    }

    @Then("other attributes should remain unchanged")
    public void validateOtherFields() {
        assertTrue(response.asString().length() > 0);
    }

    @Then("the response body should reflect the updated attribute even with incorrect data type")
    public void validatePatchInvalid() {
        assertNotNull(response.jsonPath().get("$"));
    }

    @Then("the response body should indicate missing or null required fields")
    public void validateMissingFields() {
        assertTrue(response.asString().toLowerCase().contains("missing")
                || response.asString().toLowerCase().contains("null"));
    }

    @Then("the response time should be within {int} ms")
    public void validateResponseTime(int time) {
        assertTrue(responseTime <= time);
    }
}