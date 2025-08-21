package com.example.ecommerce.controller;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtTokenProvider;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = roleRepository.save(Role.builder().name("ROLE_USER").build());
        Role adminRole = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());

        User user = userRepository.save(User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("pass"))
                .enabled(true)
                .roles(Set.of(userRole))
                .build());

        User admin = userRepository.save(User.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("pass"))
                .enabled(true)
                .roles(Set.of(adminRole))
                .build());

        List<SimpleGrantedAuthority> userAuthorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        userToken = jwtTokenProvider.generateToken(
            new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    userAuthorities
                ),
                null,
                userAuthorities
            )
        );

        List<SimpleGrantedAuthority> adminAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        adminToken = jwtTokenProvider.generateToken(
            new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                    admin.getEmail(),
                    admin.getPassword(),
                    adminAuthorities
                ),
                null,
                adminAuthorities
            )
        );
        
    }

    @Test
    void profileAsUser_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + userToken)) 
            .andExpect(status().isOk());
    }

    @Test
    void profileAsAdmin_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void profileUnauthorized_Returns401() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
            .andExpect(status().isUnauthorized());
    }

}
