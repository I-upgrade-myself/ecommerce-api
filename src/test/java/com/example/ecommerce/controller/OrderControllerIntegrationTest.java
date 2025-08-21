package com.example.ecommerce.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;

import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {
        @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setup() { 
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role roleUser = roleRepository.save(Role.builder().name("ROLE_USER").build());

        testUser = userRepository.save(User.builder()
                .email("orderuser@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("John")
                .lastName("Doe")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .roles(Set.of(roleUser))
                .build());
    }

    @Test
    void getUserOrders_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserStats_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/orders/user/" + testUser.getId() + "/status"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void placeOrder_Unauthorized() throws Exception {
        OrderDTO dto = new OrderDTO(); 
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
