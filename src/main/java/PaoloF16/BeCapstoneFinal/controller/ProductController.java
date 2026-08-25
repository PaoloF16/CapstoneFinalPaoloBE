// src/main/java/PaoloF16/BeCapstoneFinal/controller/ProductController.java
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Product;
import PaoloF16.BeCapstoneFinal.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public List<Product> getProducts(@RequestParam(required = false) UUID categoryId) {
        return menuService.getAllProducts(categoryId);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return menuService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable UUID id, @RequestBody Product product) {
        return menuService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        try {
            menuService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error al eliminar el producto: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/availability")
    public Product toggleAvailability(@PathVariable UUID id, @RequestBody Map<String, Boolean> body) {
        return menuService.toggleAvailability(id, body.get("isAvailable"));
    }
}