package hooks;

import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import pojoclass.AuthRequest;
import stepdefinitions.AuthSteps;
import utils.FileUtility;
import utils.JavaUtility;
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

    @Before("@RegisterUser")
    public void registerUserBeforeLogin() {
        String email = JavaUtility.getRandomEmail();
        String name = JavaUtility.getRandomName();
        String password = "Test@1234";
        AuthRequest registerRequest = new AuthRequest(email, password, name);
        RestUtility.post(FileUtility.get("endpoint.register"), registerRequest);
        AuthSteps.setLoginEmail(email);
    }
}
