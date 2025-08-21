package com.example.ecommerce.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderStatusDTO {
    public Long userId;
    private int orderCount;
    private BigDecimal totalSpent;
}
