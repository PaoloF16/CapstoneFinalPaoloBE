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
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        menuService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public Product toggleAvailability(@PathVariable UUID id, @RequestBody Map<String, Boolean> body) {
        return menuService.toggleAvailability(id, body.get("isAvailable"));
    }
}