package com.rey.me.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class NewsLetterResponseDto implements Serializable {

    private String title;
    private String content;
    private Instant timeStamp;
    private String username;

}
