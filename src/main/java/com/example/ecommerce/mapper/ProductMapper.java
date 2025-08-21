package com.example.ecommerce.mapper;

import org.mapstruct.Mapper;


import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductDTO toDTO(Product product);
    
    Product toEntity(ProductDTO dto);
}
