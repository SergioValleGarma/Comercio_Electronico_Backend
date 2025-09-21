/* ========================================================================== */
/* ORDER SCRIPTS JS - static/js/order-scripts.js */
/* ========================================================================== */

// CART FUNCTIONALITY
class CartManager {
    constructor() {
        this.init();
    }

    init() {
        this.bindEvents();
        this.updateCartCounter();
        this.initQuantityValidation();
    }

    bindEvents() {
        // Add to cart buttons
        document.addEventListener('click', (e) => {
            if (e.target.matches('.add-to-cart-btn')) {
                this.handleAddToCart(e);
            }
        });

        // Update quantity buttons
        document.addEventListener('click', (e) => {
            if (e.target.matches('.update-quantity-btn')) {
                this.handleUpdateQuantity(e);
            }
        });

        // Remove item buttons
        document.addEventListener('click', (e) => {
            if (e.target.matches('.remove-item-btn')) {
                this.handleRemoveItem(e);
            }
        });

        // Clear cart button
        document.addEventListener('click', (e) => {
            if (e.target.matches('.clear-cart-btn')) {
                this.handleClearCart(e);
            }
        });

        // Quantity input changes
        document.addEventListener('change', (e) => {
            if (e.target.matches('.quantity-input')) {
                this.validateQuantity(e.target);
            }
        });
    }

