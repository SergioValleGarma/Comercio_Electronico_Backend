package org.example.ecomerce.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CheckoutSummaryResponse {
    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal total;
    private int itemCount;
    private boolean isEmpty;
}
