package PaoloF16.BeCapstoneFinal.dto;

import lombok.Data;

@Data
public class CheckoutRequestDTO {
    // Descuento opcional
    private Double discountValue; // Ej: 10.0 o 5000.0 (según el tipo)
    private String discountType;  // "PERCENTAGE" o "FIXED"
}