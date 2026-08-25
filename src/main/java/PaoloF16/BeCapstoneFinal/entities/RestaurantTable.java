package PaoloF16.BeCapstoneFinal.entities;

import PaoloF16.BeCapstoneFinal.enums.TableStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "restaurant_tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Integer tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TableStatus status = TableStatus.AVAILABLE;

    private String imageUrl;
}