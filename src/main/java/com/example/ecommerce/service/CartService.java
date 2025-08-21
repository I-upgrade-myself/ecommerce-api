package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.exception.UnauthorizedAccessException;
import com.example.ecommerce.mapper.CartItemMapper;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;

    public List<CartItemDTO> getCartItems(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

        return cartItemRepository.findByUserEmail(email).stream()
                .map(cartItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CartItemDTO addItem(CartItemDTO dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));


        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + dto.getProductId() + " not found"));

        CartItem cartItem = cartItemMapper.toEntity(dto);
        cartItem.setUser(user);
        cartItem.setProduct(product);

        return cartItemMapper.toDTO(cartItemRepository.save(cartItem));
    }

    public void removeItem(Long id, String email) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item with ID " + id + " not found"));

        if (!item.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Access denied to cart item");
        }

        cartItemRepository.deleteById(id);
    }
}
