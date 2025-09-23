package org.example.ecomerce.service;

import jakarta.servlet.http.HttpSession;
import org.example.ecomerce.controller.dto.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    void addItem(HttpSession session, Long productId, Integer quantity);
    void updateQuantity(HttpSession session, Long productId, Integer quantity);
    void removeItem(HttpSession session, Long productId);
    void clearCart(HttpSession session);
    List<CartItem> getCartItems(HttpSession session);
    BigDecimal getCartTotal(HttpSession session);
    int getCartItemCount(HttpSession session);
    boolean isEmpty(HttpSession session);

}
