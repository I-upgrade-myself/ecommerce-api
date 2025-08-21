package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "product.id", target = "productId")
    CartItemDTO toDTO(CartItem cartItem);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    CartItem toEntity(CartItemDTO dto);
}

