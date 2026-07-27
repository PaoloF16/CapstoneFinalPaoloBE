
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Category;
import PaoloF16.BeCapstoneFinal.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}