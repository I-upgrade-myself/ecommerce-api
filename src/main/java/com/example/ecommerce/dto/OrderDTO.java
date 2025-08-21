package com.example.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrderDTO {
        private Long id;
    private LocalDateTime createdAt;
    private Long userId;

    @NotEmpty
    private Set<Long> productIds;
}
