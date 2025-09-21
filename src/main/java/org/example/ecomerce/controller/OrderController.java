package org.example.ecomerce.controller;

import org.example.ecomerce.controller.response.OrderStatusResponse;
import org.example.ecomerce.model.Order;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.controller.dto.CartItem;
import org.example.ecomerce.service.CartService;
import org.example.ecomerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    // ========================================
    // MOSTRAR FORMULARIO DE CHECKOUT
    // ========================================

    @GetMapping("/checkout")
    public String showCheckoutForm(HttpSession session, Model model) {
        // Temporal: simular usuario logueado
        session.setAttribute("userId", 1L);

        List<CartItem> cartItems = cartService.getCartItems(session);

        if (cartItems.isEmpty()) {
            return "redirect:/cart?error=empty";
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("cartItemCount", cartService.getCartItemCount(session));

        return "orders/checkout";
    }

    // ========================================
    // CREAR ORDEN
    // ========================================

    @PostMapping("/create")
    public String createOrder(
            @RequestParam String shippingAddress,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener usuario actual (simulado - en producción usar Spring Security)
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                redirectAttributes.addFlashAttribute("error", "Debe iniciar sesión");
                return "redirect:/login";
            }

            List<CartItem> cartItems = cartService.getCartItems(session);
            if (cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El carrito está vacío");
                return "redirect:/cart";
            }

            Order order = orderService.createOrderFromCart(
                    currentUserId, cartItems, shippingAddress, phoneNumber, notes);

            // Limpiar carrito después de crear la orden
            cartService.clearCart(session);

            redirectAttributes.addFlashAttribute("success",
                    "Orden creada exitosamente. Número de orden: " + order.getId());

            log.info("Order created successfully: orderId={}, userId={}", order.getId(), currentUserId);
            return "redirect:/orders/" + order.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la orden: " + e.getMessage());
            log.error("Error creating order: {}", e.getMessage());
            return "redirect:/orders/checkout";
        }
    }

    // ========================================
    // VER DETALLE DE ORDEN
    // ========================================

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return "redirect:/login";
        }

        Optional<Order> orderOpt = orderService.findByIdWithItems(id);

        if (orderOpt.isEmpty()) {
            return "redirect:/orders?error=notfound";
        }

        Order order = orderOpt.get();

        // Verificar que el usuario tenga acceso a esta orden
        if (!order.getUserId().equals(currentUserId)) {
            return "redirect:/orders?error=access";
        }

        model.addAttribute("order", order);
        return "orders/detail";
    }

    // ========================================
    // LISTAR ÓRDENES DEL USUARIO
    // ========================================

    @GetMapping
    public String listUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            HttpSession session,
            Model model) {

        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> ordersPage = orderService.findByUserId(currentUserId, pageable);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalOrders", ordersPage.getTotalElements());
        model.addAttribute("hasNext", ordersPage.hasNext());
        model.addAttribute("hasPrevious", ordersPage.hasPrevious());
        model.addAttribute("selectedStatus", status);

        // Agregar estadísticas del usuario
        model.addAttribute("totalUserOrders", orderService.countOrdersByUser(currentUserId));
        model.addAttribute("totalSpent", orderService.getTotalSpentByUser(currentUserId));

        return "orders/list";
    }

    // ========================================
    // CANCELAR ORDEN
    // ========================================

    @PostMapping("/{id}/cancel")
    public String cancelOrder(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Long currentUserId = getCurrentUserId(session);
            if (currentUserId == null) {
                return "redirect:/login";
            }

            Optional<Order> orderOpt = orderService.findById(id);
            if (orderOpt.isEmpty() || !orderOpt.get().getUserId().equals(currentUserId)) {
                redirectAttributes.addFlashAttribute("error", "Orden no encontrada");
                return "redirect:/orders";
            }

            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("success", "Orden cancelada exitosamente");

            log.info("Order cancelled: orderId={}, userId={}", id, currentUserId);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar: " + e.getMessage());
            log.error("Error cancelling order {}: {}", id, e.getMessage());
        }

        return "redirect:/orders/" + id;
    }

    // ========================================
    // API ENDPOINTS PARA AJAX
    // ========================================

    @GetMapping("/api/status/{id}")
    @ResponseBody
    public OrderStatusResponse getOrderStatus(@PathVariable Long id, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            return OrderStatusResponse.builder()
                    .success(false)
                    .message("No autorizado")
                    .build();
        }

        Optional<Order> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty() || !orderOpt.get().getUserId().equals(currentUserId)) {
            return OrderStatusResponse.builder()
                    .success(false)
                    .message("Orden no encontrada")
                    .build();
        }

        Order order = orderOpt.get();
        return OrderStatusResponse.builder()
                .success(true)
                .status(order.getStatus())
                .statusDisplayName(order.getStatus().getDisplayName())
                .build();
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    private Long getCurrentUserId(HttpSession session) {
        // Simulación - en producción usar Spring Security
        Object userId = session.getAttribute("userId");
        return userId != null ? (Long) userId : null;
    }

    // ========================================
    // CLASES DE RESPUESTA
    // ========================================


}
