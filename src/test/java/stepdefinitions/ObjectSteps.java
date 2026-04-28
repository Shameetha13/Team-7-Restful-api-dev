package stepdefinitions;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.*;
import io.cucumber.datatable.DataTable;
import utils.FileUtility;
import utils.RestUtility;

import java.util.List;
import java.util.Map;

public class ObjectSteps {

    Response res;
    RequestSpecification request;
    String Name;
    static String newObjectId;
    
    //Author Kamala Kannan
    @When("user sends GET to endpoint")
    public void getAllObjects() {
        res = RestUtility.getNoAuth(FileUtility.get("endpoint.objects"));
    }
    
    @Then("the status code should be {int}")
    public void checkResponseCode(int code) {
        res.then().log().all().statusCode(code);
    }
    
    @And("each object contains the field {string}")
    public void fieldPresence(String field) {
        res.then().body("$", everyItem(hasKey(field)));
    }
    
    @And("the response time is below {int} ms")
    public void verifyResponseTime(int time) {
        res.then().assertThat().time(lessThan((long) time));
    }

    @And("the response header {string} should be {string}")
    public void verifyHeader(String headerName, String headerValue) {
        res.then().header(headerName, containsString(headerValue));
    }
    
    @Given("object with id {int} and {int} exists")
    public void setQueryParameter(int id1, int id2) {
        request = RestAssured.given()
                .queryParam("id", id1)
                .queryParam("id", id2);
    }
    
    @When("user sends GET to endpoint with query params")
    public void getObjectsWithQueryParams() {
        res = request.when().get(FileUtility.get("endpoint.objects"));
    }
    
    @Given("object with id {string} doesn't exists")
    public void setParamString(String oId) {
        request = RestAssured.given().queryParam("id", oId);
    }

    @When("I send a PATCH request to update the price for an object:")
    public void patchObjectFromConfig(DataTable dataTable) {
        String objectId = FileUtility.get("object.single.id");
        String price = dataTable.asMaps().get(0).get("newPrice");
        String patchBody = "{\n"
                + "  \"data\": {\n"
                + "    \"price\": \"" + price + "\"\n"
                + "  }\n"
                + "}";
        String endpointTemplate = FileUtility.get("endpoint.object.by.id");
        String endpoint = endpointTemplate.replace("{id}", objectId);
        res = RestUtility.patchNoAuth(endpoint, patchBody);
    }
    
    @And("the {string} in the response should match {string}")
    public void verifyNestedAttribute(String path, String expectedValue) {
        res.then().body(path, equalTo(expectedValue));
    }
    
    @And("the appropriate error message {string} is present in response body")
    public void verifyErrorMessage(String expectedMessage) {
        res.then().assertThat().body(containsString(expectedMessage));
    }
    
    @When("I send a PATCH request to endpoint with invalid object id {string} and price {string}")
    public void patchObjectWithExplicitId(String objectId, String price) {
        String patchBody = "{\n"
                + "  \"data\": {\n"
                + "    \"price\": \"" + price + "\"\n"
                + "  }\n"
                + "}";
        String endpointTemplate = FileUtility.get("endpoint.object.by.id");
        String endpoint = endpointTemplate.replace("{id}", objectId);
        res = RestUtility.patchNoAuth(endpoint, patchBody);
    }
    
    //Author Barath
    @When("user sends GET with valid object id {int}")
    public void getSingleObject(int id) {
        String endpoint = FileUtility.get("endpoint.objects");
        res = RestUtility.getNoAuth(endpoint + "/" + id);
    }
    
    @When("user sends GET with invalid object id {int}")
    public void getSingleObjectId(int id) {
        String endpoint = FileUtility.get("endpoint.objects");
        res = RestUtility.getNoAuth(endpoint + "/" + id);
    }
   
    @And("the response should have id {int}")
    public void validateId(int value) {
        res.then().body("id", equalTo(String.valueOf(value)));
    }
    
    @When("DELETE is sent to object endpoint from config")
    public void deleteObjectFromConfig() {
    	String idToDelete = hooks.Hooks.newObjectId;
    	String endpointTemplate = FileUtility.get("endpoint.object.by.id");
        String endpoint = endpointTemplate.replace("{id}", idToDelete);
        res = RestUtility.deleteNoAuth(endpoint);
    }
    
    @Then("the appropriate message {string} is present in response body")
    public void verifyMessage(String expectedMessage) {
        res.then().assertThat().body(containsString(expectedMessage));
    }
    
    @When("DELETE is sent to objects endpoint with invalid IDs")
    public void deleteWithInvalidIds(DataTable table) {
        List<String> ids = table.asList();
        for (String id : ids) {
        	String endpointTemplate = FileUtility.get("endpoint.object.by.id");
            String endpoint = endpointTemplate.replace("{id}", id);
            res = RestUtility.deleteNoAuth(endpoint);
        }
    }
    
