package com.rey.me.dto;

import java.io.Serializable;
import java.util.List;

public record PageResponse<T extends Serializable>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages
)implements Serializable {}
