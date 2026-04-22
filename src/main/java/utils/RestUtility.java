package utils;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class RestUtility {

    // ===================== GET =====================

    public static Response get(String url, String token) {

        return given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
        .when()
                .get(url)
        .then()
                .extract().response();
    }

    // ===================== POST =====================

    public static Response post(String url, String token, Object payload) {

        return given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(payload)
        .when()
                .post(url)
        .then()
                .extract().response();
    }

    // ===================== PUT =====================

    public static Response put(String url, String token, Object payload) {

        return given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(payload)
        .when()
                .put(url)
        .then()
                .extract().response();
    }

    // ===================== PATCH =====================

    public static Response patch(String url, String token, Object payload) {

        return given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(payload)
        .when()
                .patch(url)
        .then()
                .extract().response();
    }

    // ===================== DELETE =====================

    public static Response delete(String url, String token) {

        return given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
        .when()
                .delete(url)
        .then()
                .extract().response();
    }
}