package org.example.ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.ecomerce.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private Instant orderDate;
    private String shippingAddress;
    private String phoneNumber;
    private String notes;
    private List<OrderItemResponse> orderItems;
}