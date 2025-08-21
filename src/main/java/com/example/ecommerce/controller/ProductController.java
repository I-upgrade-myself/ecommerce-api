package com.example.ecommerce.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.dto.ProductDTO;
import com.example.ecommerce.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // @GetMapping
    // public List<ProductDTO> getAll() {
    //     return productService.getAll();
    // }
    @Operation(summary = "Get all products", description = "Returns a paginated list of all products with sorting")
    @ApiResponse(responseCode = "200", description = "Product list retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
    @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable
    ) {
    Page<ProductDTO> products = productService.getAllProducts(pageable);
    return ResponseEntity.ok(products);
}

    @Operation(summary = "Search products", description = "Filters products by name, minimum, and maximum price")
    @ApiResponse(responseCode = "200", description = "Search executed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid query parameters")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProduct(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "min", required = false) BigDecimal min,
        @RequestParam(value = "max", required = false) BigDecimal max,
        @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable
        ) {
        Page<ProductDTO> products = productService.search(name, min, max, pageable);
        return ResponseEntity.ok(products);
    }
    

    @Operation(summary = "Create product", description = "Creates a new product based on the provided data")
    @ApiResponse(responseCode = "200", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Error in input data")
    @PostMapping
    public ProductDTO create(@Valid @RequestBody ProductDTO dto) {
        return productService.create(dto);
    }

    @Operation(summary = "Update product", description = "Updates a product by its ID")
    @ApiResponse(responseCode = "200", description = "Product successfully updated")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "400", description = "Invalid data")
    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        return productService.update(id, dto);
    }

    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    @ApiResponse(responseCode = "200", description = "Product successfully deleted")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
