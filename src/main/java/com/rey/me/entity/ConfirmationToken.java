package com.rey.me.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime created;
    private LocalDateTime expires;
    private LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    public ConfirmationToken(String token, LocalDateTime created, LocalDateTime expires, User user){
        this.token = token;
        this.created= created;
        this.expires=expires;
        this.user=user;
    }

}
