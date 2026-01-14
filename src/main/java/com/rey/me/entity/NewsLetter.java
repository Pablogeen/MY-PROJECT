package com.rey.me.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
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
         @Column(length = 200)
         private String title;
         @Lob
        private String content;
        private Instant timePosted = Instant.now();
        @ManyToOne
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        private User user;

}
