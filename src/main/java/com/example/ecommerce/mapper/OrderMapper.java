package com.example.ecommerce.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ecommerce.dto.OrderDTO;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "products", target = "productIds")
    OrderDTO toDTO(Order order);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "products", ignore = true)
    Order toEntity(OrderDTO dto);

    // Метод для перетворення Set<Product> в Set<Long> (їх id)
    default Set<Long> map(Set<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
    }
}
