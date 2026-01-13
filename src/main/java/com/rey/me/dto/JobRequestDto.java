package com.rey.me.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class JobRequestDto {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String company;

    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;

    @NotEmpty(message = "At least one technology is required")
    @Size(min = 1, max = 20, message = "Maximum 20 technologies allowed")
    private List<@NotBlank(message = "Technology name cannot be blank") String> techs;

    @NotNull(message = "Salary is required")
    @Min(value = 0, message = "Salary must be greater than or equal to 0")
    @Max(value = 10000000, message = "Salary must be less than or equal to 10,000,000")
    private Long salary;

    @NotBlank(message = "Category is required")
    private String category;

}
