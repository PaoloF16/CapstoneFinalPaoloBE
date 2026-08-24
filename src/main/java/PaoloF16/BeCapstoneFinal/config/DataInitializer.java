// src/main/java/PaoloF16/BeCapstoneFinal/config/DataInitializer.java
package PaoloF16.BeCapstoneFinal.config;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {

    public static final Set<String> ALL_PERMISSIONS = Set.of(
            "TABLES_GET", "TABLES_POST", "TABLES_PUT", "TABLES_DELETE",
            "MENU_GET", "MENU_POST", "MENU_PUT", "MENU_DELETE",
            "ORDERS_GET", "ORDERS_POST", "ORDERS_PUT", "ORDERS_DELETE",
            "INVENTORY_GET", "INVENTORY_POST", "INVENTORY_PUT", "INVENTORY_DELETE",
            "USERS_GET", "USERS_POST", "USERS_PUT", "USERS_DELETE",
            "ROLES_GET", "ROLES_POST", "ROLES_PUT", "ROLES_DELETE"
    );

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // 1. SUPER_ADMIN (Todos los permisos)
            if (!roleRepository.existsByNameIgnoreCase("SUPER_ADMIN")) {
                Role superAdmin = Role.builder()
                        .name("SUPER_ADMIN")
                        .description("Acceso total al sistema y configuraciones críticas")
                        .permissions(new HashSet<>(ALL_PERMISSIONS))
                        .build();
                roleRepository.save(superAdmin);
            }

            // 2. ADMIN (Permisos operativos y de gestión)
            if (!roleRepository.existsByNameIgnoreCase("ADMIN")) {
                Role admin = Role.builder()
                        .name("ADMIN")
                        .description("Administrador del restaurante con permisos de gestión")
                        .permissions(new HashSet<>(ALL_PERMISSIONS))
                        .build();
                roleRepository.save(admin);
            }
        };
    }
}