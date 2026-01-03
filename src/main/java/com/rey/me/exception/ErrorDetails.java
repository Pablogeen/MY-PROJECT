package com.rey.me.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorDetails {

    private String message;
    private String errorcode;
    private String details;
    private LocalDateTime timestamp;

}
