package com.rey.me.dto;

import com.rey.me.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JobResponseDto implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String company;
    private String location;
    private List<String> techs;
    private Long salary;
    private String category;
}
