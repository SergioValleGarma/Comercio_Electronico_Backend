package org.example.ecomerce.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.ecomerce.controller.dto.CartItem;
import org.example.ecomerce.controller.dto.CheckoutSummaryResponse;
import org.example.ecomerce.dto.response.ApiResponse;
import org.example.ecomerce.dto.response.CartInfoResponse;
import org.example.ecomerce.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<List<CartItem>> getCart(HttpSession session) {
        List<CartItem> cartItems = cartService.getCartItems(session);
        return ApiResponse.success(cartItems);
    }

    @PostMapping("/add")
    public ApiResponse<CartInfoResponse> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session) {
        
        try {
            cartService.addItem(session, productId, quantity);
            return getCartInfoResponse(session, "Producto agregado al carrito");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ApiResponse<CartInfoResponse> updateQuantity(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            HttpSession session) {
        
        try {
            cartService.updateQuantity(session, productId, quantity);
            return getCartInfoResponse(session, "Cantidad actualizada");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ApiResponse<CartInfoResponse> removeFromCart(
            @PathVariable Long productId,
            HttpSession session) {
        
        cartService.removeItem(session, productId);
        return getCartInfoResponse(session, "Producto eliminado del carrito");
    }

    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart(HttpSession session) {
        cartService.clearCart(session);
        return ApiResponse.success("Carrito vaciado exitosamente", null);
    }

    @GetMapping("/info")
    public ApiResponse<CartInfoResponse> getCartInfo(HttpSession session) {
        return getCartInfoResponse(session, "Información del carrito");
    }

    private ApiResponse<CartInfoResponse> getCartInfoResponse(HttpSession session, String message) {
        List<CartItem> cartItems = cartService.getCartItems(session);
        BigDecimal total = cartService.getCartTotal(session);
        int itemCount = cartService.getCartItemCount(session);
        
        CartInfoResponse response = new CartInfoResponse(cartItems, total, itemCount, message);
        return ApiResponse.success(response);
    }

    @GetMapping("/checkout/summary")
    public ApiResponse<CheckoutSummaryResponse> getCheckoutSummary(HttpSession session) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(session);
            BigDecimal subtotal = cartService.getCartTotal(session);
            int itemCount = cartService.getCartItemCount(session);

            BigDecimal shipping = BigDecimal.valueOf(25.00);
            BigDecimal total = subtotal.add(shipping);

            CheckoutSummaryResponse summary = new CheckoutSummaryResponse(
                    subtotal,
                    shipping,
                    total,
                    itemCount,
                    cartItems.isEmpty()
            );

            return ApiResponse.success(summary);

        } catch (Exception e) {
            return ApiResponse.error("Error al obtener resumen: " + e.getMessage());
        }
    }

    @GetMapping("/checkout/items")
    public ApiResponse<List<CartItem>> getCheckoutItems(HttpSession session) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(session);
            return ApiResponse.success(cartItems);

        } catch (Exception e) {
            return ApiResponse.error("Error al obtener items: " + e.getMessage());
        }
    }
}