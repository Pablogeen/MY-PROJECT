package com.REY.Me.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordDTO {

    @NotNull
    @NotBlank
    private String currentPassword;
    @NotNull
    @NotBlank
    private String newPassword;
}
