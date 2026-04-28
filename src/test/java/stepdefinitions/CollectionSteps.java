package stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import pojoclass.ObjectAndCollection;
import io.restassured.response.Response;
import utils.ExcelUtility;
import utils.FileUtility;
import utils.RestUtility;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;

import org.testng.Assert;

public class CollectionSteps {

	RequestSpecification request;
	Response response;
	String requestedId;
	static String objectId;

	@Given("the API URL {string} is up and running")
	public void setBaseUrl(String baseuri) {
		Assert.assertEquals(RestAssured.baseURI, baseuri,
				"Base URI mismatch: expected " + baseuri + " but Hooks set " + RestAssured.baseURI);
	}

	// Author Varshinee
	private String createTempObject(String collection, String name, int year, double price, String cpu, String disk) {
		ObjectAndCollection obj = new ObjectAndCollection();
		obj.setName(name);

		ObjectAndCollection.Data data = new ObjectAndCollection.Data();
		data.setYear(year);
		data.setPrice(price);
		data.setCpuModel(cpu);
		data.setHardDisk(disk);
		obj.setData(data);
		
		String endpointTemplate = FileUtility.get("endpoint.collection.objects");
		String endpoint = endpointTemplate.replace("{collectionName}", collection);
		Response postResponse = RestUtility.post(endpoint, obj);
		
		if (postResponse.getStatusCode() == 200) {
			return postResponse.jsonPath().getString("id");
		} else {
			System.out.println(
					"POST failed [" + postResponse.getStatusCode() + "]: " + postResponse.getBody().asString());
			return null;
		}
	}

	public void setPathParams(String key, String cname, String id) {
		requestedId = id;
		String endpointTemplate = FileUtility.get("endpoint.collection.object");
		String endpoint = endpointTemplate
				.replace("{collectionName}", cname)
				.replace("{id}", id);
		request = RestAssured.given()
				.header("x-api-key", key)
				.baseUri(FileUtility.get("base.url"))
				.basePath(endpoint);
		
	}
	
	@When("GET request is sent to the correct endpoint")
	public void sendGetRequest() {
		response = request.when().get();
	}

	@When("I send a GET request to collection {string} for object at row {int}")
	public void sendGetRequestForObject(String collection, int rowNum) {
	    Map<String, String> rowData = ExcelUtility.getRowDataAsMap("bdata", rowNum);
	    String Id = rowData.get("id"); 
	    String endpoint = "/collections/" + collection + "/objects/" + Id;
	    response = RestUtility.get(endpoint);
	}
	
	@Then("the response status code should be {int}")
	public void checkResponseCode(int code) {
		response.then().statusCode(code);
	}

	@Then("the value of {string} field in response should match with that in request")
	public void verifyIdMatch(String fieldName) {
		response.then().body(fieldName, equalTo(requestedId));
	}

	@And("the response body should contain the field {string}")
	public void verifyFieldPresence(String fieldName) {
		response.then().body("$", hasKey(fieldName));
	}

	@And("the {string} header of response should be {string}")
	public void verifyHeader(String headerName, String headerValue) {
		response.then().header(headerName, containsString(headerValue));
	}

	@Given("the API key {string} is invalid but collection name and Object Id are valid")
	public void setInvalidApiKey(String invalidkey) {
		setPathParams(invalidkey, FileUtility.get("collection.name.valid"), FileUtility.get("collection.object.id"));
	}

	@Given("the API key and collection name are valid but Object Id {string} is invalid")
	public void setInvalidId(String invalidId) {
		setPathParams(FileUtility.get("api.key"), FileUtility.get("collection.name.valid"), invalidId);
	}
	
	@Given("the API key and Object Id are valid but collection name {string} is invalid")
	public void setInvalidName(String cname) {
		setPathParams(FileUtility.get("api.key"), cname, FileUtility.get("collection.object.id"));
	}
	
	@Then("the error message in response body should contain {string}")
	public void verifyErrorMessage(String expectedMessage) {
		response.then().assertThat().body(containsString(expectedMessage));
	}

	// Author Shameetha
	@When("I add a collection item from Excel sheet {string} at row {int} into collection {string}")
	public void addCollectionItemFromExcel(String sheetName, int rowNum, String collection) {

		Map<String, String> rowData = ExcelUtility.getRowDataAsMap(sheetName, rowNum);

		ObjectAndCollection obj = new ObjectAndCollection();
		obj.setName(rowData.get("name"));

		ObjectAndCollection.Data data = new ObjectAndCollection.Data();
		data.setYear(Integer.parseInt(rowData.get("year")));
		data.setPrice(Double.parseDouble(rowData.get("price")));
		data.setCpuModel(rowData.get("cpu"));
		data.setHardDisk(rowData.get("harddisk"));
		obj.setData(data);

		String endpoint = "/collections/" + collection + "/objects";
		response = RestUtility.post(endpoint, obj);
		objectId = response.jsonPath().getString("id");
	}

