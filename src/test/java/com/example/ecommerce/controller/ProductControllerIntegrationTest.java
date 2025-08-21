package com.example.ecommerce.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtTokenProvider;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIntegrationTest {
    @Autowired 
    private MockMvc mockMvc;

    @Autowired 
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String jwtToken;
    private Long productId;
    

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role roleUser = roleRepository.save(Role.builder().name("ROLE_USER").build());

        User user = userRepository.save(User.builder()
                .email("cartuser@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("John")
                .lastName("Doe") 
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .roles(Set.of(roleUser))
                .build());

        Set<SimpleGrantedAuthority> authorities = Set.of(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        this.jwtToken = jwtTokenProvider.generateToken(
            new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
                ),
                null,
                authorities
            )
        );

        Product product = productRepository.save(Product.builder()
        .name("Phone")
        .price(BigDecimal.valueOf(299.99))
        .description("Smartphone")
        .build());

        this.productId = product.getId();
    }


    @Test
    void getAllProducts_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + jwtToken)
                .param("size", "5")
                .param("sort", "price,desc"))
            .andExpect(status().isOk());
    }

    @Test
    void searchProductsByName_ReturnsFiltered() throws Exception {
        mockMvc.perform(get("/api/products/search")
                .header("Authorization", "Bearer " + jwtToken)
                .param("name", "Phone"))
            .andExpect(status().isOk());
    }

    @Test
    void searchWithInvalidMinPrice_Returns400() throws Exception {
        mockMvc.perform(get("/api/products/search")
                .header("Authorization", "Bearer " + jwtToken)
                .param("min", "abc"))
            .andExpect(status().isBadRequest());
    }

}
