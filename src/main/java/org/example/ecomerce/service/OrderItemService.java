package org.example.ecomerce.service;

import org.example.ecomerce.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    Optional<OrderItem> findById(Long id);
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByOrderIdWithProduct(Long orderId);
    List<OrderItem> findByProductId(Long productId);
    Long getTotalQuantitySoldByProduct(Long productId);
    List<Object[]> getTopSellingProducts();
    long countOrdersByProduct(Long productId);
    List<OrderItem> findByUserAndProduct(Long userId, Long productId);
}
