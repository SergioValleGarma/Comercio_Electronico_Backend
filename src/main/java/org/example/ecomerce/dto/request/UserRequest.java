package org.example.ecomerce.dto.request;

import lombok.Data;
import org.example.ecomerce.model.UserRole;

@Data
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private UserRole role;
    private Boolean active = true;
}