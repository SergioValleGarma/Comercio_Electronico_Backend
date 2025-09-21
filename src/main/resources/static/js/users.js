// Users Management JavaScript
document.addEventListener('DOMContentLoaded', function() {

    // Elementos del DOM
    const deleteButtons = document.querySelectorAll('.delete-btn');
    const deleteModal = document.getElementById('deleteModal');
    const deleteForm = document.getElementById('deleteForm');
    const userToDeleteSpan = document.getElementById('userToDelete');
    const cancelDeleteBtn = document.getElementById('cancelDelete');
    const closeModal = document.querySelector('.modal .close'); // Selector más específico
    const successAlert = document.getElementById('successAlert');
    const errorAlert = document.getElementById('errorAlert');

    // Auto-ocultar alertas después de 5 segundos
    if (successAlert) {
        setTimeout(() => hideAlert(successAlert), 5000);
    }

    if (errorAlert) {
        setTimeout(() => hideAlert(errorAlert), 5000);
    }

    // Manejar clicks en botones de eliminar
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-user-id');
            const userName = this.getAttribute('data-user-name');

            // Configurar modal
            userToDeleteSpan.textContent = userName;
            deleteForm.action = `/users/${userId}/delete`;

            // Mostrar modal
            showModal();
        });
    });

    // Cerrar modal
    function closeDeleteModal() {
        hideModal();
    }

    // Event listeners para cerrar modal
    if (cancelDeleteBtn) {
        cancelDeleteBtn.addEventListener('click', closeDeleteModal);
    }

    if (closeModal) {
        closeModal.addEventListener('click', closeDeleteModal);
    }

    // Cerrar modal al hacer click fuera
    window.addEventListener('click', function(event) {
        if (event.target === deleteModal) {
            closeDeleteModal();
        }
    });

    // Cerrar modal con ESC
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape' && deleteModal && deleteModal.style.display === 'block') {
            closeDeleteModal();
        }
    });

    // Funciones auxiliares
    function showModal() {
        if (deleteModal) {
            deleteModal.style.display = 'block';
            document.body.style.overflow = 'hidden';
        }
    }

    function hideModal() {
        if (deleteModal) {
            deleteModal.style.display = 'none';
            document.body.style.overflow = 'auto';
        }
    }

    function hideAlert(alertElement) {
        // Implementa una animación de salida si es necesario
        if (alertElement) {
            alertElement.style.transition = 'opacity 0.5s ease';
            alertElement.style.opacity = '0';
            setTimeout(() => alertElement.remove(), 500);
        }
    }

    // Filtrado de tabla (funcionalidad básica)
    function addTableFilter() {
        const header = document.querySelector('.header');
        const usersTable = document.getElementById('usersTable');

        // Solo agregar filtro si existe la tabla y el header
        if (!header || !usersTable) return;

        const filterInput = document.createElement('input');
        filterInput.type = 'text';
        filterInput.placeholder = '🔍 Buscar usuarios...';
        // ✨ MEJORA: Usar clase CSS en lugar de estilos en línea
        filterInput.className = 'header-search-filter';

        header.appendChild(filterInput);

        filterInput.addEventListener('input', function() {
            const filter = this.value.toLowerCase();
            const rows = usersTable.querySelectorAll('tbody tr');

            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(filter) ? '' : 'none';
            });
        });
    }

    // Agregar filtro de búsqueda solo en la página de lista
    if (document.getElementById('usersTable')) {
        addTableFilter();
    }

    console.log('Users management script loaded successfully');
});