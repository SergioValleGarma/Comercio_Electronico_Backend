package org.example.ecomerce.service.impl;

import org.example.ecomerce.controller.dto.ProductFilter;
import org.example.ecomerce.controller.spec.ProductSpecs;
import org.example.ecomerce.model.Product;
import org.example.ecomerce.repository.ProductRepository;
import org.example.ecomerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> search(ProductFilter filter, Pageable pageable) {
        boolean noFilters =
                (filter == null) ||
                        (isBlank(filter.getQ()) &&
                                filter.getCategory() == null &&
                                filter.getPriceMin() == null &&
                                filter.getPriceMax() == null &&
                                filter.getInStock() == null);

        if (noFilters) {
            return productRepository.findAll(pageable);
        }

        Specification<Product> spec = ProductSpecs.fromFilter(filter);
        return productRepository.findAll(spec, pageable);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}