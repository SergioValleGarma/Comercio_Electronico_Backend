package org.example.ecomerce.service;

import org.example.ecomerce.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    Optional<User> authenticate(String email, String password);
}