package stepdefinitions;


import java.util.*;
=======
>>>>>>> refs/remotes/origin/TS-01/05/09
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import pojoclass.ObjectAndCollection;
import io.restassured.response.Response;
<<<<<<< HEAD
import static org.testng.Assert.*;
=======
>>>>>>> refs/remotes/origin/TS-01/05/09
import utils.ExcelUtility;
import utils.FileUtility;
import utils.RestUtility;
import static org.hamcrest.Matchers.*;
import java.util.List;
import java.util.Map;
import org.testng.Assert;


public class CollectionSteps {
	
// Author Kamala Kannan
	private Response response;
	
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



	@When("user sends authenticated DELETE requests to {string} with invalid IDs")
	public void deleteWithInvalidIds(String baseEndpoint, DataTable table) {
		List<String> ids = table.asList();
		for (String id : ids) {
			response = RestUtility.delete(baseEndpoint + id);
		}
	}

		for (int i = 0; i < rowCount; i++) {
			String invalidAPI = ExcelUtility.getCellData("Sheet1", i, 22);
			response = RestAssured.given().header("x-api-key", invalidAPI).when().get("/collections");



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
	}

}