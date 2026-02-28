package ru.stellarburgers.entities;

import lombok.Data;

@Data
public class Ingredient {
    private String _id;
    private String name;
    private String type;
    private int proteins;
    private int fat;
    private int carbohydrates;
    private int calories;
    private int price;
    private String image;
    private String imageMobile;
    private String imageLarge;
}