// src/main/java/PaoloF16/BeCapstoneFinal/controller/RoleController.java
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.entities.User;
import PaoloF16.BeCapstoneFinal.repository.RoleRepository;
import PaoloF16.BeCapstoneFinal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:5173")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        try {
            if (role.getName() == null || role.getName().trim().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El nombre del rol es obligatorio"));
            }

            String formattedName = role.getName().trim().toUpperCase();
            if (roleRepository.existsByNameIgnoreCase(formattedName)) {
                return ResponseEntity.badRequest().body(Map.of("message", "El rol '" + formattedName + "' ya existe"));
            }

            role.setName(formattedName);
            Role saved = roleRepository.save(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable UUID id, @RequestBody Role roleDetails) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            if (roleDetails.getName() != null && !roleDetails.getName().trim().isBlank()) {
                role.setName(roleDetails.getName().trim().toUpperCase());
            }
            role.setDescription(roleDetails.getDescription());
            if (roleDetails.getPermissions() != null) {
                role.setPermissions(roleDetails.getPermissions());
            }

            return ResponseEntity.ok(roleRepository.save(role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable UUID id) {
        try {
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            String roleName = role.getName().toUpperCase();
            if (roleName.equals("SUPER_ADMIN") || roleName.equals("ADMIN")) {
                return ResponseEntity.badRequest().body(Map.of("message", "No se pueden eliminar los roles base del sistema"));
            }

            // 1. Desvincular usuarios asociados para permitir la eliminación sin error de Foreign Key
            List<User> usersWithRole = userRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && u.getRole().getId().equals(id))
                    .toList();
            for (User u : usersWithRole) {
                u.setRole(null);
                userRepository.save(u);
            }

            // 2. Eliminar el rol
            roleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}