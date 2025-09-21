package org.example.ecomerce.controller.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderStatistics {
    private long totalOrders;
    private long weeklyOrders;
    private long monthlyOrders;
    private long pendingOrders;
    private BigDecimal weeklyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal averageOrderValue;
}
