package hooks;

import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.FileUtility;
import utils.RestUtility;

public class Hooks {
    @Before
    public void setUp() {
        RestAssured.baseURI = FileUtility.get("base.url");
    }
    
    public static String newObjectId;
    @Before("@DeleteObject")
    public void setupObjectForTest() {
        String body = "{\n"
                + "  \"name\": \"Setup Object for Delete\",\n"
                + "  \"data\": {\n"
                + "    \"year\": 2024,\n"
                + "    \"price\": 100.00\n"
                + "  }\n"
                + "}";
        
        Response setupRes = RestUtility.postNoAuth(FileUtility.get("endpoint.objects"), body, "application/json");
        newObjectId = setupRes.jsonPath().getString("id");
    }
}
