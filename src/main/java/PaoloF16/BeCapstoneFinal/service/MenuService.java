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

    // 💡 ACTUALIZACIÓN SEGURA DE PLATO
    @Transactional
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

        // Validar y reasignar la categoría si fue modificada
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDetails.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id: " + productDetails.getCategory().getId()));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        orderItemRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    public Product toggleAvailability(UUID id, Boolean isAvailable) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el id: " + id));
        product.setIsAvailable(isAvailable);
        return productRepository.save(product);
    }
}