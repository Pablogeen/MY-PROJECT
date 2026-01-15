package com.rey.me.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

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
         @Column(columnDefinition = "LONGTEXT")
        private String content;
        private Instant timeStamp = Instant.now();
        @ManyToOne
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        private User user;

}