    async handleAddToCart(e) {
        e.preventDefault();
        const form = e.target.closest('form');
        const formData = new FormData(form);

        try {
            this.showLoading(e.target);
            const response = await fetch('/cart/add-ajax', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();

            if (result.success) {
                this.showSuccess('Producto agregado al carrito');
                this.updateCartCounter(result.cartItemCount);
                this.updateCartTotal(result.cartTotal);
            } else {
                this.showError(result.message);
            }
        } catch (error) {
            this.showError('Error al agregar producto al carrito');
        } finally {
            this.hideLoading(e.target);
        }
    }

    async handleUpdateQuantity(e) {
        e.preventDefault();
        const form = e.target.closest('form');
        const formData = new FormData(form);

        try {
            const response = await fetch('/cart/update-ajax', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();

            if (result.success) {
                this.showSuccess('Cantidad actualizada');
                this.updateCartCounter(result.cartItemCount);
                this.updateCartTotal(result.cartTotal);
                // Refresh the page to show updated cart
                setTimeout(() => window.location.reload(), 1000);
            } else {
                this.showError(result.message);
            }
        } catch (error) {
            this.showError('Error al actualizar cantidad');
        }
    }

    async handleRemoveItem(e) {
        e.preventDefault();

        if (!confirm('¿Está seguro de eliminar este producto del carrito?')) {
            return;
        }

        const form = e.target.closest('form');
        const formData = new FormData(form);

        try {
            const response = await fetch('/cart/remove-ajax', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();

            if (result.success) {
                this.showSuccess('Producto eliminado del carrito');
                this.updateCartCounter(result.cartItemCount);
                this.updateCartTotal(result.cartTotal);
                // Remove the row from table
                const row = e.target.closest('tr');
                row.style.transition = 'opacity 0.3s ease';
                row.style.opacity = '0';
                setTimeout(() => {
                    row.remove();
                    // Refresh if cart is empty
                    if (result.cartItemCount === 0) {
                        window.location.reload();
                    }
                }, 300);
            } else {
                this.showError(result.message);
            }
        } catch (error) {
            this.showError('Error al eliminar producto');
        }
    }

    handleClearCart(e) {
        if (!confirm('¿Está seguro de vaciar todo el carrito?')) {
            e.preventDefault();
            return false;
        }
        return true;
    }

    validateQuantity(input) {
        const min = parseInt(input.min) || 1;
        const max = parseInt(input.max) || 9999;
        const value = parseInt(input.value);

        if (value < min) {
            input.value = min;
            this.showWarning(`Cantidad mínima: ${min}`);
        } else if (value > max) {
            input.value = max;
            this.showWarning(`Cantidad máxima disponible: ${max}`);
        }
    }

    initQuantityValidation() {
        document.querySelectorAll('.quantity-input').forEach(input => {
            input.addEventListener('input', (e) => {
                // Only allow numbers
                e.target.value = e.target.value.replace(/[^0-9]/g, '');
            });
        });
    }

    async updateCartCounter(count = null) {
        if (count === null) {
            try {
                const response = await fetch('/cart/count');
                count = await response.text();
            } catch (error) {
                console.error('Error fetching cart count:', error);
                return;
            }
        }

        const counters = document.querySelectorAll('.cart-counter');
        counters.forEach(counter => {
            counter.textContent = count;
            counter.style.display = count > 0 ? 'inline' : 'none';
        });
    }

    updateCartTotal(total) {
        const totalElements = document.querySelectorAll('.cart-total');
        totalElements.forEach(element => {
            element.textContent = `$${total}`;
        });
    }

    showLoading(button) {
        button.disabled = true;
        button.innerHTML = '⏳ Agregando...';
    }

    hideLoading(button) {
        button.disabled = false;
        button.innerHTML = '🛒 Agregar al Carrito';
    }

    showSuccess(message) {
        this.showNotification(message, 'success');
    }

    showError(message) {
        this.showNotification(message, 'error');
    }

    showWarning(message) {
        this.showNotification(message, 'warning');
    }

    showNotification(message, type = 'info') {
        // Remove existing notifications
        document.querySelectorAll('.notification').forEach(n => n.remove());

        const notification = document.createElement('div');
        notification.className = `notification alert alert-${type === 'error' ? 'error' : 'success'}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            min-width: 300px;
            animation: slideIn 0.3s ease;
        `;

        notification.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>${message}</span>
                <button onclick="this.parentElement.parentElement.remove()" 
                        style="background: none; border: none; font-size: 18px; cursor: pointer; color: inherit;">×</button>
            </div>
        `;

        document.body.appendChild(notification);

        // Auto remove after 5 seconds
        setTimeout(() => {
            if (notification.parentElement) {
                notification.style.animation = 'slideOut 0.3s ease';
                setTimeout(() => notification.remove(), 300);
            }
        }, 5000);
    }
}

// ORDER FUNCTIONALITY
class OrderManager {
    constructor() {
        this.init();
    }

    init() {
        this.bindEvents();
        this.initFormValidation();
    }

    bindEvents() {
        // Cancel order buttons
        document.addEventListener('click', (e) => {
            if (e.target.matches('.cancel-order-btn')) {
                this.handleCancelOrder(e);
            }
        });

        // Order status refresh
        document.addEventListener('click', (e) => {
            if (e.target.matches('.refresh-status-btn')) {
                this.refreshOrderStatus(e);
            }
        });
    }

    handleCancelOrder(e) {
        const orderId = e.target.getAttribute('data-order-id');

        if (!confirm('¿Está seguro de cancelar esta orden? Esta acción no se puede deshacer.')) {
            e.preventDefault();
            return false;
        }

        // Add loading state
        e.target.disabled = true;
        e.target.innerHTML = '⏳ Cancelando...';

        return true;
    }

    async refreshOrderStatus(e) {
        e.preventDefault();
        const orderId = e.target.getAttribute('data-order-id');

        try {
            const response = await fetch(`/orders/api/status/${orderId}`);
            const result = await response.json();

            if (result.success) {
                const statusElement = document.querySelector('.order-status');
                if (statusElement) {
                    statusElement.textContent = result.statusDisplayName;
                    statusElement.className = `status status-${result.status.toLowerCase()}`;
                }
                this.showSuccess('Estado actualizado');
            } else {
                this.showError(result.message);
            }
        } catch (error) {
            this.showError('Error al actualizar estado');
        }
    }

    initFormValidation() {
        const checkoutForm = document.querySelector('#checkout-form');
        if (checkoutForm) {
            checkoutForm.addEventListener('submit', (e) => {
                if (!this.validateCheckoutForm(checkoutForm)) {
                    e.preventDefault();
                }
            });
        }
    }

    validateCheckoutForm(form) {
        const address = form.querySelector('#shippingAddress').value.trim();
        const phone = form.querySelector('#phoneNumber').value.trim();

        if (address.length < 10) {
            this.showError('La dirección debe tener al menos 10 caracteres');
            return false;
        }

        if (phone.length < 9) {
            this.showError('El teléfono debe tener al menos 9 dígitos');
            return false;
        }

        return true;
    }

    showSuccess(message) {
        this.showNotification(message, 'success');
    }

    showError(message) {
        this.showNotification(message, 'error');
    }

    showNotification(message, type) {
        // Reuse CartManager notification method
        const cartManager = new CartManager();
        cartManager.showNotification(message, type);
    }
}

// UTILITY FUNCTIONS
class Utils {
    static formatCurrency(amount) {
        return new Intl.NumberFormat('es-PE', {
            style: 'currency',
            currency: 'PEN'
        }).format(amount);
    }

    static formatDate(dateString) {
        return new Intl.DateTimeFormat('es-PE', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        }).format(new Date(dateString));
    }

    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    static showConfirmDialog(message, callback) {
        if (confirm(message)) {
            callback();
        }
    }
}

// ANIMATIONS
const animations = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    @keyframes fadeIn {
        from { opacity: 0; }
        to { opacity: 1; }
    }
    
    @keyframes pulse {
        0% { transform: scale(1); }
        50% { transform: scale(1.05); }
        100% { transform: scale(1); }
    }
`;

// Add animations to head
const style = document.createElement('style');
style.textContent = animations;
document.head.appendChild(style);

// INITIALIZATION
document.addEventListener('DOMContentLoaded', () => {
    // Initialize managers
    window.cartManager = new CartManager();
    window.orderManager = new OrderManager();

    // Add loading states to forms
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', (e) => {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn && !submitBtn.disabled) {
                submitBtn.disabled = true;
                const originalText = submitBtn.textContent;
                submitBtn.textContent = '⏳ Procesando...';

                // Re-enable after 5 seconds as fallback
                setTimeout(() => {
                    submitBtn.disabled = false;
                    submitBtn.textContent = originalText;
                }, 5000);
            }
        });
    });

    // Add smooth scrolling
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Add fade-in animation to elements
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animation = 'fadeIn 0.6s ease forwards';
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.container, .total-section, .stats').forEach(el => {
        el.style.opacity = '0';
        observer.observe(el);
    });

    console.log('🛒 Order system initialized successfully!');
});