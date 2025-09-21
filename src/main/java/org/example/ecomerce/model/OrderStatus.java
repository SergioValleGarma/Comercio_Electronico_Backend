package org.example.ecomerce.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pendiente"),
    CONFIRMED("Confirmada"),
    PROCESSING("En Proceso"),
    SHIPPED("Enviada"),
    DELIVERED("Entregada"),
    CANCELLED("Cancelada");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
}