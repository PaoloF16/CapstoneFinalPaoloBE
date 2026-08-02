package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:5173")
public class RoleController {

    @Autowired
    private UserService userService;

    // Obtener todos los roles disponibles
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = userService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // Crear un nuevo rol (ej: ADMINISTRADOR, GARZON, CAJERO, COCINA)
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role newRole = userService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }
}