package com.example.ecommerce.dto.auth;

import java.util.Set;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> role;
}