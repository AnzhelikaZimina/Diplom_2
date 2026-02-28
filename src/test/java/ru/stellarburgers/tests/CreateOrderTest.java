package ru.stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.*;
import ru.stellarburgers.api.IngredientsApi;
import ru.stellarburgers.api.OrderApi;
import ru.stellarburgers.api.UserApi;
import ru.stellarburgers.entities.CreateOrderRequest;
import ru.stellarburgers.entities.LoginUserResponse;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class CreateOrderTest extends BaseTest {

    private static String accessToken;
    private static String[] validIngredients;

    @Before
    public void setup() {
        System.out.println(RestAssured.baseURI);
        String email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@test.com";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = "TestUser";

        UserApi.createUser(email, password, name);
        Response loginResponse = UserApi.loginUser(email, password);
        LoginUserResponse loginData = loginResponse.as(LoginUserResponse.class);
        accessToken = loginData.getAccessToken();

        validIngredients = IngredientsApi.getRandomIngredientsIds(3);
    }

    @Test
    @DisplayName("Проверка создания заказа с авторизацией")
    @Description("Тест создает заказ с авторизацией и проверяет, что сервер возвращает 201 Created")
    public void orderShouldBeCreatedWithAuthorization() {
        CreateOrderRequest request = new CreateOrderRequest(validIngredients);

        Response response = OrderApi.createOrder(request, accessToken);

        checkIfOrderCreated(response);
    }

    @Step("Проверка статуса и сообщения для успешного заказа")
    public void checkIfOrderCreated(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Проверка создания заказа без авторизации")
    @Description("Тест проверяет, что при создании заказа без авторизации сервер возвращает 401 Unauthorized")
    public void orderShouldNotBeCreatedWithoutAuthorization() {
        CreateOrderRequest request = new CreateOrderRequest(validIngredients);

        Response response = OrderApi.createOrder(request, null);

        checkIfUnauthorized(response);
    }

    @Step("Проверка статуса и сообщения при отсутствии авторизации")
    public void checkIfUnauthorized(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Проверка создания заказа с ингредиентами")
    @Description("Тест создает заказ с ингредиентами и проверяет, что заказ успешно создан")
    public void orderShouldBeCreatedWithIngredients() {
        CreateOrderRequest request = new CreateOrderRequest(validIngredients);

        Response response = OrderApi.createOrder(request, accessToken);

        checkIfOrderCreated(response);
    }

    @Test
    @DisplayName("Проверка создания заказа без ингредиентов")
    @Description("Тест проверяет, что при создании заказа без ингредиентов сервер возвращает 400 Bad Request")
    public void orderShouldNotBeCreatedWithoutIngredients() {
        CreateOrderRequest request = new CreateOrderRequest(null);

        Response response = OrderApi.createOrder(request, accessToken);

        checkIfIdsMustBeProvided(response);
    }

    @Step("Проверка статуса и сообщения при отсутствии хешей ингредиентов")
    public void checkIfIdsMustBeProvided(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Проверка создания заказа с неверным хешем ингредиентов")
    @Description("Тест проверяет, что при создании заказа с неверным хешем ингредиентов сервер возвращает 500 Internal Server Error")
    public void orderShouldNotBeCreatedWithInvalidIngredientsHash() {
        CreateOrderRequest request = new CreateOrderRequest(new String[]{"invalid-hash-1", "invalid-hash-2"});

        Response response = OrderApi.createOrder(request, accessToken);

        checkIfHashNotValid(response);
    }

    @Step("Проверка статуса и сообщения при неправильном хеше ингредиента")
    public void checkIfHashNotValid(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}