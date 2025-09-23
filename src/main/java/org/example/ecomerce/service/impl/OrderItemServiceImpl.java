package org.example.ecomerce.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.model.OrderItem;
import org.example.ecomerce.repository.OrderItemRepository;
import org.example.ecomerce.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public Optional<OrderItem> findById(Long id) {
        return orderItemRepository.findById(id);
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderItem> findByOrderIdWithProduct(Long orderId) {
        return orderItemRepository.findByOrderIdWithProduct(orderId);
    }

    @Override
    public List<OrderItem> findByProductId(Long productId) {
        return orderItemRepository.findByProductId(productId);
    }

    @Override
    public Long getTotalQuantitySoldByProduct(Long productId) {
        Long total = orderItemRepository.getTotalQuantitySoldByProduct(productId);
        return total != null ? total : 0L;
    }

    @Override
    public List<Object[]> getTopSellingProducts() {
        return orderItemRepository.getTopSellingProducts();
    }

    @Override
    public long countOrdersByProduct(Long productId) {
        return orderItemRepository.countByProductId(productId);
    }

    @Override
    public List<OrderItem> findByUserAndProduct(Long userId, Long productId) {
        return orderItemRepository.findByUserAndProduct(userId, productId);
    }
}
