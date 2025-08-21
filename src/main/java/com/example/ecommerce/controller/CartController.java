package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    
    @Operation(summary = "Get all items in the cart", description = "Returns the list of items in the current user's cart")
    @ApiResponse(responseCode = "200", description = "Cart items retrieved successfully")
    @ApiResponse(responseCode = "401", description = "User is not authenticated")
    @GetMapping
    public List<CartItemDTO> getCartItems(Authentication auth) {
        return cartService.getCartItems(auth.getName());
    }

    @Operation(summary = "Add item to cart", description = "Adds an item to the cart of the authenticated user")
    @ApiResponse(responseCode = "200", description = "Item successfully added")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @ApiResponse(responseCode = "401", description = "User is not authenticated")
    @PostMapping
    public CartItemDTO addItem(@Valid @RequestBody CartItemDTO dto, Authentication auth) {
        return cartService.addItem(dto, auth.getName());
    }

    @Operation(summary = "Remove item from cart", description = "Removes an item from the cart by its ID")
    @ApiResponse(responseCode = "200", description = "Item successfully removed")
    @ApiResponse(responseCode = "404", description = "Item not found")
    @ApiResponse(responseCode = "401", description = "User is not authenticated")
    @DeleteMapping("/{id}")
    public void removeItem(@PathVariable Long id, Authentication auth) {
        cartService.removeItem(id, auth.getName());
    }
}