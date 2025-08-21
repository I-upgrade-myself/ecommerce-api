package com.example.ecommerce.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("SELECT p FROM Product p WHERE (:name IS NULL OR p.name LIKE %:name%) AND (:min IS NULL OR p.price >= :min) AND (:max IS NULL OR p.price <= :max)")
    Page<Product> findByFilters(@Param("name") String name,
                                @Param("min") BigDecimal min,
                                @Param("max") BigDecimal max,
                                Pageable pageable); 
}