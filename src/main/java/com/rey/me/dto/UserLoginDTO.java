package com.rey.me.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotBlank(message = "Username Required")
    @NotNull(message = "Username Required")
    private String username;
    @NotBlank(message = "Password Required")
    @NotNull(message = "Password Required")
    private String password;
}
