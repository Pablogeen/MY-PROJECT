package com.REY.Me.DTO;

import jakarta.persistence.Lob;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsLetterDTO {

    private String text;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

}
