package com.rey.me.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class NewsLetterResponseDto {

    private String title;
    private String content;
    private Instant timePosted;
    private String username;

}
