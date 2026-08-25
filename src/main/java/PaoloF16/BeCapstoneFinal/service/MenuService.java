// src/main/java/PaoloF16/BeCapstoneFinal/service/MenuService.java
package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.Category;
import PaoloF16.BeCapstoneFinal.entities.Product;
import PaoloF16.BeCapstoneFinal.repository.CategoryRepository;
import PaoloF16.BeCapstoneFinal.repository.OrderItemRepository;
import PaoloF16.BeCapstoneFinal.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MenuService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // --- MÉTODOS DE CATEGORÍAS ---
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        List<Product> products = productRepository.findByCategoryId(id);
        for (Product p : products) {
            orderItemRepository.deleteByProductId(p.getId());
        }
        productRepository.deleteByCategoryId(id);
        categoryRepository.deleteById(id);
    }

    // --- MÉTODOS DE PRODUCTOS ---
    public List<Product> getAllProducts(UUID categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + product.getCategory().getId()));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(UUID id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el id: " + id));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setOriginalPrice(productDetails.getOriginalPrice());
        product.setImageUrl(productDetails.getImageUrl());
        product.setIsAvailable(productDetails.getIsAvailable());
        product.setIsGlutenFree(productDetails.getIsGlutenFree());
        product.setIsNew(productDetails.getIsNew());
        product.setDiscountBadge(productDetails.getDiscountBadge());

        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDetails.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    // 💡 ELIMINACIÓN SEGURA CON TRANSACCIÓN
    @Transactional
    public void deleteProduct(UUID id) {
        // 1. Eliminar referencias del producto en items de comandas
        orderItemRepository.deleteByProductId(id);

        // 2. Eliminar el plato de la carta
        productRepository.deleteById(id);
    }

    public Product toggleAvailability(UUID id, Boolean isAvailable) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el id: " + id));
        product.setIsAvailable(isAvailable);
        return productRepository.save(product);
    }
}