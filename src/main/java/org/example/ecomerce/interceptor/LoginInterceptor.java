package org.example.ecomerce.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        // Rutas que NO necesitan login
        if (uri.equals("/login") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.equals("/")) {
            return true;
        }

        // Verificar si está logueado
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            return true;
        }

        // Si no está logueado, redirigir
        response.sendRedirect("/login");
        return false;
    }
}
