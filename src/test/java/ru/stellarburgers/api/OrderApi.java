package ru.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import ru.stellarburgers.entities.CreateOrderRequest;
import ru.stellarburgers.entities.CreateOrderResponse;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderApi {
    public static final List<Integer> createdOrders = new ArrayList<>();

    @Step("Создаём заказ")
    public static Response createOrder(CreateOrderRequest request, String accessToken) {
        Response rawResponse;
        if (accessToken == null) {
            rawResponse = given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/orders");
        } else {
            rawResponse = given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", accessToken)
                    .body(request)
                    .when()
                    .post("/api/orders");
        }

        if (rawResponse.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
            return rawResponse;
        }

        CreateOrderResponse response = rawResponse.as(CreateOrderResponse.class);
        if (response != null && response.getNumber() != 0) {
            createdOrders.add(response.getNumber());
        }
        return rawResponse;
    }

    @Step("Получаем заказ по номеру")
    public static Response getOrder(int orderNumber, String accessToken) {
        if (accessToken != null) {
            return given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", accessToken)
                    .when()
                    .get("/api/v1/orders/{number}", orderNumber);
        } else {
            return given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/api/v1/orders/{number}", orderNumber);
        }
    }
}