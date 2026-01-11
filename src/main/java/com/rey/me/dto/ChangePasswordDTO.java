package com.rey.me.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    @NotNull(message = "Current Password can't be null")
    @NotBlank(message = "Password Expected")
    private String currentPassword;
    @NotNull(message = "New Password can't be null")
    @NotBlank(message = "Password Expected")
    private String newPassword;
}
