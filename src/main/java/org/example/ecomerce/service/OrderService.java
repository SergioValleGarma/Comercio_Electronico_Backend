package org.example.ecomerce.service;

import org.example.ecomerce.controller.dto.CartItem;
import org.example.ecomerce.controller.dto.OrderStatistics;
import org.example.ecomerce.model.Order;
import org.example.ecomerce.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrderFromCart(Long userId, List<CartItem> cartItems, String shippingAddress, String phoneNumber, String notes);
    Optional<Order> findById(Long id);
    Optional<Order> findByIdWithItems(Long id);
    List<Order> findByUserId(Long userId);
    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findByUserIdWithItems(Long userId);
    List<Order> findByStatus(OrderStatus status);
    Page<Order> findAll(Pageable pageable);
    Order updateOrderStatus(Long orderId, OrderStatus newStatus);
    Order cancelOrder(Long orderId);
    long countOrdersByUser(Long userId);
    BigDecimal getTotalSpentByUser(Long userId);
    BigDecimal getAverageOrderValue();
    OrderStatistics getOrderStatistics();
}