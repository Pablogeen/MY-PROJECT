package com.rey.me.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewsLetterDTO {

    @NotBlank(message = "Required")
    private String text;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

}
