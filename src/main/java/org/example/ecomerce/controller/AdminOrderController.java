package org.example.ecomerce.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.model.Order;
import org.example.ecomerce.model.OrderStatus;
import org.example.ecomerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderController {

    private final OrderService orderService;

    // ========================================
    // LISTAR TODAS LAS ÓRDENES
    // ========================================

    @GetMapping
    public String listAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        List<Order> orders;
        if (status != null && !status.isEmpty()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orderService.findByStatus(orderStatus);
        } else {
            Page<Order> ordersPage = orderService.findByUserId(null, pageable); // Modificar para admin
            orders = ordersPage.getContent();
            model.addAttribute("totalPages", ordersPage.getTotalPages());
            model.addAttribute("totalOrders", ordersPage.getTotalElements());
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("orderStatuses", OrderStatus.values());

        // Estadísticas generales
        model.addAttribute("orderStats", orderService.getOrderStatistics());

        return "admin/orders/list";
    }

    // ========================================
    // ACTUALIZAR ESTADO DE ORDEN
    // ========================================

    @PostMapping("/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            orderService.updateOrderStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado correctamente");

            log.info("Order status updated by admin: orderId={}, newStatus={}", id, newStatus);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar estado: " + e.getMessage());
            log.error("Error updating order status: {}", e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    // ========================================
    // VER DETALLE DE ORDEN (ADMIN)
    // ========================================

    @GetMapping("/{id}")
    public String viewOrderAdmin(@PathVariable Long id, Model model) {
        return orderService.findByIdWithItems(id)
                .map(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("orderStatuses", OrderStatus.values());
                    return "admin/orders/detail";
                })
                .orElse("redirect:/admin/orders?error=notfound");
    }
}