package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.dto.UserOrderStatusDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
        

    public UserOrderStatusDTO getUserStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        List<Order> orders = orderRepository.findByUserId(userId);
        int count = orders.size();
        BigDecimal total = orders.stream()
            .map(Order::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new UserOrderStatusDTO(userId, count, total);
    }




    public List<OrderDTO> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

        return orderRepository.findByUserEmail(email).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }
 
    public OrderDTO placeOrder(OrderDTO dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderMapper.toEntity(dto);
        order.setUser(user);

        Set<Product> products = dto.getProductIds().stream()
                .map(id -> productRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id)))
                .collect(Collectors.toSet());

        order.setProducts(products);

        return orderMapper.toDTO(orderRepository.save(order));
    }
}
