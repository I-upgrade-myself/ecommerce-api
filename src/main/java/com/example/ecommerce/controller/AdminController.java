package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Operation(
    summary = "Admin panel",
    description = "Accessible only to users with the ADMIN role"
    )

    @ApiResponse(responseCode = "200", description = "Successful access to the admin panel")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome, admin");
    }
}
