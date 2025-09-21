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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Listar todos los usuarios
     */
    @GetMapping
    public String listUsers(Model model, HttpSession session) {
        // Verificar si es admin (opcional, comentar si no tienes login implementado)
        // if (!isCurrentUserAdmin(session)) {
        //     return "redirect:/products?error=Acceso denegado";
        // }

        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());

        return "users/list";
    }

    /**
     * Formulario para crear usuario
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", UserRole.values());
        return "users/create";
    }

    /**
     * Crear usuario
     */
    @PostMapping("/create")
    public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            // Verificar que el email no exista
            if (userService.existsByEmail(user.getEmail())) {
                redirectAttributes.addFlashAttribute("error",
                        "Ya existe un usuario con ese email: " + user.getEmail());
                return "redirect:/users/create";
            }

            // Guardar usuario
            userService.save(user);
            redirectAttributes.addFlashAttribute("success",
                    "Usuario creado exitosamente: " + user.getFullName());

            return "redirect:/users";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al crear usuario: " + e.getMessage());
            return "redirect:/users/create";
        }
    }

    /**
     * Ver detalles de usuario
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findById(id);

        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "users/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/users";
        }
    }

    /**
     * Formulario para editar usuario
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findById(id);

        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("canChangeRole", true); // Cambiar según tu lógica de permisos
            return "users/edit";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/users";
        }
    }

    /**
     * Actualizar usuario
     */
    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User user,
                             RedirectAttributes redirectAttributes) {
        try {
            // Verificar que el usuario existe
            Optional<User> existingUserOpt = userService.findById(id);
            if (existingUserOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/users";
            }

            User existingUser = existingUserOpt.get();

            // Verificar email único (excepto el propio)
            if (!existingUser.getEmail().equals(user.getEmail()) &&
                    userService.existsByEmail(user.getEmail())) {
                redirectAttributes.addFlashAttribute("error",
                        "Ya existe un usuario con ese email: " + user.getEmail());
                return "redirect:/users/" + id + "/edit";
            }

            // Actualizar campos
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            existingUser.setRole(user.getRole());
            existingUser.setActive(user.getActive());

            // Solo actualizar password si se proporcionó uno nuevo
            if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
                existingUser.setPassword(user.getPassword());
            }

            // Guardar cambios
            userService.save(existingUser);

            redirectAttributes.addFlashAttribute("success",
                    "Usuario actualizado exitosamente: " + existingUser.getFullName());

            return "redirect:/users";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar usuario: " + e.getMessage());
            return "redirect:/users/" + id + "/edit";
        }
    }

    /**
     * Eliminar usuario
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findById(id);

            if (userOpt.isPresent()) {
                String userName = userOpt.get().getFullName();
                userService.deleteById(id);
                redirectAttributes.addFlashAttribute("success",
                        "Usuario eliminado exitosamente: " + userName);
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar usuario: " + e.getMessage());
        }

        return "redirect:/users";
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Verificar si el usuario actual es admin (descomenta cuando tengas login)
     */
    /*
    private boolean isCurrentUserAdmin(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        return isAdmin != null && isAdmin;
    }
    */
}