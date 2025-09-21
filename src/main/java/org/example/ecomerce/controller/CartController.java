package org.example.ecomerce.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.controller.dto.CartItem;
import org.example.ecomerce.controller.response.CartResponse;
import org.example.ecomerce.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    // ========================================
    // VER CARRITO
    // ========================================

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cartItems = cartService.getCartItems(session);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("cartItemCount", cartService.getCartItemCount(session));
        model.addAttribute("isEmpty", cartService.isEmpty(session));

        return "cart/cart";
    }

    // ========================================
    // AGREGAR AL CARRITO
    // ========================================

    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            cartService.addItem(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
            log.info("Product added to cart: productId={}, quantity={}", productId, quantity);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            log.error("Error adding product to cart: {}", e.getMessage());
        }

        return "redirect:/products";
    }

    @PostMapping("/add-ajax")
    @ResponseBody
    public CartResponse addToCartAjax(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session) {

        try {
            cartService.addItem(session, productId, quantity);
            return CartResponse.builder()
                    .success(true)
                    .message("Producto agregado al carrito")
                    .cartItemCount(cartService.getCartItemCount(session))
                    .cartTotal(cartService.getCartTotal(session))
                    .build();
        } catch (Exception e) {
            return CartResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    // ========================================
    // ACTUALIZAR CANTIDAD
    // ========================================

    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            cartService.updateQuantity(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Cantidad actualizada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/update-ajax")
    @ResponseBody
    public CartResponse updateQuantityAjax(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            HttpSession session) {

        try {
            cartService.updateQuantity(session, productId, quantity);
            return CartResponse.builder()
                    .success(true)
                    .message("Cantidad actualizada")
                    .cartItemCount(cartService.getCartItemCount(session))
                    .cartTotal(cartService.getCartTotal(session))
                    .build();
        } catch (Exception e) {
            return CartResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    // ========================================
    // REMOVER DEL CARRITO
    // ========================================

    @PostMapping("/remove/{productId}")
    public String removeFromCart(
            @PathVariable Long productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        cartService.removeItem(session, productId);
        redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");

        return "redirect:/cart";
    }

    @PostMapping("/remove-ajax")
    @ResponseBody
    public CartResponse removeFromCartAjax(
            @RequestParam Long productId,
            HttpSession session) {

        cartService.removeItem(session, productId);
        return CartResponse.builder()
                .success(true)
                .message("Producto eliminado del carrito")
                .cartItemCount(cartService.getCartItemCount(session))
                .cartTotal(cartService.getCartTotal(session))
                .build();
    }

    // ========================================
    // LIMPIAR CARRITO
    // ========================================

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.clearCart(session);
        redirectAttributes.addFlashAttribute("success", "Carrito vaciado");
        return "redirect:/cart";
    }

    // ========================================
    // API PARA OBTENER INFORMACIÓN DEL CARRITO
    // ========================================

    @GetMapping("/count")
    @ResponseBody
    public int getCartItemCount(HttpSession session) {
        return cartService.getCartItemCount(session);
    }

    @GetMapping("/total")
    @ResponseBody
    public CartResponse getCartInfo(HttpSession session) {
        return CartResponse.builder()
                .success(true)
                .cartItemCount(cartService.getCartItemCount(session))
                .cartTotal(cartService.getCartTotal(session))
                .build();
    }

}
