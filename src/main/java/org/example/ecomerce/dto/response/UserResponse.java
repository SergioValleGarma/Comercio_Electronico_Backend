package org.example.ecomerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.ecomerce.model.UserRole;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private UserRole role;
    private Boolean active;
    private String fullName;
}
