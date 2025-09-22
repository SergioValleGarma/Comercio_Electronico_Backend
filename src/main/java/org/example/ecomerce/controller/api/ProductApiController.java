package org.example.ecomerce.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.ecomerce.controller.dto.ProductFilter;
import org.example.ecomerce.dto.request.ProductFilterRequest;
import org.example.ecomerce.dto.response.ApiResponse;
import org.example.ecomerce.dto.response.ProductResponse;
import org.example.ecomerce.model.Product;
import org.example.ecomerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getProducts(ProductFilterRequest filterRequest) {
        try {
            ProductFilter filter = convertToFilter(filterRequest);
            Pageable pageable = PageRequest.of(
                filterRequest.getPage() != null ? filterRequest.getPage() : 0, 
                filterRequest.getSize() != null ? filterRequest.getSize() : 12,
                getSort(filterRequest.getSort())
            );
            
            Page<Product> productsPage = productService.search(filter, pageable);
            Page<ProductResponse> responsePage = productsPage.map(this::convertToResponse);
            
            return ApiResponse.success(responsePage);
        } catch (Exception e) {
            return ApiResponse.error("Error al obtener productos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        try {
            // Necesitarías implementar este método en ProductService
            // Product product = productService.findById(id).orElseThrow(...);
            return ApiResponse.error("Endpoint no implementado aún");
        } catch (Exception e) {
            return ApiResponse.error("Error al obtener producto: " + e.getMessage());
        }
    }

    private ProductFilter convertToFilter(ProductFilterRequest request) {
        ProductFilter filter = new ProductFilter();
        filter.setQ(request.getQ());
        filter.setCategory(request.getCategory());
        filter.setPriceMin(request.getPriceMin());
        filter.setPriceMax(request.getPriceMax());
        filter.setInStock(request.getInStock());
        filter.setSort(request.getSort());
        return filter;
    }

    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getImageUrl(),
            product.getCategory() != null ? product.getCategory().getId() : null,
            product.getCategory() != null ? product.getCategory().getName() : null
        );
    }

    private Sort getSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.unsorted();
        return switch (sort) {
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "nameAsc" -> Sort.by(Sort.Direction.ASC, "name");
            case "nameDesc" -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.unsorted();
        };
    }
}