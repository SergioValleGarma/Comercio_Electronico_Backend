package org.example.ecomerce.controller;

import org.example.ecomerce.model.User;
import org.example.ecomerce.model.UserRole;
import org.example.ecomerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        // Si ya está logueado, redirigir según su rol
        if (session.getAttribute("userId") != null) {
            String userRole = (String) session.getAttribute("userRole");
            if ("ADMIN".equals(userRole)) {
                return "redirect:/users";
            } else {
                return "redirect:/products";
            }
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        var userOpt = userService.authenticate(email, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Guardar en sesión
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userFullName", user.getFullName());
            session.setAttribute("userRole", user.getRole().toString());

            redirectAttributes.addFlashAttribute("success",
                    "¡Bienvenido, " + user.getFirstName() + "!");

            // Redirigir según el rol del usuario
            if (user.getRole() == UserRole.ADMIN) {
                return "redirect:/users";  // Admin va a gestión de usuarios
            } else {
                return "redirect:/products";  // Cliente va a productos
            }

        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Email o contraseña incorrectos");
            return "redirect:/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Sesión cerrada");
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            // Redirigir según el rol del usuario
            String userRole = (String) session.getAttribute("userRole");
            if ("ADMIN".equals(userRole)) {
                return "redirect:/users";
            } else {
                return "redirect:/products";
            }
        }
        return "redirect:/login";
    }
}

