package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.UserOrderStatusDTO;
import com.example.ecommerce.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get user order statistics", description = "Returns the number of orders and the total amount spent by the user with the given ID")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/user/{userId}/status")
    public ResponseEntity<UserOrderStatusDTO> getUserStats(@PathVariable Long userId, Authentication auth) {
        return ResponseEntity.ok(orderService.getUserStats(userId));
    }


    @Operation(summary = "Get all orders of the current user", description = "Returns a list of orders belonging to the authenticated user")
    @ApiResponse(responseCode = "200", description = "Order list retrieved successfully")
    @ApiResponse(responseCode = "401", description = "User is not authenticated")
    @GetMapping
    public List<OrderDTO> getUserOrders(Authentication auth) {
        return orderService.getUserOrders(auth.getName());
    }

    @Operation(summary = "Create a new order", description = "Creates a new order for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Order successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "401", description = "User is not authenticated")
    @PostMapping
    public OrderDTO placeOrder(@Valid @RequestBody OrderDTO dto, Authentication auth) {
        return orderService.placeOrder(dto, auth.getName());
    }
}
