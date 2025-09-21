$(document).ready(function() {
    // Actualizar cantidad
    $('.btn-update').on('click', function() {
        const productId = $(this).data('product-id');
        const quantity = $(`input[data-product-id="${productId}"]`).val();

        updateQuantity(productId, quantity);
    });

    // Remover item
    $('.btn-remove').on('click', function() {
        const productId = $(this).data('product-id');
        removeItem(productId);
    });

    // Vaciar carrito
    $('#clear-cart').on('click', function() {
        if (confirm('¿Estás seguro de vaciar el carrito?')) {
            clearCart();
        }
    });

    function updateQuantity(productId, quantity) {
        $.ajax({
            url: '/cart/update-ajax',
            method: 'POST',
            data: { productId: productId, quantity: quantity },
            success: function(response) {
                if (response.success) {
                    toastr.success('Cantidad actualizada');
                    location.reload();
                } else {
                    toastr.error(response.message);
                }
            }
        });
    }

    function removeItem(productId) {
        $.ajax({
            url: '/cart/remove-ajax',
            method: 'POST',
            data: { productId: productId },
            success: function(response) {
                toastr.success('Producto eliminado');
                location.reload();
            }
        });
    }

    function clearCart() {
        $.post('/cart/clear', function() {
            toastr.success('Carrito vaciado');
            location.reload();
        });
    }
});