package org.example.ecomerce.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.controller.dto.CartItem;
import org.example.ecomerce.model.Product;
import org.example.ecomerce.repository.ProductRepository;
import org.example.ecomerce.service.CartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private static final String CART_SESSION_KEY = "shopping_cart";

    @Override
    public void addItem(HttpSession session, Long productId, Integer quantity) {
        log.debug("Adding item to cart: productId={}, quantity={}", productId, quantity);

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Producto no encontrado: " + productId);
        }

        Product product = productOpt.get();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente para: " + product.getName());
        }

        List<CartItem> cart = getCartItems(session);

        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }

            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(productId)
                    .productName(product.getName())
                    .productPrice(product.getPrice())
                    .quantity(quantity)
                    .build();
            cart.add(newItem);
        }

        session.setAttribute(CART_SESSION_KEY, cart);
        log.debug("Cart updated. Total items: {}", cart.size());
    }

    @Override
    public void updateQuantity(HttpSession session, Long productId, Integer quantity) {
        log.debug("Updating cart item quantity: productId={}, quantity={}", productId, quantity);

        if (quantity <= 0) {
            removeItem(session, productId);
            return;
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Producto no encontrado: " + productId);
        }

        Product product = productOpt.get();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente para: " + product.getName());
        }

        List<CartItem> cart = getCartItems(session);
        cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void removeItem(HttpSession session, Long productId) {
        log.debug("Removing item from cart: productId={}", productId);

        List<CartItem> cart = getCartItems(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void clearCart(HttpSession session) {
        log.debug("Clearing cart");
        session.removeAttribute(CART_SESSION_KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CartItem> getCartItems(HttpSession session) {
        Object cartObj = session.getAttribute(CART_SESSION_KEY);
        if (cartObj instanceof List) {
            return (List<CartItem>) cartObj;
        }
        return new ArrayList<>();
    }

    @Override
    public BigDecimal getCartTotal(HttpSession session) {
        return getCartItems(session).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int getCartItemCount(HttpSession session) {
        return getCartItems(session).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Override
    public boolean isEmpty(HttpSession session) {
        return getCartItems(session).isEmpty();
    }
}
