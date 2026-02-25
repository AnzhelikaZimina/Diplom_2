package ru.stellarburgers.entities;

import lombok.Data;

@Data
public class LoginUserResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private User user;
}