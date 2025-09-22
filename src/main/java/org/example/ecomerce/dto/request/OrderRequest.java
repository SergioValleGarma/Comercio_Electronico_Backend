package org.example.ecomerce.dto.request;

import lombok.Data;
import org.example.ecomerce.controller.dto.CartItem;

import java.util.List;

@Data
public class OrderRequest {
    private List<CartItem> cartItems;
    private String shippingAddress;
    private String phoneNumber;
    private String notes;
}
