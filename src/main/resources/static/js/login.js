// Login JavaScript Functions
document.addEventListener('DOMContentLoaded', function() {

    // Elementos del DOM
    const loginForm = document.getElementById('loginForm');
    const loginBtn = document.getElementById('loginBtn');
    const btnText = document.getElementById('btnText');
    const btnSpinner = document.getElementById('btnSpinner');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const demoUsers = document.querySelectorAll('.demo-user');

    // Auto-rellenar con usuarios demo
    demoUsers.forEach(user => {
        user.addEventListener('click', function() {
            const email = this.getAttribute('data-email');
            const password = this.getAttribute('data-password');

            emailInput.value = email;
            passwordInput.value = password;

            // Efecto visual
            emailInput.focus();
            setTimeout(() => passwordInput.focus(), 100);

            // Highlight temporal
            this.style.background = 'rgba(102, 126, 234, 0.2)';
            setTimeout(() => {
                this.style.background = '';
            }, 300);
        });
    });

    // Manejo del envío del formulario
    loginForm.addEventListener('submit', function(e) {
        // Mostrar spinner
        showLoading();

        // Validación básica
        if (!emailInput.value.trim() || !passwordInput.value.trim()) {
            e.preventDefault();
            hideLoading();
            showError('Por favor completa todos los campos');
            return;
        }

        // Validar formato de email
        if (!isValidEmail(emailInput.value)) {
            e.preventDefault();
            hideLoading();
            showError('Por favor ingresa un email válido');
            return;
        }
    });

    // Función para mostrar estado de carga
    function showLoading() {
        loginBtn.disabled = true;
        btnText.style.display = 'none';
        btnSpinner.style.display = 'inline';
    }

    // Función para ocultar estado de carga
    function hideLoading() {
        loginBtn.disabled = false;
        btnText.style.display = 'inline';
        btnSpinner.style.display = 'none';
    }

    // Validar formato de email
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // Mostrar mensaje de error
    function showError(message) {
        // Remover alertas existentes
        const existingAlerts = document.querySelectorAll('.alert');
        existingAlerts.forEach(alert => alert.remove());

        // Crear nueva alerta
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-error';
        alertDiv.innerHTML = `<span>${message}</span>`;

        // Insertar antes del formulario
        const loginBox = document.querySelector('.login-box');
        loginBox.insertBefore(alertDiv, loginForm);

        // Auto-remover después de 5 segundos
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.remove();
            }
        }, 5000);
    }

    // Enter key navigation
    emailInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            passwordInput.focus();
        }
    });

    passwordInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            loginForm.submit();
        }
    });

    // Auto-focus en el campo email al cargar
    setTimeout(() => {
        emailInput.focus();
    }, 100);

    // Efecto de shake en caso de error (si existe alerta de error al cargar)
    const errorAlert = document.querySelector('.alert-error');
    if (errorAlert) {
        const loginBox = document.querySelector('.login-box');
        loginBox.style.animation = 'shake 0.5s ease-in-out';

        setTimeout(() => {
            loginBox.style.animation = '';
        }, 500);
    }
});

// Agregar animación de shake al CSS
const shakeKeyframes = `
@keyframes shake {
    0%, 100% { transform: translateX(0); }
    25% { transform: translateX(-5px); }
    75% { transform: translateX(5px); }
}
`;

// Insertar keyframes dinámicamente
const style = document.createElement('style');
style.textContent = shakeKeyframes;
document.head.appendChild(style);