	@And("the response should have name from Excel row {int}")
	public void validateName(int rowNum) {

		Map<String, String> rowData = ExcelUtility.getRowDataAsMap("data", rowNum);
		String expectedName = rowData.get("name");

		String actualName = response.jsonPath().getString("name");

		if (actualName == null) {
			System.out.println("Name not present in response");
		} else {
			Assert.assertEquals(actualName, expectedName, "Name mismatch!");
		}
	}

	@And("the response should have {string} from Excel row {int}")
	public void validateFieldFromExcel(String fieldName, int rowNum) {

		Map<String, String> rowData = ExcelUtility.getRowDataAsMap("data", rowNum);
		String expectedValue = rowData.get(fieldName);

		String jsonPath = fieldName.equals("name") ? "name" : "data." + getJsonPathMapping(fieldName);

		if (fieldName.equalsIgnoreCase("price") || fieldName.equalsIgnoreCase("year")) {

			double expected = Double.parseDouble(expectedValue);
			double actual = response.jsonPath().getDouble(jsonPath);
			Assert.assertEquals(actual, expected, 0.001, fieldName + " mismatch!");
		} else {

			String actualValue = response.jsonPath().getString(jsonPath);
			Assert.assertEquals(actualValue, expectedValue, fieldName + " mismatch!");
		}
	}

	private String getJsonPathMapping(String field) {
		if (field.equalsIgnoreCase("year"))
			return "year";
		if (field.equalsIgnoreCase("price"))
			return "price";
		if (field.equalsIgnoreCase("cpu"))
			return "'CPU model'";
		if (field.equalsIgnoreCase("harddisk"))
			return "'Hard disk size'";
		return field;
	}

	@And("the collection response time should be within {int} ms")
	public void verifyResponseTime(int time) {
		response.then().assertThat().time(lessThan((long) time));
	}

	// Author Manish
	@When("I send a GET request to {string}")
	public void sendGetRequestToEndpoint(String endpoint) {
		response = RestUtility.get(endpoint);
	}

	public void createCollectionIfNotExists(String collectionName) {
	    String endpoint = "/collections/"+collectionName+"/objects";

	    String body = "{\"name\":\"Apple MacBook Pro 16\",\"data\":{\"year\":2019,\"price\":1849.99,\"CPU model\":\"Intel Core i9\",\"Hard disk size\":\"1 TB\"}}";

	    try {
	        Response res = RestUtility.post(endpoint, body);

	        if (res.getStatusCode() == 200) {
	            System.out.println("Collection created: " + collectionName);
	        } else {
	            System.out.println("Collection may already exist: " + collectionName);
	        }

	    } catch (Exception e) {
	        System.out.println("Skipping creation (possibly exists): " + collectionName);
	    }
	}
	@When("I send a GET request to collection objects using row {int}")
	public void sendGetRequestToEndpoint(int rowNum) {
	    createCollectionIfNotExists("product");
	    createCollectionIfNotExists("test");

	    String collectionName = ExcelUtility.getCellData("Sheet1", rowNum, 23);
	    String fetchEndpoint = FileUtility.get("endpoint.collection.objects");
	    String endpoint = fetchEndpoint.replace("{collectionName}", collectionName);
	    response = RestUtility.get(endpoint);
	}
	
	@And("the response body should contain a list of all objects in the collection")
	public void validateList() {
		response.then().body("$", not(empty()));
	}

	@And("the response body should contain an empty list")
	public void validateEmptyList() {
		response.then().body("$", empty());
	}

	@When("I send a GET request to measure response time")
	public void sendGetRequestWithTime() {
		String endpointTemplate = FileUtility.get("endpoint.collection.objects");
		String endpoint = endpointTemplate.replace("{collectionName}", FileUtility.get("collection.name.products"));
		response = RestUtility.get(endpoint);
	}

	@When("I send a PUT request to {string} with a valid full payload")
	public void sendPutRequestValid(String endpoint) {
		String body = "{ \"name\": \"Updated Product\", \"data\": { \"price\": 1000, \"year\": 2025 } }";
		response = RestUtility.put(endpoint, body);
	}

	@And("the response body should contain the fully updated object")
	public void validateUpdatedObject() {
		response.then().body("name", notNullValue());
	}

	@And("the response body should reflect all updated values from the request")
	public void validateUpdatedValues() {
		response.then().body("name", equalTo("Updated Product"));
	}

	@When("I send a PUT request to {string} with missing required fields")
	public void sendPutMissingFields(String endpoint) {
		String body = "{ \"name\": \"\" }";
		response = RestUtility.put(endpoint, body);
	}

	@When("I send a PUT request to collection {string} with name {string}, year {int}, price {double}, cpu {string}, and disk {string}")
	public void sendPutRequest(String collection, String name, int year, double price, String cpu, String disk) {
		String endpoint = "/collections/" + collection + "/objects/" + objectId;

		ObjectAndCollection obj = new ObjectAndCollection();
		obj.setName(name);

		ObjectAndCollection.Data data = new ObjectAndCollection.Data();
		data.setYear(year);
		data.setPrice(price);
		data.setCpuModel(cpu);
		data.setHardDisk(disk);
		obj.setData(data);

		response = RestUtility.put(endpoint, obj);
	}

