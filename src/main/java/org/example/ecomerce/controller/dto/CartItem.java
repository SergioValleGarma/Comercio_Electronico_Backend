package org.example.ecomerce.controller.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartItem {

    @EqualsAndHashCode.Include
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;

    public BigDecimal getSubtotal() {
        return productPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
