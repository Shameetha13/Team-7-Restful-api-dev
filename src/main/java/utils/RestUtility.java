package utils;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class RestUtility {

    public static Response post(String endpoint, Object body) {
        return given()
                .header("Content-Type", "application/json")
                .header("x-api-key", FileUtility.get("api.key"))
                .body(body)
                .when()
                .post(endpoint);
    }

    public static Response get(String endpoint) {
        return given()
                .header("x-api-key", FileUtility.get("api.key"))
                .when()
                .get(endpoint);
    }

    public static Response put(String endpoint, Object body) {
        return given()
                .header("Content-Type", "application/json")
                .header("x-api-key", FileUtility.get("api.key"))
                .body(body)
                .when()
                .put(endpoint);
    }

    public static Response patch(String endpoint, Object body) {
        return given()
                .header("Content-Type", "application/json")
                .header("x-api-key", FileUtility.get("api.key"))
                .body(body)
                .when()
                .patch(endpoint);
    }

    public static Response delete(String endpoint) {
        return given()
                .header("x-api-key", FileUtility.get("api.key"))
                .when()
                .delete(endpoint);
    }



    public static Response postNoAuth(String endpoint, Object body) {
        return given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(endpoint);
    }

    public static Response getNoAuth(String endpoint) {
        return given()
                .when()
                .get(endpoint);
    }



    public static Response getWithKey(String endpoint, String apiKey) {
        return given()
                .header("x-api-key", apiKey)
                .when()
                .get(endpoint);
    }

    public static Response deleteWithKey(String endpoint, String apiKey) {
        return given()
                .header("x-api-key", apiKey)
                .when()
                .delete(endpoint);
    }



    public static Response postWithContentType(String endpoint, String contentType, Object body) {
        return given()
                .header("Content-Type", contentType)
                .body(body)
                .when()
                .post(endpoint);
    }

    public static Response postWithContentTypeNoAuth(String endpoint, String contentType, Object body) {
        return given()
                .header("Content-Type", contentType)
                .body(body)
                .when()
                .post(endpoint);
    }
}