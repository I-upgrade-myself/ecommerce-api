package com.example.ecommerce.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ecommerce.dto.auth.LoginRequest;
import com.example.ecommerce.dto.auth.RegisterRequest;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {
    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        if(roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(Role.builder().name("ROLE_USER").build());
        }
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("newuser@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testRegisterUser_DuplicateEmail() throws Exception {
        userRepository.save(com.example.ecommerce.entity.User.builder()
                .email("duplicate@example.com")
                .password("encoded") 
                .firstName("Jane")
                .lastName("Smith")
                .roles(Set.of(roleRepository.findByName("ROLE_USER").orElseThrow()))
                .enabled(true)
                .build());

        RegisterRequest duplicateRequest = RegisterRequest.builder()
                .email("duplicate@example.com")
                .password("anotherpass")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isBadRequest());          
    }

    @Test 
    void testLoginUser_Success() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
            .email("loginuser@example.com")
            .password("password123")
            .firstName("Jane")
            .lastName("Doe")
            .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk());   


        LoginRequest loginRequest = LoginRequest.builder()
                .email("loginuser@example.com")
                .password("password123")
                .build();
    
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("loginuser@example.com"));

}


    @Test 
    void testLogin_Fail() throws Exception {
        LoginRequest loginRequest = new LoginRequest("invalid@example.com", "wrongpass");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
    }
}