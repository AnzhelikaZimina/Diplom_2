package ru.stellarburgers.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.stellarburgers.entities.Ingredient;
import ru.stellarburgers.entities.IngredientsResponse;

import static io.restassured.RestAssured.given;

public class IngredientsApi {

    @Step("Получаем список ингредиентов")
    public static Response getIngredients() {
        return given()
                .when()
                .get("/api/ingredients");
    }

    public static Ingredient[] getIngredientsData() {
        Response response = getIngredients();
        IngredientsResponse ingredientsResponse = response.as(IngredientsResponse.class);
        return ingredientsResponse.getData();
    }

    public static String[] getRandomIngredientsIds(int count) {
        Ingredient[] ingredients = getIngredientsData();
        String[] ids = new String[count];
        for (int i = 0; i < Math.min(count, ingredients.length); i++) {
            ids[i] = ingredients[i].get_id();
        }
        return ids;
    }
}