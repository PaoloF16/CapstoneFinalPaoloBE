package PaoloF16.BeCapstoneFinal.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double stockQuantity; // Cantidad en stock

    @Column(nullable = false)
    private String unit; // Kg, Gramos, Litros, Unidades

    private Double minStockWarning; // Stock mínimo para alerta
}