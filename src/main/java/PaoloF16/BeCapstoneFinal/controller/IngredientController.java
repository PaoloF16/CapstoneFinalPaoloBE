// src/main/java/PaoloF16/BeCapstoneFinal/controller/IngredientController.java
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Ingredient;
import PaoloF16.BeCapstoneFinal.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "http://localhost:5173")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAll() {
        return ingredientService.getAllIngredients();
    }

    @PostMapping
    public Ingredient create(@RequestBody Ingredient ingredient) {
        return ingredientService.createIngredient(ingredient);
    }

    @PutMapping("/{id}")
    public Ingredient update(@PathVariable UUID id, @RequestBody Ingredient ingredient) {
        return ingredientService.updateIngredient(id, ingredient);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        ingredientService.deleteIngredient(id);
    }
}