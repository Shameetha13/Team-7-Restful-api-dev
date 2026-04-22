package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;
import io.cucumber.datatable.DataTable;

import static io.restassured.RestAssured.given; 
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import java.util.*;

public class ObjectSteps {
    Response res;
	RequestSpecification request;
    String name;
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
    // API KEY
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

        request = RestAssured.given(); // reset

        response = request.when().get(baseUrl + endpoint);
    }
    @When("user sends POST to /objects with name as {string} year as {int} price as {double} cpu model as {string} and disk size as {string}")
	public void createObject(String name, int year, double price, String model, String size) {
		String data = "{\r\n" + //
                        "  \"name\": \"" + name +"\",\r\n" + //
                        "  \"data\": {\r\n" + //
                        "    \"year\": " + year + ",\r\n" + //
                        "    \"price\": " + price + ",\r\n" + //
                        "    \"CPU model\": \"" + model + "\",\r\n" + //
                        "    \"Hard disk size\": \"" + size +"\"\r\n" + //
                        "  }\r\n" + //
                        "}";
		res = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(data)
				.post("/objects");
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
    // VALIDATIONS
    // =========================
    @Then("the status code should be {int}")
	public void checkResponseCode(int code) {
		res.then().statusCode(code);
	}

    @And("a unique object id should be generated")
	public void verifyIdExists() {
		res.then().body("$", hasKey("id"));
	}
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

    @When("user sends POST to /objects with name as {string}")
	public void createObjectMalformed(String name) {
		String data = "{\r\n" + //
                        "  \"name\": \"" + name +"\",\r\n" + //
                        "  \"data\": "+null+"\r\n" + //
                        "}";
		res = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(data)
				.post("/objects");
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

    @Then("the error message should contain {string}")
    public void verifyErrorMessage(String expectedMessage) {
        res.then()
            .assertThat()
            .body("error", containsString(expectedMessage));
    }

    @Given("content-type is set to text in header")
    public void setHeader() {
        request = RestAssured.given()
                .header("Content-Type", "text/plain");
    }

    @When("user sends POST to {string} with complete valid json body")
    public void sendPostRequest(String endpoint) {
        String Body = "{\r\n" + //
                        "  \"name\": \"Apple MacBook Pro 16\",\r\n" + //
                        "  \"data\": {\r\n" + //
                        "    \"year\": 2019,\r\n" + //
                        "    \"price\": 1849.99,\r\n" + //
                        "    \"CPU model\": \"Intel Core i9\",\r\n" + //
                        "    \"Hard disk size\": \"1 TB\"\r\n" + //
                        "  }\r\n" + //
                        "}";
        res = request.body(Body)
                .when()
                .post(endpoint);
    }

    @When("user sends PUT to /objects with valid object id")
	public void updateObject(DataTable data) {
		List<Map<String, String>> table = data.asMaps(String.class, String.class);
		
		for(Map<String,String> row : table) {
			String name = row.get("name");
			int year = Integer.parseInt(row.get("year"));
            Double price = Double.parseDouble(row.get("price"));
            String model = row.get("CPU model");
            String size = row.get("Hard disk size");
			
			String payload = "{\r\n" + //
                        "  \"name\": \"" + name +"\",\r\n" + //
                        "  \"data\": {\r\n" + //
                        "    \"year\": " + year + ",\r\n" + //
                        "    \"price\": " + price + ",\r\n" + //
                        "    \"CPU model\": \"" + model + "\",\r\n" + //
                        "    \"Hard disk size\": \"" + size +"\"\r\n" + //
                        "  }\r\n" + //
                        "}";
			res = RestAssured.given()
					.contentType(ContentType.JSON)
					.body(payload)
					.post("/addProject");
		}
		
	}

    @Then("the values must be updated")
    public void verifyValuesUpdated() {
        res.then()
                .assertThat()
                .body("name", equalTo(name)) 
                .body("data.year", notNullValue());
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
    // DELETE
    // =========================
    @When("user deletes the object {string}")
    public void deleteObject(String id) {
        response = given().when().delete("/objects/" + id);
    }

    @When("user deletes already deleted objects")
    public void deleteMultiple(DataTable dataTable) {
        for (String id : dataTable.asList()) {
            response = given().when().delete("/objects/" + id);
            System.out.println(response.getStatusCode());
        }
    }

    @When("user sends PUT to {string} with valid json payloady")
    public void updateObjectInvalidID(String endpoint) {
        String Body = "{\r\n" + //
                        "  \"name\": \"Apple MacBook Pro 16\",\r\n" + //
                        "  \"data\": {\r\n" + //
                        "    \"year\": 2019,\r\n" + //
                        "    \"price\": 1849.99,\r\n" + //
                        "    \"CPU model\": \"Intel Core i9\",\r\n" + //
                        "    \"Hard disk size\": \"1 TB\"\r\n" + //
                        "  }\r\n" + //
                        "}";
        res = request.body(Body)
                .when()
                .post(endpoint);
    }

    @When("user sends PUT to {string} with valid object id")
	public void updateObjectMalformed(String endpoint) {
		String data = "{\r\n" + //
                        "  \"name\": \"updated iphone 16\",\r\n" + //
                        "  \"data\": "+null+"\r\n" + //
                        "}";
		res = RestAssured.given()
				.contentType(ContentType.JSON)
				.body(data)
				.post(endpoint);
	}
}


    

    
    

    @When("user sends GET requests to {string} with invalid IDs")
    public void multipleInvalidGet(String endpoint, DataTable dataTable) {
        for (String id : dataTable.asList()) {
            response = given().when().get(endpoint + "/" + id);
            System.out.println(response.getStatusCode());
        }
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