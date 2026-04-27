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
	
	private Response response;
	// Author Manish
	@When("I send a GET request to {string}")
	public void sendGetRequestToEndpoint(String endpoint) {
		response = RestUtility.get(endpoint);
	}

	@When("I send a GET request to collection objects using row {int}")
	public void sendGetRequestToEndpoint(int rowNum) {
		String collectionName = ExcelUtility.getCellData("Sheet1", rowNum, 23); // col 23 = collectionName
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