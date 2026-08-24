// src/main/java/PaoloF16/BeCapstoneFinal/service/UserService.java (Añadir o actualizar métodos de Role)
package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.entities.User;
import PaoloF16.BeCapstoneFinal.repository.RoleRepository;
import PaoloF16.BeCapstoneFinal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
        String roleName = role.getName().trim().toUpperCase();

        if (roleRepository.existsByNameIgnoreCase(roleName)) {
            throw new IllegalArgumentException("El rol '" + roleName + "' ya existe en el sistema.");
        }

        role.setName(roleName);
        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        return roleRepository.save(role);
    }

    public Role updateRole(UUID id, Role roleData) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + id));

        role.setDescription(roleData.getDescription());
        if (roleData.getPermissions() != null) {
            role.setPermissions(roleData.getPermissions());
        }
        return roleRepository.save(role);
    }

    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        if ("SUPER_ADMIN".equalsIgnoreCase(role.getName()) || "ADMIN".equalsIgnoreCase(role.getName())) {
            throw new IllegalArgumentException("No se pueden eliminar los roles del sistema base.");
        }
        roleRepository.delete(role);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(UUID userId, User updatedData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setName(updatedData.getName());
        user.setRole(updatedData.getRole());
        user.setPosPin(updatedData.getPosPin());

        return userRepository.save(user);
    }

    public User toggleUserStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setActive(!user.isActive());
        return userRepository.save(user);
    }
}