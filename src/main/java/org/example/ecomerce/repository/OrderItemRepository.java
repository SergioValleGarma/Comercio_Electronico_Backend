package org.example.ecomerce.repository;

import org.example.ecomerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ========================================
    // QUERIES POR ORDER
    // ========================================

    List<OrderItem> findByOrderId(Long orderId);

    // ========================================
    // QUERIES POR PRODUCT
    // ========================================

    List<OrderItem> findByProductId(Long productId);

    long countByProductId(Long productId);

    // ========================================
    // QUERIES PERSONALIZADAS
    // ========================================

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderIdWithProduct(@Param("orderId") Long orderId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long getTotalQuantitySoldByProduct(@Param("productId") Long productId);

    @Query("SELECT oi.product.id, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.product.id ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopSellingProducts();

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId AND oi.product.id = :productId")
    List<OrderItem> findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}