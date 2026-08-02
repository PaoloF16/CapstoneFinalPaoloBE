package PaoloF16.BeCapstoneFinal.config;

import PaoloF16.BeCapstoneFinal.entities.Role;
import PaoloF16.BeCapstoneFinal.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            List<String> defaultRoles = List.of("ADMINISTRADOR", "MESERO", "CAJERO", "COCINA");

            for (String roleName : defaultRoles) {
                if (!roleRepository.existsByNameIgnoreCase(roleName)) {
                    Role role = new Role();
                    role.setName(roleName);
                    role.setDescription("Rol por defecto de " + roleName);
                    roleRepository.save(role);
                }
            }
        };
    }
}