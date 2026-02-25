package ru.stellarburgers.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserRequest {
    private String email;
    private String password;
    private String name;
}