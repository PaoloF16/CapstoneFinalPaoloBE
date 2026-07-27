// src/main/java/PaoloF16/BeCapstoneFinal/service/IngredientService.java
package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.Ingredient;
import PaoloF16.BeCapstoneFinal.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public Ingredient updateIngredient(UUID id, Ingredient details) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingrediente no encontrado: " + id));

        ingredient.setName(details.getName());
        ingredient.setStockQuantity(details.getStockQuantity());
        ingredient.setUnit(details.getUnit());
        ingredient.setMinStockWarning(details.getMinStockWarning());

        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(UUID id) {
        ingredientRepository.deleteById(id);
    }
}