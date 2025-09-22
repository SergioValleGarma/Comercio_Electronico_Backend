package org.example.ecomerce.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.dto.request.OrderRequest;
import org.example.ecomerce.dto.response.ApiResponse;
import org.example.ecomerce.dto.response.OrderResponse;
import org.example.ecomerce.dto.response.OrderItemResponse;
import org.example.ecomerce.dto.response.OrderStatusResponse;
import org.example.ecomerce.dto.response.UserOrderStats;
import org.example.ecomerce.model.Order;
import org.example.ecomerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderApiController {

    private final OrderService orderService;

    // ========================================
    // CREAR ORDEN
    // ========================================

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ApiResponse.error("Debe iniciar sesión");
            }

            // En una implementación real, necesitarías obtener los items del carrito de la sesión
            // Por ahora, usaremos los items que vienen en el request
            Order order = orderService.createOrderFromCart(
                    currentUserId, 
                    orderRequest.getCartItems(), 
                    orderRequest.getShippingAddress(), 
                    orderRequest.getPhoneNumber(), 
                    orderRequest.getNotes()
            );

            return ApiResponse.success("Orden creada exitosamente", convertToResponse(order));

        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return ApiResponse.error("Error al crear la orden: " + e.getMessage());
        }
    }

    // ========================================
    // LISTAR ÓRDENES DEL USUARIO
    // ========================================

    @GetMapping
    public ApiResponse<Page<OrderResponse>> listUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            HttpSession session) {

        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ApiResponse.error("Debe iniciar sesión");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Order> ordersPage = orderService.findByUserId(currentUserId, pageable);
            Page<OrderResponse> responsePage = ordersPage.map(this::convertToResponse);

            return ApiResponse.success(responsePage);

        } catch (Exception e) {
            log.error("Error listing orders: {}", e.getMessage());
            return ApiResponse.error("Error al listar órdenes: " + e.getMessage());
        }
    }

    // ========================================
    // VER DETALLE DE ORDEN
    // ========================================

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> viewOrder(@PathVariable Long id, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ApiResponse.error("Debe iniciar sesión");
            }

            Optional<Order> orderOpt = orderService.findByIdWithItems(id);
            if (orderOpt.isEmpty()) {
                return ApiResponse.error("Orden no encontrada");
            }

            Order order = orderOpt.get();
            if (!order.getUserId().equals(currentUserId)) {
                return ApiResponse.error("Acceso denegado");
            }

            return ApiResponse.success(convertToResponse(order));

        } catch (Exception e) {
            log.error("Error viewing order: {}", e.getMessage());
            return ApiResponse.error("Error al ver orden: " + e.getMessage());
        }
    }

    // ========================================
    // CANCELAR ORDEN
    // ========================================

    @PostMapping("/{id}/cancel")
    public ApiResponse<String> cancelOrder(@PathVariable Long id, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ApiResponse.error("Debe iniciar sesión");
            }

            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty() || !orderOpt.get().getUserId().equals(currentUserId)) {
                return ApiResponse.error("Orden no encontrada");
            }

            orderService.cancelOrder(id);
            return ApiResponse.success("Orden cancelada exitosamente");

        } catch (Exception e) {
            log.error("Error cancelling order: {}", e.getMessage());
            return ApiResponse.error("Error al cancelar orden: " + e.getMessage());
        }
    }

    // ========================================
    // VER ESTADO DE ORDEN
    // ========================================

    @GetMapping("/{id}/status")
    public ApiResponse<OrderStatusResponse> getOrderStatus(@PathVariable Long id, HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ApiResponse.error("No autorizado");
            }

            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty() || !orderOpt.get().getUserId().equals(currentUserId)) {
                return ApiResponse.error("Orden no encontrada");
            }

            Order order = orderOpt.get();
            OrderStatusResponse response = new OrderStatusResponse(
                order.getStatus(),
                order.getStatus().getDisplayName()
            );
            
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("Error getting order status: {}", e.getMessage());
            return ApiResponse.error("Error al obtener estado: " + e.getMessage());
        }
    }

    // ========================================
    // ESTADÍSTICAS DE USUARIO
    // ========================================

    @GetMapping("/statistics/user")
    public ApiResponse<UserOrderStats> getUserOrderStatistics(HttpSession session) {
        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return ApiResponse.error("Debe iniciar sesión");
            }

            long totalOrders = orderService.countOrdersByUser(currentUserId);
            BigDecimal totalSpent = orderService.getTotalSpentByUser(currentUserId);

            UserOrderStats stats = new UserOrderStats(totalOrders, totalSpent);
            return ApiResponse.success(stats);

        } catch (Exception e) {
            log.error("Error getting user statistics: {}", e.getMessage());
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

    private Long getCurrentUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId != null ? (Long) userId : null;
    }
}