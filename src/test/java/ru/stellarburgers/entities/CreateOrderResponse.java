package ru.stellarburgers.entities;

import lombok.Data;

@Data
public class CreateOrderResponse {
    private boolean success;
    private String message;
    private int number;
    private String name;
}