package org.example.ecomerce.service;

import org.example.ecomerce.controller.dto.ProductFilter;
import org.example.ecomerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> search(ProductFilter filter, Pageable pageable);
}
