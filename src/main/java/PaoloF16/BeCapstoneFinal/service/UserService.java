// src/main/java/PaoloF16/BeCapstoneFinal/service/UserService.java
package PaoloF16.BeCapstoneFinal.service;

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

    // --- REGISTRO DE USUARIOS ---
    public User register(String name, String email, String password, String roleStr) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo ya se encuentra registrado");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);

        String targetRole = (roleStr != null && !roleStr.isBlank())
                ? roleStr.trim().toUpperCase()
                : "ADMIN";

        // Comparación segura como String con equalsIgnoreCase
        if (roleRepository != null) {
            roleRepository.findAll().stream()
                    .filter(r -> r.getName() != null && r.getName().toString().equalsIgnoreCase(targetRole))
                    .findFirst()
                    .ifPresent(user::setRole);
        }

        return userRepository.save(user);
    }

    // --- AUTENTICACIÓN / LOGIN ---
    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.isActive()) {
            throw new RuntimeException("Usuario inactivo");
        }

        if (user.getPassword() != null && !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Obtener el rol directamente como String (sin llamar a .name())
        String roleStr = (user.getRole() != null && user.getRole().getName() != null)
                ? user.getRole().getName().toString()
                : "ADMIN";

        String token = jwtUtil.generateToken(user.getEmail(), roleStr);

        return Map.of(
                "token", token,
                "id", user.getId().toString(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", roleStr,
                "permissions", List.of()
        );
    }

    // --- MÉTODOS DE USER CONTROLLER ---
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User updateUser(UUID id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPosPin(userDetails.getPosPin());
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepository.save(user);
    }

    public User toggleUserStatus(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
}