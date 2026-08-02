package PaoloF16.BeCapstoneFinal.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String posPin;

    private boolean active = true; // Para la opción de "Desactivar"

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPosPin() { return posPin; }
    public void setPosPin(String posPin) { this.posPin = posPin; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}