    @When("DELETE is sent to endpoint for reserved object")
    public void deleteReservedObject() {
        String reservedId = FileUtility.get("object.reserved.id");
        String endpointTemplate = FileUtility.get("endpoint.object.by.id");
        String endpoint = endpointTemplate.replace("{id}", reservedId);
        res = RestUtility.deleteNoAuth(endpoint);
    }
    
    //Author Varshinee
    @When("user sends POST to endpoint with name as {string} year as {int} price as {double} cpu model as {string} and disk size as {string} and content type is {string}")
    public void createObject(String name, int year, double price, String model, String size, String type) {
        Name = name;
        String body = "{\r\n"
                + "  \"name\": \"" + name + "\",\r\n"
                + "  \"data\":{\r\n"
                + "    \"year\":" + year + ",\r\n"
                + "    \"price\":" + price + ",\r\n"
                + "    \"CPU model\": \"" + model + "\",\r\n"
                + "    \"Hard disk size\": \"" + size + "\"\r\n"
                + "  }\r\n"
                + "}";
        res = RestUtility.postNoAuth(FileUtility.get("endpoint.objects"), body, type);
    }

    @When("user sends POST to endpoint with name as {string} and price as {string} and content type is {string}")
    public void createObjectMalformed(String name, String price, String type) {
        String body = "{\r\n"
                + "  \"name\": \"" + name + "\",\r\n"
                + "  \"data\": {\r\n"
                + "    \"price\": \"" + price + "\"\n"
                + "  }\r\n"
                + "}";
        res = RestUtility.postNoAuth(FileUtility.get("endpoint.objects"), body, type);
    }

    @When("user sends POST to endpoint with complete valid json body and content type is {string}")
    public void sendPostRequest(String type) {
        String body = "{\r\n"
                + "  \"name\": \"iphone Pro 16\",\r\n"
                + "  \"data\": {\r\n"
                + "    \"year\": 2019,\r\n"
                + "    \"price\": 1849.99,\r\n"
                + "    \"CPU model\": \"Intel Core i9\",\r\n"
                + "    \"Hard disk size\": \"1 TB\"\r\n"
                + "  }\r\n"
                + "}";
        res = RestUtility.postNoAuth(FileUtility.get("endpoint.objects"), body,type);
        newObjectId = res.body().jsonPath().getString("id");
    }

    @When("user sends PUT to endpoint with valid object id and complete payload with updated values")
    public void updateObject(DataTable data) {
        String objectId = FileUtility.get("object.single.id");
        List<Map<String, String>> table = data.asMaps(String.class, String.class);
        for (Map<String, String> row : table) {
            Name = row.get("name");
            int year = Integer.parseInt(row.get("year"));
            double price = Double.parseDouble(row.get("price"));
            String model = row.get("CPU model");
            String size = row.get("Hard disk size");
            String payload = "{\r\n"
                    + "  \"name\": \"" + Name + "\",\r\n"
                    + "  \"data\": {\r\n"
                    + "    \"year\": " + year + ",\r\n"
                    + "    \"price\": " + price + ",\r\n"
                    + "    \"CPU model\": \"" + model + "\",\r\n"
                    + "    \"Hard disk size\": \"" + size + "\"\r\n"
                    + "  }\r\n"
                    + "}";
            String endpointTemplate = FileUtility.get("endpoint.object.by.id");
            String endpoint = endpointTemplate.replace("{id}", objectId);
            res = RestUtility.putNoAuth(endpoint, payload);
        }
    }

    @When("user sends PUT to endpoint with invalid id {string}")
    public void updateObjectInvalidId(String oId) {
        String body = "{\r\n"
                + "  \"name\": \"Updated Apple MacBook Pro 16\",\r\n"
                + "  \"data\": {\r\n"
                + "    \"year\": 2019,\r\n"
                + "    \"price\": 1849.99,\r\n"
                + "    \"CPU model\": \"Intel Core i9\",\r\n"
                + "    \"Hard disk size\": \"1 TB\"\r\n"
                + "  }\r\n"
                + "}";
        String endpointTemplate = FileUtility.get("endpoint.object.by.id");
        String endpoint = endpointTemplate.replace("{id}", oId);
        res = RestUtility.putNoAuth(endpoint, body);
    }

    @When("user sends PUT to endpoint with name as {string} for object from config")
    public void updateObjectMissingData(String name) {
        Name = name;
        String objectId = FileUtility.get("object.single.id");
        String body = "{\r\n"
                + "  \"name\": \"" + name + "\",\r\n"
                + "  \"data\": null\r\n"
                + "}";
        String endpointTemplate = FileUtility.get("endpoint.object.by.id");
        String endpoint = endpointTemplate.replace("{id}", objectId);
        res = RestUtility.putNoAuth(endpoint, body);
    }

    @And("the response body has the field {string}")
    public void verifyFieldPresence(String fieldName) {
        res.then().body("$", hasKey(fieldName));
    }

    @And("the value of {string} field in response should match the name in request")
    public void verifyNameMatch(String fieldName) {
        res.then().body(fieldName, equalTo(Name));
    }

    @And("the response body should contain an empty array")
    public void validateEmptyList() {
        res.then().body("$", empty());
    }  
}
