package com.rey.me.dto;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class NewsLetterDTO {

    private String text;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

}
