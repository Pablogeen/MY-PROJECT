package com.rey.me.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewsLetterRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    @NotBlank(message = "Content is required")
    @Size(min = 5, message = "Content must be at least 5 characters")
    private String content;


}
