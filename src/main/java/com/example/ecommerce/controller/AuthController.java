package com.example.ecommerce.controller;

import java.lang.module.ResolutionException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.auth.AuthResponse;
import com.example.ecommerce.dto.auth.LoginRequest;
import com.example.ecommerce.dto.auth.RegisterRequest;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.DuplicateEmailException;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.CustomUserDetailsService;
import com.example.ecommerce.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Operation(summary = "User registration", description = "Register a new user with the USER role")
    @ApiResponse(responseCode = "200", description = "User successfully registered")
    @ApiResponse(responseCode = "400", description = "Email already in use or invalid data")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registration request received for email: {}", registerRequest.getEmail());

    if (userRepository.existsByEmail(registerRequest.getEmail())) {
        logger.error("Email already in use: {}", registerRequest.getEmail());
        throw new DuplicateEmailException("Email already in use");
    }

    // Знаходимо роль USER
    String requestedRoleName = "ROLE_" + registerRequest.getRole().toUpperCase();

    Role selectedRole = roleRepository.findByName(requestedRoleName)
            .orElseThrow(() -> new RuntimeException("Role not found: " + requestedRoleName));

    // Створення нового користувача
    User user = User.builder()
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .roles(Set.of(selectedRole)) // одразу задаємо роль
            .build();

    userRepository.save(user);

    // Не використовуємо напряму user.getRoles(), а створюємо копію
    Set<Role> rolesCopy = new HashSet<>(user.getRoles());

    List<GrantedAuthority> authorities = rolesCopy.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );



    String token = jwtTokenProvider.generateToken(auth);

    return ResponseEntity.ok(AuthResponse.builder()
            .token(token)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(rolesCopy.stream().map(Role::getName).collect(Collectors.toSet()))
            .build());

    }


    

    @Operation(summary = "User authentication", description = "User login and JWT token retrieval")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid username or password")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    logger.info("Login request received for email: {}", loginRequest.getEmail());

    try {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new ResolutionException("User not found"));

        Set<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

        logger.info("User authenticated successfully: {}", loginRequest.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
            .token(token)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(roles)
            .build();

        return ResponseEntity.ok(authResponse);

    } catch (BadCredentialsException ex) {
        logger.warn("Login failed for email: {}", loginRequest.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }
}

}
