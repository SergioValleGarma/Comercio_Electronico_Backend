package org.example.ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserOrderStats {
    private long totalOrders;
    private BigDecimal totalSpent;
}