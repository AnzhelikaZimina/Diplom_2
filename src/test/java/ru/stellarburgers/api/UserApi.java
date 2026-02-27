package ru.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.stellarburgers.entities.CreateUserRequest;
import ru.stellarburgers.entities.LoginUserRequest;
import ru.stellarburgers.entities.LoginUserResponse;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class UserApi {
    private static final List<CreateUserRequest> createdUsers = new ArrayList<>();

    public static Response createUser(String email, String password, String name) {
        CreateUserRequest request = new CreateUserRequest(email, password, name);
        return createUser(request);
    }

    @Step("Создаём нового пользователя")
    public static Response createUser(CreateUserRequest request) {
        Response response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/auth/register");

        createdUsers.add(request);
        return response;
    }

    @Step("Логинимся под пользователем")
    public static Response loginUser(String email, String password) {
        LoginUserRequest request = new LoginUserRequest(email, password);

        return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/auth/login");
    }

    @Step("Удаляем пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user");
    }

    public static void deleteAllCreatedUsers() {
        for (CreateUserRequest user : createdUsers) {
            Response loginResponse = loginUser(user.getEmail(), user.getPassword());
            LoginUserResponse loginData = loginResponse.as(LoginUserResponse.class);
            if (loginData != null && loginData.getAccessToken() != null) {
                deleteUser(loginData.getAccessToken());
            }
        }
        createdUsers.clear();
    }
}