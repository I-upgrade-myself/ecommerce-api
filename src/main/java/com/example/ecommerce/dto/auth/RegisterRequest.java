package com.example.ecommerce.dto.auth;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String role;
}
