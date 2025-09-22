package org.example.ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.ecomerce.model.OrderStatus;

@Data
@AllArgsConstructor
public class OrderStatusResponse {
    private OrderStatus status;
    private String statusDisplayName;
}