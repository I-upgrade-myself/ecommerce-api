package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // public List<ProductDTO> getAll() {
    //     return productRepository.findAll().stream()
    //             .map(productMapper::toDTO)
    //             .collect(Collectors.toList());
    // }

    
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
    return productRepository.findAll(pageable)
        .map(productMapper::toDTO);
    }

        public Page<ProductDTO> search(String name, BigDecimal min, BigDecimal max, Pageable pageable) {
        return productRepository.findByFilters(name, min, max, pageable)
                .map(productMapper::toDTO);
    }

    public ProductDTO create(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        return productMapper.toDTO(productRepository.save(product));
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"+ id));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        return productMapper.toDTO(productRepository.save(product));
    }

    public void delete(Long id) {
            if (!productRepository.existsById(id)) {
        throw new ResourceNotFoundException("Product with ID " + id + " does not exist");
    }
    productRepository.deleteById(id);
    }
}