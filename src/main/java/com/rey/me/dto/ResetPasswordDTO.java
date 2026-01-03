package com.rey.me.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResetPasswordDTO {

    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String token;
    @NotNull
    @NotBlank
    private String NewPassword;
}
