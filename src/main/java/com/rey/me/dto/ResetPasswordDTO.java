package com.rey.me.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    @NotNull(message = "Email Required")
    @NotBlank(message = "Email Required")
    private String email;
    @NotNull(message = "Token Required")
    @NotBlank(message = "Token Required")
    private String token;
    @NotNull(message = "New Password Required")
    @NotBlank(message = "New Password Required")
    private String NewPassword;
}
