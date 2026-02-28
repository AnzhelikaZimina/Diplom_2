package ru.stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import ru.stellarburgers.api.UserApi;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserLoginTest extends BaseTest {

    private final String email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@test.com";
    private final String password = RandomStringUtils.randomAlphabetic(10);

    @Before
    public void setUp() {
        String name = "TestUser";
        UserApi.createUser(email, password, name);
    }

    @Test
    @DisplayName("Проверка успешного входа под существующим пользователем")
    @Description("Тест создает пользователя и проверяет, что вход под существующим пользователем возвращает 200 OK")
    public void userShouldLoginSuccessfully() {
        Response response = UserApi.loginUser(email, password);

        checkIfLoginSuccessful(response);
    }

    @Step("Проверка статуса и сообщения при успешном входе")
    private static void checkIfLoginSuccessful(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Проверка что нельзя войти с неверным логином")
    @Description("Тест проверяет, что при попытке входа с неверными данными сервер возвращает 401 Unauthorized")
    public void shouldNotLoginWithInvalidLogin() {
        String email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@test.com";

        Response response = UserApi.loginUser(email, password);

        checkIfEmailOrPasswordAreIncorrect(response);
    }

    @Test
    @DisplayName("Проверка что нельзя войти с неверным паролем")
    @Description("Тест проверяет, что при попытке входа с неверными данными сервер возвращает 401 Unauthorized")
    public void shouldNotLoginWithInvalidPassword() {
        String password = RandomStringUtils.randomAlphabetic(10);

        Response response = UserApi.loginUser(email, password);

        checkIfEmailOrPasswordAreIncorrect(response);
    }

    @Step("Проверка статуса и сообщения при неверном логине или пароле")
    public void checkIfEmailOrPasswordAreIncorrect(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}