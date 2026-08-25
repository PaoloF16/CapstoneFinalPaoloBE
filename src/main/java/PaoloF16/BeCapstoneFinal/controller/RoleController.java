// src/main/java/PaoloF16/BeCapstoneFinal/controller/RoleController.java
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.repository.RoleRepository;
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

    // 1. Obtener todos los roles
    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // 2. Crear rol validando duplicados con existsByNameIgnoreCase
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
            Role savedRole = roleRepository.save(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error al crear el rol: " + e.getMessage()));
        }
    }

    // 3. Modificar rol existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable UUID id, @RequestBody Role roleDetails) {
        return roleRepository.findById(id).map(role -> {
            if (roleDetails.getName() != null && !roleDetails.getName().trim().isBlank()) {
                role.setName(roleDetails.getName().trim().toUpperCase());
            }
            role.setDescription(roleDetails.getDescription());
            role.setPermissions(roleDetails.getPermissions());
            return ResponseEntity.ok(roleRepository.save(role));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Eliminar rol
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable UUID id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}