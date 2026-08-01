package PaoloF16.BeCapstoneFinal.service;



import PaoloF16.BeCapstoneFinal.entities.Category;
import PaoloF16.BeCapstoneFinal.entities.Product;
import PaoloF16.BeCapstoneFinal.repository.CategoryRepository;
import PaoloF16.BeCapstoneFinal.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MenuService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // Métodos para Categorías
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Métodos para Productos
    public List<Product> getAllProducts(UUID categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
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
        product.setCategory(productDetails.getCategory());

        return productRepository.save(product);
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    public Product toggleAvailability(UUID id, Boolean isAvailable) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con el id: " + id));
        product.setIsAvailable(isAvailable);
        return productRepository.save(product);
    }


    @Transactional
    public void deleteCategory(UUID id) {
        // 1. Primero borras los productos vinculados a esta categoría
        productRepository.deleteByCategoryId(id);

        // 2. Luego borras la categoría
        categoryRepository.deleteById(id);
    }
}