package ru.stellarburgers.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.stellarburgers.api.UserApi;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class CreateUserTest extends BaseTest {

    @Test
    @DisplayName("Проверка успешного создания уникального пользователя")
    @Description("Тест создаёт пользователя со всеми уникальными параметрами и проверяет, что сервер возвращает 201 Created и поле success в теле ответа имеет значение true")
    public void userShouldBeCreatedSuccessfully() {
        String email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@test.com";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);

        Response response = UserApi.createUser(email, password, name);

        checkIfUserCreatedSuccessfully(response, email, name);
    }

    @Step("Проверка кода ответа и сообщения после успешного создания")
    public void checkIfUserCreatedSuccessfully(Response response, String email, String name) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Проверка что нельзя создать пользователя, который уже зарегистрирован")
    @Description("Тест создаёт пользователя с уникальными параметрами, затем пытается создать пользователя с тем же email и проверяет, что сервер возвращает 403 Forbidden и сообщение 'User already exists'")
    public void shouldNotCreateDuplicateUser() {
        String email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@test.com";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);

        UserApi.createUser(email, password, name);
        Response response = UserApi.createUser(email, password, name);

        checkIfUserAlreadyExists(response);
    }

    @Step("Проверка статуса и сообщения при уже существующем пользователе")
    public void checkIfUserAlreadyExists(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Нельзя создать пользователя без email")
    @Description("Тест проверяет, что при попытке создать пользователя без поля email сервер возвращает 403 Forbidden и сообщение об ошибке в теле ответа")
    public void shouldNotCreateUserWithoutEmail() {
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);

        Response response = UserApi.createUser(null, password, name);

        checkIfFieldsRequired(response);
    }

    @Step("Проверка статуса и сообщения при отсутствующем поле")
    public void checkIfFieldsRequired(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Нельзя создать пользователя без пароля")
    @Description("Тест проверяет, что при попытке создать пользователя без поля password сервер возвращает 403 Forbidden и сообщение об ошибке в теле ответа")
    public void shouldNotCreateUserWithoutPassword() {
        String email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@test.com";
        String name = RandomStringUtils.randomAlphabetic(10);

        Response response = UserApi.createUser(email, null, name);

        checkIfFieldsRequired(response);
    }

    @Test
    @DisplayName("Нельзя создать пользователя без имени")
    @Description("Тест проверяет, что при попытке создать пользователя без поля name сервер возвращает 403 Forbidden и сообщение об ошибке в теле ответа")
    public void shouldNotCreateUserWithoutName() {
        String email = RandomStringUtils.randomAlphabetic(10) + "@test.com";
        String password = RandomStringUtils.randomAlphabetic(10);

        Response response = UserApi.createUser(email, password, null);

        checkIfFieldsRequired(response);
    }
}