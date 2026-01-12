package com.rey.me.dto;

import com.rey.me.entity.ROLE;
import lombok.Data;

@Data
public class UserResponseDto {

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private ROLE role;
    private boolean enabled;
}
