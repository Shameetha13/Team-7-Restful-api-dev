package stepdefinitions;


import java.util.*;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import pojoclass.ObjectAndCollection;
import io.restassured.response.Response;
import static org.testng.Assert.*;
import utils.ExcelUtility;
import utils.FileUtility;
import utils.RestUtility;
import static org.hamcrest.Matchers.*;
import java.util.List;
import java.util.Map;
import org.testng.Assert;

public class CollectionSteps {

    private Response response;
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
	
	
    // Author Kamala Kannan
	
	@When("user sends authenticated GET to {string}")
	public void authenticatedGet(String endpoint) {
		response = RestUtility.get(endpoint);
	}


	@And("the response should contain field {string}")
	public void fieldPresence(String field) {
		response.then().body("$", everyItem(hasKey("collectionName")));
	}

	@When("user sends authenticated GET to {string} with no objects and key {string}")
	public void getWithKey(String endpoint, String key) {
		response = RestUtility.getWithKey(endpoint, key);
	}
	
	@Given("the API URL {string} is up and running")
	public void setBaseUrl(String baseuri) {
		Assert.assertEquals(RestAssured.baseURI, baseuri,
				"Base URI mismatch: expected " + baseuri + " but Hooks set " + RestAssured.baseURI); 
	}

	@When("user sends GET to {string} with invalid key {string}")
	public void getWithInvalidKey(String endpoint, String key) {
		response = RestUtility.getWithKey(endpoint, key);

	}

	@When("user sends unauthenticated GET to {string}")
	public void unauthenticatedGet(String endpoint) {
		response = RestUtility.getNoAuth(endpoint);
	}

	
	// Author Barath
	@When("user deletes the created collection item from {string}")
	public void deleteCreatedItem(String endpoint) {
		response = RestUtility.delete(endpoint);
	}

	@When("user sends GET request to collections endpoint with invalid API key from test data")
	public void userSendsGETWithInvalidAPIFromExcel() {
		int rowCount = ExcelUtility.getRowCount("Sheet1", 22);
	}

	@When("user sends authenticated DELETE requests to {string} with invalid IDs")
	public void deleteWithInvalidIds(String baseEndpoint, DataTable table) {
		List<String> ids = table.asList();
		for (String id : ids) {
			response = RestUtility.delete(baseEndpoint + id);
		}

		for (int i = 0; i < rowCount; i++) {
			String invalidAPI = ExcelUtility.getCellData("Sheet1", i, 22);
			response = RestAssured.given().header("x-api-key", invalidAPI).when().get("/collections");
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
	
	
    // Author Manish
	@When("I send a GET request to {string}")
	public void sendGetRequestToEndpoint(String endpoint) {
		response = RestUtility.get(endpoint);
	}
	
	public void createCollectionIfNotExists(String collectionName) {
	    String endpoint = "/collections";

	    // Create request body
	    Map<String, String> body = new HashMap<>();
	    body.put("name", collectionName);

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
	    String endpoint = "/collections/" + collectionName + "/objects";

	    System.out.println("Collection Name: " + collectionName);
	    System.out.println("Endpoint: " + endpoint);

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

	@When("I send a GET request to {string} and measure response time")
	public void sendGetRequestWithTime(String endpoint) {
		sendGetRequestToEndpoint(endpoint);
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

	@When("I send a PATCH request to collection {string} with payload")
	public void sendPatchWithConfig(String collection, DataTable dataTable) {
		String newObjectId = createTempObject(collection, "Temp Product", 2023, 1000.0, "i5", "256GB");
		System.out.println(newObjectId);
		List<List<String>> data = dataTable.asLists();
		String field = data.get(0).get(0);
		String value = data.get(1).get(0);

		String endpointTemplate = FileUtility.get("endpoint.collection.object");
		String body = "{ \"" + field + "\": \"" + value + "\" }";
		String actualPath = endpointTemplate.replace("{collectionName}", collection).replace("{id}", newObjectId);
		System.out.println(actualPath);
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
}