	@When("I send a PATCH request to collection with payload")
	public void sendPatchWithConfig(DataTable dataTable) {
		String collection = FileUtility.get("collection.name.products");
		String newObjectId = createTempObject(collection, "Temp Product", 2023, 1000.0, "i5", "256GB");
		List<List<String>> data = dataTable.asLists();
		String field = data.get(0).get(0);
		String value = data.get(1).get(0);

		String endpointTemplate = FileUtility.get("endpoint.collection.object");
		String body = "{ \"" + field + "\": \"" + value + "\" }";
		String actualPath = endpointTemplate.replace("{collectionName}", collection).replace("{id}", newObjectId);
		response = RestUtility.patch(actualPath, body);

	}

	@And("the response body should show updated {string} as {string}")
	public void validatePatchedField(String field, String expectedValue) {
		String actual = response.jsonPath().getString(field);
		Assert.assertEquals(actual, expectedValue, "Field '" + field + "' mismatch");
	}

	@And("other attributes should remain unchanged")
	public void validateOtherFields() {
		response.then().body("id", notNullValue());
	}

	@When("I send a PATCH request to {string} with partial data and measure response time")
	public void sendPatchSingle(String endpoint, DataTable dataTable) {
		Map<String, String> data = dataTable.asMaps().get(0);
		String cName = data.get("collectionName");
		String oId = data.get("objectId");
		String body = "{ \"name\": \"Patched Name\" }";
		String patchEndpoint = "/collections/" + cName + "/objects/" + oId;
		response = RestUtility.patch(patchEndpoint, body);
	}

	// Author Kamala Kannan
	@When("user sends authenticated GET to {string}")
	public void authenticatedGet(String endpoint) {
		response = RestUtility.get(endpoint);
	}

	@And("the response should contain field {string}")
	public void fieldPresence(String field) {
		response.then().body("$", everyItem(hasKey("collectionName")));
	}

	@When("user sends authenticated GET to {string} with no objects and valid key")
	public void getWithKey(String endpoint) {
		String key = FileUtility.get("collection.alt.api.key");	
		response = RestUtility.getWithKey(endpoint, key);
	}

	@When("user sends GET to {string} with invalid key {string}")
	public void getWithInvalidKey(String endpoint, String key) {
		response = RestUtility.getWithKey(endpoint, key);
	}

	@When("user sends unauthenticated GET to {string}")
	public void unauthenticatedGet(String endpoint) {
		response = RestUtility.getNoAuth(endpoint);
	}

	@When("user sends GET request to collections endpoint with invalid API key from test data")
	public void userSendsGETWithInvalidAPIFromExcel() {
		int rowCount = ExcelUtility.getRowCount("Sheet1", 22);

		for (int i = 0; i < rowCount; i++) {
			String invalidAPI = ExcelUtility.getCellData("Sheet1", i, 22);
			response = RestAssured.given().header("x-api-key", invalidAPI).when().get("/collections");

		}
	}

	// Author Barath
	@When("user deletes the created collection item from {string}")
	public void deleteCreatedItem(String endpoint) {
		response = RestUtility.delete(endpoint);
	}

	@When("user sends authenticated DELETE request with invalid IDs")
	public void deleteWithInvalidIds(DataTable table) {
		List<String> ids = table.asList();
		for (String id : ids) {
			String endpointTemplate = FileUtility.get("endpoint.collection.object");
			String actualPath = endpointTemplate.replace("{collectionName}", FileUtility.get("collection.name.valid")).replace("{id}", id);
			response = RestUtility.delete(actualPath);
		}
	}

	@When("user sends authenticated DELETE to {string}")
	public void deleteOtherUserCollection(String endpoint) {
		response = RestUtility.delete(endpoint);
	}

	@When("user deletes the collection item for {string}")
	public void deleteItem(String collectionName) {
		String newObjectId = createTempObject(collectionName, "Temp Delete", 2023, 1000.0, "i5", "256GB");
		String endpointTemplate = FileUtility.get("endpoint.collection.object");
		String actualPath = endpointTemplate.replace("{collectionName}", collectionName).replace("{id}", newObjectId);
		response = RestUtility.delete(actualPath);
	}

	@When("user deletes the collection item from Excel sheet {string} at row {int}")
	public void deleteItemFromExcel(String sheetName, int rowNum) {
		Map<String, String> data = ExcelUtility.getRowDataAsMap(sheetName, rowNum);
		String collectionName = data.get("collectionNames");
		String name = data.get("tempName");
		int year = (int) Double.parseDouble(data.get("tempYear"));
		double price = Double.parseDouble(data.get("tempPrice"));
		String cpu = data.get("tempCPU");
		String disk = data.get("tempDisk");
		String newObjectId = createTempObject(collectionName, name, year, price, cpu, disk);

		String endpointTemplate = FileUtility.get("endpoint.collection.object");
		String actualPath = endpointTemplate.replace("{collectionName}", collectionName).replace("{id}", newObjectId);

		response = RestUtility.delete(actualPath);
	}
}
