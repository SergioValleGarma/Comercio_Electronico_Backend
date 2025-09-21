package org.example.ecomerce.controller.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter {
  private String q;
  private Long category;
  private BigDecimal priceMin;
  private BigDecimal priceMax;
  private Boolean inStock;
  private String sort;
}
