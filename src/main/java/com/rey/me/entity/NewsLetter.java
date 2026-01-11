package com.rey.me.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NewsLetter {

         @Id
         @GeneratedValue(strategy=GenerationType.IDENTITY)
        private Long id;
         @Lob
        private String text;
        private LocalDateTime timePosted;

        private String imageName;
        private String imageType;
        @Lob
        private byte[] imageData;
        @ManyToOne
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        private User user;

}
