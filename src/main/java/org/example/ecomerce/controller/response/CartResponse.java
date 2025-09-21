package org.example.ecomerce.controller.response;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.ToString
public class CartResponse {
    private boolean success;
    private String message;
    private int cartItemCount;
    private java.math.BigDecimal cartTotal;
}
