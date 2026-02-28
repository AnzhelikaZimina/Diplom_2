package ru.stellarburgers.tests;

import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.stellarburgers.api.UserApi;

public class BaseTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
    }

    @AfterClass
    public static void afterClass() throws Exception {
        UserApi.deleteAllCreatedUsers();
    }
}