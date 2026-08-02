package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.entities.User;
import PaoloF16.BeCapstoneFinal.repository.RoleRepository;
import PaoloF16.BeCapstoneFinal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public List<Role> getAllRoles() { return roleRepository.findAll(); }

    public Role createRole(Role role) {
        // Normalizar el nombre (ej. eliminar espacios al inicio y final)
        String roleName = role.getName().trim();

        if (roleRepository.existsByNameIgnoreCase(roleName)) {
            throw new IllegalArgumentException("El rol '" + roleName + "' ya existe en el sistema.");
        }

        role.setName(roleName);
        return roleRepository.save(role);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Actualizar usuario (Rol, Nombre, PIN, etc.)
    public User updateUser(UUID userId, User updatedData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setName(updatedData.getName());
        user.setRole(updatedData.getRole());
        user.setPosPin(updatedData.getPosPin());

        return userRepository.save(user);
    }

    // Cambiar estado ACTIVE / INACTIVE
    public User toggleUserStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
}