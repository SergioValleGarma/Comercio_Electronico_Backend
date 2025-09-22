package org.example.ecomerce.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.dto.response.ApiResponse;
import org.example.ecomerce.dto.response.OrderResponse;
import org.example.ecomerce.dto.response.OrderItemResponse;
import org.example.ecomerce.model.Order;
import org.example.ecomerce.model.OrderStatus;
import org.example.ecomerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderApiController {

    private final OrderService orderService;

    // ========================================
    // LISTAR TODAS LAS ÓRDENES
    // ========================================

    @GetMapping
    public ApiResponse<Page<OrderResponse>> listAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {

        try {
            Pageable pageable = PageRequest.of(page, size);

            Page<Order> ordersPage;
            if (status != null && !status.isEmpty()) {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                List<Order> orders = orderService.findByStatus(orderStatus);
                // Convertir List a Page manualmente
                ordersPage = new PageImpl<>(orders, pageable, orders.size());
            } else {
                ordersPage = orderService.findAll(pageable);
            }

            Page<OrderResponse> responsePage = ordersPage.map(this::convertToResponse);
            return ApiResponse.success(responsePage);

        } catch (Exception e) {
            log.error("Error listing all orders: {}", e.getMessage());
            return ApiResponse.error("Error al listar órdenes: " + e.getMessage());
        }
    }

    // ========================================
    // ACTUALIZAR ESTADO DE ORDEN
    // ========================================

    @PatchMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
            
            log.info("Order status updated by admin: orderId={}, newStatus={}", id, newStatus);
            return ApiResponse.success("Estado actualizado correctamente", convertToResponse(updatedOrder));

        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage());
            return ApiResponse.error("Error al actualizar estado: " + e.getMessage());
        }
    }

    // ========================================
    // VER DETALLE DE ORDEN (ADMIN)
    // ========================================

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> viewOrderAdmin(@PathVariable Long id) {
        try {
            return orderService.findByIdWithItems(id)
                    .map(order -> ApiResponse.success(convertToResponse(order)))
                    .orElse(ApiResponse.error("Orden no encontrada"));

        } catch (Exception e) {
            log.error("Error viewing admin order: {}", e.getMessage());
            return ApiResponse.error("Error al ver orden: " + e.getMessage());
        }
    }

    // ========================================
    // ESTADÍSTICAS GENERALES
    // ========================================

    @GetMapping("/statistics")
    public ApiResponse<Object> getOrderStatistics() {
        try {
            Object stats = orderService.getOrderStatistics();
            return ApiResponse.success(stats);

        } catch (Exception e) {
            log.error("Error getting order statistics: {}", e.getMessage());
            return ApiResponse.error("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    private OrderResponse convertToResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getOrderDate(),
            order.getShippingAddress(),
            order.getPhoneNumber(),
            order.getNotes(),
            order.getOrderItems().stream().map(item -> new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            )).collect(Collectors.toList())
        );
    }
}