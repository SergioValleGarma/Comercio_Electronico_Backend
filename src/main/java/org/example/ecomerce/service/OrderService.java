package org.example.ecomerce.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.controller.dto.CartItem;
import org.example.ecomerce.controller.dto.OrderStatistics;
import org.example.ecomerce.model.Order;
import org.example.ecomerce.model.OrderItem;
import org.example.ecomerce.model.OrderStatus;
import org.example.ecomerce.model.Product;
import org.example.ecomerce.repository.OrderItemRepository;
import org.example.ecomerce.repository.OrderRepository;
import org.example.ecomerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    // ========================================
    // CREAR ÓRDENES
    // ========================================

    public Order createOrderFromCart(Long userId, List<CartItem> cartItems,
                                     String shippingAddress, String phoneNumber, String notes) {
        log.info("Creating order for user: {}", userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        // Crear la orden
        Order order = Order.builder()
                .userId(userId)
                .totalAmount(BigDecimal.ZERO)
                .shippingAddress(shippingAddress)
                .phoneNumber(phoneNumber)
                .notes(notes)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        // Procesar cada item del carrito
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + cartItem.getProductId()));

            // Verificar stock disponible
            if (product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + product.getName());
            }

            // Crear OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice()) // Precio actual del producto
                    .build();

            order.getOrderItems().add(orderItem);

            // Actualizar stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            total = total.add(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully: {}", savedOrder.getId());
        return savedOrder;
    }

    // ========================================
    // CONSULTAR ÓRDENES
    // ========================================

    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findByIdWithItems(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Order> findByUserIdWithItems(Long userId) {
        return orderRepository.findByUserIdWithItems(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // ========================================
    // ACTUALIZAR ÓRDENES
    // ========================================

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order status: orderId={}, newStatus={}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated: {} -> {}", oldStatus, newStatus);

        return updatedOrder;
    }

    public Order cancelOrder(Long orderId) {
        log.info("Cancelling order: {}", orderId);

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("No se puede cancelar una orden entregada");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("La orden ya está cancelada");
        }

        // Restaurar stock de productos
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
            log.debug("Stock restored for product: {} (+{})", product.getName(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        log.info("Order cancelled successfully: {}", orderId);
        return cancelledOrder;
    }

    // ========================================
    // ESTADÍSTICAS
    // ========================================

    @Transactional(readOnly = true)
    public long countOrdersByUser(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalSpentByUser(Long userId) {
        BigDecimal total = orderRepository.getTotalSpentByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageOrderValue() {
        BigDecimal avg = orderRepository.getAverageOrderValue();
        return avg != null ? avg : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics() {
        Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant lastWeek = today.minus(7, ChronoUnit.DAYS);
        Instant lastMonth = today.minus(30, ChronoUnit.DAYS);

        long totalOrders = orderRepository.count();
        long weeklyOrders = orderRepository.countByStatusAndOrderDateAfter(OrderStatus.DELIVERED, lastWeek);
        long monthlyOrders = orderRepository.countByStatusAndOrderDateAfter(OrderStatus.DELIVERED, lastMonth);
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);

        BigDecimal weeklyRevenue = orderRepository.getTotalSalesBetweenDates(lastWeek, today);
        BigDecimal monthlyRevenue = orderRepository.getTotalSalesBetweenDates(lastMonth, today);

        return OrderStatistics.builder()
                .totalOrders(totalOrders)
                .weeklyOrders(weeklyOrders)
                .monthlyOrders(monthlyOrders)
                .pendingOrders(pendingOrders)
                .weeklyRevenue(weeklyRevenue != null ? weeklyRevenue : BigDecimal.ZERO)
                .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                .averageOrderValue(getAverageOrderValue())
                .build();
    }
}