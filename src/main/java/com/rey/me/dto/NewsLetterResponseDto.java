package com.rey.me.dto;

import com.rey.me.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
public class NewsLetterResponseDto {

    private String title;
    private String content;
    private Instant timePosted = Instant.now();
    private User user;

}
