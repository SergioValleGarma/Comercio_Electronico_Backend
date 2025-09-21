document.addEventListener("DOMContentLoaded", function () {
    console.log("Product.js cargado");

    if (typeof toastr === "undefined") {
        console.error("toastr no está cargado; revisa el orden de los <script>.");
        return;
    }

    const buttons = document.querySelectorAll(".btn-add-cart");

    buttons.forEach(button => {
        button.addEventListener("click", function () {
            console.log("Botón de añadir al carrito clickeado");
            const productId = this.getAttribute("data-id");
            const productName = this.getAttribute("data-name") || "Producto";
            const productPrice = this.getAttribute("data-price") || "0";

            // Crear FormData para enviar como form POST
            const formData = new FormData();
            formData.append("productId", productId);
            formData.append("quantity", 1);

            // Usar fetch con FormData para que coincida con tu CartController
            fetch("/cart/add-ajax", {
                method: "POST",
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Error al añadir al carrito");
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        toastr.success(`${productName} añadido al carrito 🛒`, "Éxito", {
                            closeButton: true,
                            progressBar: true,
                            positionClass: "toast-top-right",
                            timeOut: 3000
                        });

                        // Actualizar contador del carrito
                        updateCartCounter(data.cartItemCount);

                        console.log("Producto añadido exitosamente:", data);
                    } else {
                        toastr.error(data.message || "No se pudo añadir al carrito", "Error");
                    }
                })
                .catch(err => {
                    toastr.error("No se pudo añadir al carrito", "Error");
                    console.error("Error:", err);
                });
        });
    });

    // Función para actualizar contador del carrito
    function updateCartCounter(count = null) {
        if (count !== null) {
            // Si tenemos el count del response, usarlo directamente
            const cartCountElement = document.getElementById("cart-count");
            if (cartCountElement) {
                cartCountElement.textContent = count;
            }
        } else {
            // Si no, hacer una petición para obtenerlo
            fetch("/cart/count")
                .then(response => response.text())
                .then(count => {
                    const cartCountElement = document.getElementById("cart-count");
                    if (cartCountElement) {
                        cartCountElement.textContent = count;
                    }
                })
                .catch(err => console.error("Error obteniendo count del carrito:", err));
        }
    }

    // Actualizar contador al cargar la página
    updateCartCounter();
});

(function () {
    const rangeMin = document.getElementById('rangeMin');
    const rangeMax = document.getElementById('rangeMax');
    const minVal  = document.getElementById('rangeMinVal');
    const maxVal  = document.getElementById('rangeMaxVal');
    const inputMin = document.getElementById('priceMin');
    const inputMax = document.getElementById('priceMax');

    if (!rangeMin || !rangeMax) return;

    const initMin = parseFloat(inputMin?.value || '0');
    const initMax = parseFloat(inputMax?.value || '10000');
    rangeMin.value = isNaN(initMin) ? 0 : initMin;
    rangeMax.value = isNaN(initMax) ? 10000 : initMax;
    minVal.textContent = `S/ ${rangeMin.value}`;
    maxVal.textContent = `S/ ${rangeMax.value}`;

    const clamp = () => {
        if (+rangeMin.value > +rangeMax.value) {
            const t = rangeMin.value;
            rangeMin.value = rangeMax.value;
            rangeMax.value = t;
        }
        minVal.textContent = `S/ ${rangeMin.value}`;
        maxVal.textContent = `S/ ${rangeMax.value}`;
        if (inputMin) inputMin.value = rangeMin.value;
        if (inputMax) inputMax.value = rangeMax.value;
    };

    rangeMin.addEventListener('input', clamp);
    rangeMax.addEventListener('input', clamp);

    const syncFromInputs = () => {
        const vMin = parseFloat(inputMin.value || '0');
        const vMax = parseFloat(inputMax.value || '0');
        if (!isNaN(vMin)) rangeMin.value = vMin;
        if (!isNaN(vMax)) rangeMax.value = vMax;
        clamp();
    };
    inputMin?.addEventListener('change', syncFromInputs);
    inputMax?.addEventListener('change', syncFromInputs);
})();
