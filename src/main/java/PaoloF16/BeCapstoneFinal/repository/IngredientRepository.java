// src/main/java/PaoloF16/BeCapstoneFinal/repository/IngredientRepository.java
package PaoloF16.BeCapstoneFinal.repository;

import PaoloF16.BeCapstoneFinal.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
}