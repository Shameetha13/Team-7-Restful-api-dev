package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;
import io.cucumber.datatable.DataTable;

import java.util.List;
import java.util.Map;

public class ObjectSteps {
    Response res;
	RequestSpecification request;
    String name;

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

    @Then("the status code should be {int}")
	public void checkResponseCode(int code) {
		res.then().statusCode(code);
	}

    @And("a unique object id should be generated")
	public void verifyIdExists() {
		res.then().body("$", hasKey("id"));
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


    

    
    
