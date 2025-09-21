package org.example.ecomerce.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductFilterRequest {
    private String q;
    private Long category;
    private BigDecimal priceMin;  // Cambiado de Double a BigDecimal
    private BigDecimal priceMax;  // Cambiado de Double a BigDecimal
    private Boolean inStock;
    private String sort;
    private Integer page = 0;
    private Integer size = 12;
}