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

	@Given("the API URL {string} is up and running")
	public void setBaseUrl(String baseuri) {
		
		Assert.assertEquals(RestAssured.baseURI, baseuri,
				"Base URI mismatch: expected " + baseuri + " but Hooks set " + RestAssured.baseURI);
	}


	// Author Barath
	@When("user deletes the created collection item from {string}")
	public void deleteCreatedItem(String endpoint) {
		response = RestUtility.delete(endpoint);
	}

	@When("user sends authenticated DELETE requests to {string} with invalid IDs")
	public void deleteWithInvalidIds(String baseEndpoint, DataTable table) {
		List<String> ids = table.asList();
		for (String id : ids) {
			response = RestUtility.delete(baseEndpoint + id);
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