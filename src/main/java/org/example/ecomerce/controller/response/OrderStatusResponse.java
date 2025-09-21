package org.example.ecomerce.controller.response;

import lombok.*;
import org.example.ecomerce.model.OrderStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderStatusResponse {
    private boolean success;
    private String message;
    private OrderStatus status;
    private String statusDisplayName;
}
