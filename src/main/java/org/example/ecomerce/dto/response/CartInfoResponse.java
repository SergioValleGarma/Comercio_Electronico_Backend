package org.example.ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.ecomerce.controller.dto.CartItem;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartInfoResponse {
    private List<CartItem> cartItems;
    private BigDecimal total;
    private int itemCount;
    private String message;
}