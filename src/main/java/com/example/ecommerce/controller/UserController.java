package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/user")
public class UserController { 
    
    @Operation(summary = "Get user profile", description = "This endpoint allows retrieving user profile information, available for USER and ADMIN roles.")
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access to profile denied (insufficient permissions)")
    @ApiResponse(responseCode = "401", description = "User is not authenticated")
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> profile() {
        return ResponseEntity.ok("User profile page");
    }
}
