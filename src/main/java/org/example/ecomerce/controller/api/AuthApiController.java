package org.example.ecomerce.controller.api;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecomerce.dto.request.LoginRequest;
import org.example.ecomerce.dto.response.ApiResponse;
import org.example.ecomerce.dto.response.AuthResponse;
import org.example.ecomerce.model.User;
import org.example.ecomerce.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        var userOpt = userService.authenticate(request.getEmail(), request.getPassword());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userFullName", user.getFullName());
            session.setAttribute("userRole", user.getRole().toString());
            
            AuthResponse response = new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().toString(),
                "Login exitoso"
            );
            
            return ApiResponse.success(response);
        }
        
        return ApiResponse.error("Credenciales inválidas");
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.success("Sesión cerrada exitosamente", null);
    }

    @GetMapping("/session")
    public ApiResponse<AuthResponse> checkSession(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId != null) {
            AuthResponse response = new AuthResponse(
                (Long) userId,
                (String) session.getAttribute("userEmail"),
                (String) session.getAttribute("userFullName"),
                (String) session.getAttribute("userRole"),
                "Sesión activa"
            );
            return ApiResponse.success(response);
        }
        return ApiResponse.error("No hay sesión activa");
    }
}