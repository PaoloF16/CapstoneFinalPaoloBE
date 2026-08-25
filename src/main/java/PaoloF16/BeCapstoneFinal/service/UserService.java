// src/main/java/PaoloF16/BeCapstoneFinal/service/UserService.java
package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.entities.User;
import PaoloF16.BeCapstoneFinal.repository.RoleRepository;
import PaoloF16.BeCapstoneFinal.repository.UserRepository;
import PaoloF16.BeCapstoneFinal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // --- REGISTRO ---
    public User register(String name, String email, String password, String roleStr) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya se encuentra registrado");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);

        assignRoleToUser(user, roleStr != null ? roleStr : "ADMIN");
        return userRepository.save(user);
    }

    // --- LOGIN ---
    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.isActive()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (user.getPassword() != null && !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String roleStr = (user.getRole() != null && user.getRole().getName() != null)
                ? user.getRole().getName()
                : "ADMIN";

        String token = jwtUtil.generateToken(user.getEmail(), roleStr);

        return Map.of(
                "token", token,
                "id", user.getId().toString(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", roleStr,
                "permissions", (user.getRole() != null && user.getRole().getPermissions() != null)
                        ? user.getRole().getPermissions()
                        : List.of()
        );
    }

    // --- OBTENER TODOS ---
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // --- CREAR USUARIO DESDE GESTIÓN ---
    public User createUserFromPayload(Map<String, Object> payload) {
        String email = (String) payload.get("email");
        if (email == null || email.isBlank()) {
            throw new RuntimeException("El correo electrónico es obligatorio");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya se encuentra registrado");
        }

        User user = new User();
        user.setName((String) payload.get("name"));
        user.setEmail(email);

        String password = (String) payload.get("password");
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        } else {
            user.setPassword(passwordEncoder.encode("123456")); // Password por defecto
        }

        user.setPosPin((String) payload.get("posPin"));
        user.setActive(payload.containsKey("active") ? (Boolean) payload.get("active") : true);

        Object roleObj = payload.get("role");
        if (roleObj != null) {
            assignRoleToUser(user, roleObj.toString());
        }

        return userRepository.save(user);
    }

    // --- EDITAR USUARIO (EVITA SOBREESCRIBIR EMAIL CON NULL) ---
    public User updateUserFromPayload(UUID id, Map<String, Object> payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (payload.containsKey("name") && payload.get("name") != null) {
            user.setName((String) payload.get("name"));
        }

        if (payload.containsKey("email") && payload.get("email") != null && !((String) payload.get("email")).isBlank()) {
            user.setEmail((String) payload.get("email"));
        }

        if (payload.containsKey("posPin")) {
            user.setPosPin((String) payload.get("posPin"));
        }

        if (payload.containsKey("active") && payload.get("active") != null) {
            user.setActive((Boolean) payload.get("active"));
        }

        if (payload.containsKey("password") && payload.get("password") != null && !((String) payload.get("password")).isBlank()) {
            user.setPassword(passwordEncoder.encode((String) payload.get("password")));
        }

        // Asignación segura de rol por ID o por Nombre
        if (payload.containsKey("role") && payload.get("role") != null) {
            assignRoleToUser(user, payload.get("role").toString());
        }

        return userRepository.save(user);
    }

    // --- ACTIVAR / DESACTIVAR USUARIO ---
    public User toggleUserStatus(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    // --- ELIMINAR USUARIO ---
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    // --- HELPER PARA ASIGNAR ROL ---
    private void assignRoleToUser(User user, String roleIdentifier) {
        if (roleRepository == null || roleIdentifier == null) return;

        // Limpiar el texto si viene como JSON o string formateado
        String cleanIdentifier = roleIdentifier.replace("{", "").replace("}", "").replace("name=", "").trim();

        // Buscar por ID UUID o por Nombre
        roleRepository.findAll().stream()
                .filter(r -> r.getId().toString().equalsIgnoreCase(cleanIdentifier)
                        || r.getName().equalsIgnoreCase(cleanIdentifier))
                .findFirst()
                .ifPresent(user::setRole);
    }
}