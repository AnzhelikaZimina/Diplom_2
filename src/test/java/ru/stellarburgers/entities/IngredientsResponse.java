package ru.stellarburgers.entities;

import lombok.Data;

@Data
public class IngredientsResponse {
    private boolean success;
    private Ingredient[] data;
}