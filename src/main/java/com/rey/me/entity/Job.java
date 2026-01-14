package com.rey.me.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(nullable = false, length = 100)
    private String title;
    @Column(nullable = false,length = 1000)
    private String description;
    @Column(nullable = false, length = 50)
    private String company;
    @Column(nullable = false, length = 50)
    private String location;
    @Column(nullable = false)
    private List<String> techs;
    @Column(nullable = false)
    private Long salary;
    @Column(nullable = false, length = 20)
    private String category;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
