
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Category;
import PaoloF16.BeCapstoneFinal.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public List<Category> getCategories() {
        return menuService.getAllCategories();
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return menuService.createCategory(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        // Nota: Si tu ID en Java es String en vez de UUID, cambia "UUID id" por "String id"
        menuService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}