package org.example.ecomerce.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecomerce.dto.request.UserRequest;
import org.example.ecomerce.dto.response.ApiResponse;
import org.example.ecomerce.dto.response.UserResponse;
import org.example.ecomerce.model.User;
import org.example.ecomerce.model.UserRole;
import org.example.ecomerce.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {

    private final UserService userService;

    // ========================================
    // LISTAR TODOS LOS USUARIOS
    // ========================================

    @GetMapping
    public ApiResponse<List<UserResponse>> listUsers(HttpSession session) {
        try {
            if (!isCurrentUserAdmin(session)) {
                return ApiResponse.error("Acceso denegado");
            }

            List<User> users = userService.findAll();
            List<UserResponse> userResponses = users.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return ApiResponse.success(userResponses);

        } catch (Exception e) {
            log.error("Error listing users: {}", e.getMessage());
            return ApiResponse.error("Error al listar usuarios: " + e.getMessage());
        }
    }

    // ========================================
    // CREAR USUARIO
    // ========================================

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody UserRequest userRequest, HttpSession session) {
        try {
            if (!isCurrentUserAdmin(session)) {
                return ApiResponse.error("Acceso denegado");
            }

            // Verificar que el email no exista
            if (userService.existsByEmail(userRequest.getEmail())) {
                return ApiResponse.error("Ya existe un usuario con ese email: " + userRequest.getEmail());
            }

            User user = convertToEntity(userRequest);
            User savedUser = userService.save(user);

            return ApiResponse.success("Usuario creado exitosamente", convertToResponse(savedUser));

        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            return ApiResponse.error("Error al crear usuario: " + e.getMessage());
        }
    }

    // ========================================
    // VER DETALLES DE USUARIO
    // ========================================

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> viewUser(@PathVariable Long id, HttpSession session) {
        try {
            if (!isCurrentUserAdmin(session)) {
                return ApiResponse.error("Acceso denegado");
            }

            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("Usuario no encontrado");
            }

            return ApiResponse.success(convertToResponse(userOpt.get()));

        } catch (Exception e) {
            log.error("Error viewing user: {}", e.getMessage());
            return ApiResponse.error("Error al ver usuario: " + e.getMessage());
        }
    }

    // ========================================
    // ACTUALIZAR USUARIO
    // ========================================

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest userRequest,
            HttpSession session) {

        try {
            if (!isCurrentUserAdmin(session)) {
                return ApiResponse.error("Acceso denegado");
            }

            // Verificar que el usuario existe
            Optional<User> existingUserOpt = userService.findById(id);
            if (existingUserOpt.isEmpty()) {
                return ApiResponse.error("Usuario no encontrado");
            }

            User existingUser = existingUserOpt.get();

            // Verificar email único (excepto el propio)
            if (!existingUser.getEmail().equals(userRequest.getEmail()) &&
                    userService.existsByEmail(userRequest.getEmail())) {
                return ApiResponse.error("Ya existe un usuario con ese email: " + userRequest.getEmail());
            }

            // Actualizar campos
            existingUser.setFirstName(userRequest.getFirstName());
            existingUser.setLastName(userRequest.getLastName());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setPhone(userRequest.getPhone());
            existingUser.setAddress(userRequest.getAddress());
            existingUser.setRole(userRequest.getRole());
            existingUser.setActive(userRequest.getActive());

            // Solo actualizar password si se proporcionó uno nuevo
            if (userRequest.getPassword() != null && !userRequest.getPassword().trim().isEmpty()) {
                existingUser.setPassword(userRequest.getPassword());
            }

            User updatedUser = userService.save(existingUser);
            return ApiResponse.success("Usuario actualizado exitosamente", convertToResponse(updatedUser));

        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            return ApiResponse.error("Error al actualizar usuario: " + e.getMessage());
        }
    }

    // ========================================
    // ELIMINAR USUARIO
    // ========================================

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id, HttpSession session) {
        try {
            if (!isCurrentUserAdmin(session)) {
                return ApiResponse.error("Acceso denegado");
            }

            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return ApiResponse.error("Usuario no encontrado");
            }

            userService.deleteById(id);
            return ApiResponse.success("Usuario eliminado exitosamente");

        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ApiResponse.error("Error al eliminar usuario: " + e.getMessage());
        }
    }

    // ========================================
    // OBTENER ROLES DE USUARIO
    // ========================================

    @GetMapping("/roles")
    public ApiResponse<List<UserRole>> getUserRoles() {
        try {
            return ApiResponse.success(List.of(UserRole.values()));
        } catch (Exception e) {
            log.error("Error getting user roles: {}", e.getMessage());
            return ApiResponse.error("Error al obtener roles: " + e.getMessage());
        }
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhone(),
            user.getAddress(),
            user.getRole(),
            user.getActive(),
            user.getFullName()
        );
    }

    private User convertToEntity(UserRequest userRequest) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setPhone(userRequest.getPhone());
        user.setAddress(userRequest.getAddress());
        user.setRole(userRequest.getRole());
        user.setActive(userRequest.getActive());
        return user;
    }

    private boolean isCurrentUserAdmin(HttpSession session) {
        Object userRole = session.getAttribute("userRole");
        return userRole != null && "ADMIN".equals(userRole.toString());
    }
}
