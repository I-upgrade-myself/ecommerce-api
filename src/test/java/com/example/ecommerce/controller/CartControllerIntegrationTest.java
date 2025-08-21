package com.example.ecommerce.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.example.ecommerce.entity.User;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CartControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;
    private Long productId;

     @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
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

        Product product = productRepository.save(Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(19.99))
                .description("Test description")
                .build());

        this.productId = product.getId();

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
    }

    @Test
    void testGetCartItems() throws Exception {
        // Спочатку додаємо товар
        CartItemDTO dto = CartItemDTO.builder()
                .productId(productId)
                .quantity(2)
                .build();

        mockMvc.perform(post("/api/cart")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Перевірка GET
        mockMvc.perform(get("/api/cart")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(productId))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    void testRemoveItemFromCart() throws Exception {
        // Додаємо товар
        CartItemDTO dto = CartItemDTO.builder()
                .productId(productId)
                .quantity(1)
                .build();

        String response = mockMvc.perform(post("/api/cart")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long cartItemId = objectMapper.readTree(response).get("id").asLong();

        // Тестуємо DELETE
        mockMvc.perform(delete("/api/cart/" + cartItemId)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}
