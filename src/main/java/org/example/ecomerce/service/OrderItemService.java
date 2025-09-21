package org.example.ecomerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.model.OrderItem;
import org.example.ecomerce.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    // ========================================
    // CONSULTAS BÁSICAS
    // ========================================

    public Optional<OrderItem> findById(Long id) {
        return orderItemRepository.findById(id);
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<OrderItem> findByOrderIdWithProduct(Long orderId) {
        return orderItemRepository.findByOrderIdWithProduct(orderId);
    }

    public List<OrderItem> findByProductId(Long productId) {
        return orderItemRepository.findByProductId(productId);
    }

    // ========================================
    // ESTADÍSTICAS DE PRODUCTOS
    // ========================================

    public Long getTotalQuantitySoldByProduct(Long productId) {
        Long total = orderItemRepository.getTotalQuantitySoldByProduct(productId);
        return total != null ? total : 0L;
    }

    public List<Object[]> getTopSellingProducts() {
        return orderItemRepository.getTopSellingProducts();
    }

    public long countOrdersByProduct(Long productId) {
        return orderItemRepository.countByProductId(productId);
    }

    public List<OrderItem> findByUserAndProduct(Long userId, Long productId) {
        return orderItemRepository.findByUserAndProduct(userId, productId);
    }
}
