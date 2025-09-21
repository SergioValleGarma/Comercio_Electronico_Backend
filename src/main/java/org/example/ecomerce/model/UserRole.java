package org.example.ecomerce.model;

public enum UserRole {
    CUSTOMER("Cliente"),
    ADMIN("Administrador");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // NO sobrescribir toString() para que Spring use los valores enum originales
    // @Override
    // public String toString() {
    //     return displayName;
    // }
}