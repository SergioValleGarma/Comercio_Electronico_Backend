package org.example.ecomerce.repository;


import org.example.ecomerce.model.Order;
import org.example.ecomerce.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ========================================
    // QUERIES POR USER ID
    // ========================================

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    Page<Order> findByUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    long countByUserId(Long userId);

    // ========================================
    // QUERIES POR STATUS
    // ========================================

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusIn(List<OrderStatus> statuses);

    long countByStatus(OrderStatus status);

    // ========================================
    // QUERIES POR FECHA
    // ========================================

    List<Order> findByOrderDateBetween(Instant startDate, Instant endDate);

    List<Order> findByOrderDateAfter(Instant date);

    List<Order> findByOrderDateBefore(Instant date);

    // ========================================
    // QUERIES POR MONTO
    // ========================================

    List<Order> findByTotalAmountGreaterThan(BigDecimal amount);

    List<Order> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // ========================================
    // QUERIES COMBINADAS
    // ========================================

    List<Order> findByUserIdAndStatusAndOrderDateBetween(
            Long userId,
            OrderStatus status,
            Instant startDate,
            Instant endDate
    );

    List<Order> findByStatusAndOrderDateAfter(OrderStatus status, Instant date);

    // ========================================
    // QUERIES PERSONALIZADAS CON @Query
    // ========================================

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.userId = :userId")
    List<Order> findByUserIdWithItems(@Param("userId") Long userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.orderDate >= :date")
    long countByStatusAndOrderDateAfter(@Param("status") OrderStatus status, @Param("date") Instant date);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.userId = :userId AND o.status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED')")
    BigDecimal getTotalSpentByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSalesBetweenDates(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED') AND o.orderDate < :date")
    List<Order> findStaleOrders(@Param("date") Instant date);

    // ========================================
    // ESTADÍSTICAS
    // ========================================

    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getAverageOrderValue();

    @Query("SELECT COUNT(DISTINCT o.userId) FROM Order o WHERE o.orderDate >= :date")
    long getUniqueCustomersCount(@Param("date") Instant date);
}