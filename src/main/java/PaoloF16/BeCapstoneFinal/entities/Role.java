// src/main/java/PaoloF16/BeCapstoneFinal/entities/Role.java
package PaoloF16.BeCapstoneFinal.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // ej: "SUPER_ADMIN", "ADMIN", "MESERO", etc.

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    @Builder.Default
    private Set<String> permissions = new HashSet<>();
}