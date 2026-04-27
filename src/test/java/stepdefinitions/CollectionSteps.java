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
}