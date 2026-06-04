package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Product;
import com.una.ac.cr.gym.repository.ProductRepository;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServices {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(int idProduct) {
        if (idProduct <= 0) {
            return null;
        }

        return productRepository.findById(idProduct).orElse(null);
    }

    public Page<Product> getProductsFiltered(String category, Double minPrice, Double maxPrice, Pageable pageable) {
        if (category == null) {
            category = "";
        }

        if (minPrice == null) {
            minPrice = 0.0;
        }

        if (maxPrice == null) {
            maxPrice = Double.MAX_VALUE;
        }

        return productRepository.findByCategoryContainingIgnoreCaseAndPriceBetween(
                category,
                minPrice,
                maxPrice,
                pageable
        );
    }

    public Page<Product> getClientProducts(String search, String category, Pageable pageable) {
        if (search == null) {
            search = "";
        }

        if (category == null) {
            category = "";
        }

        return productRepository.findActiveProductsForClient(search.trim(), category.trim(), pageable);
    }

    public Double getMinProductPrice() {
        Double min = productRepository.findMinPrice();
        return min != null ? min : 0.0;
    }

    public Double getMaxProductPrice() {
        Double max = productRepository.findMaxPrice();
        return max != null ? max : 0.0;
    }

    public List<String> getCategories() {
        return productRepository.findDistinctCategories();
    }

    public List<String> getActiveCategories() {
        return productRepository.findDistinctActiveCategories();
    }

    public String saveProduct(Product product) {
        Map<String, String> errors = validateFields(product, false);

        if (!errors.isEmpty()) {
            return firstError(errors);
        }

        productRepository.save(product);
        return "";
    }

    public Map<String, String> validateProductForm(Product product, String newCategory, String existingCategory, boolean isUpdate) {
        Map<String, String> errors = validateCategorySelection(product, newCategory, existingCategory);
        Map<String, String> fieldErrors = validateFields(product, isUpdate);

        if (errors.containsKey("categorySelection")) {
            fieldErrors.remove("category");
        }

        errors.putAll(fieldErrors);
        return errors;
    }

    public String updateProduct(Product product) {
        Map<String, String> errors = validateFields(product, true);

        if (!errors.isEmpty()) {
            return firstError(errors);
        }

        productRepository.save(product);
        return "";
    }

    public String deleteProduct(int idProduct) {
        if (idProduct <= 0) {
            return "message.product.invalidId";
        }

        if (!productRepository.existsById(idProduct)) {
            return "message.product.notFound";
        }

        productRepository.deleteById(idProduct);
        return "";
    }

    public Map<String, String> validateFields(Product product, boolean isUpdate) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (product == null) {
            errors.put("form", "message.form.review");
            return errors;
        }

        if (isUpdate && product.getIdProduct() <= 0) {
            errors.put("form", "message.form.updateError");
        }

        if (isBlank(product.getNameProduct())) {
            errors.put("nameProduct", "message.validation.required");
        } else if (product.getNameProduct().trim().length() > 100) {
            errors.put("nameProduct", "message.validation.max100");
        }

        if (isBlank(product.getCategory())) {
            errors.put("category", "message.validation.required");
        } else if (product.getCategory().trim().length() > 80) {
            errors.put("category", "message.validation.max80");
        }

        if (product.getPrice() == null) {
            errors.put("price", "message.validation.required");
        } else if (product.getPrice() < 0) {
            errors.put("price", "message.validation.priceMin");
        }

        if (product.getQuantityStock() == null) {
            errors.put("quantityStock", "message.validation.required");
        } else if (product.getQuantityStock() < 0) {
            errors.put("quantityStock", "message.validation.value");
        }

        if (product.getRegisterDate() == null) {
            errors.put("registerDate", "message.validation.dateRequired");
        } else if (product.getRegisterDate().isAfter(LocalDate.now())) {
            errors.put("registerDate", "message.validation.dateValid");
        }

        if (isBlank(product.getDescription())) {
            errors.put("description", "message.validation.required");
        } else if (product.getDescription().length() > 255) {
            errors.put("description", "message.validation.max255");
        }

        if (product.getImagePath() != null && product.getImagePath().length() > 255) {
            errors.put("imagePath", "message.validation.max255");
        }

        return errors;
    }

    private Map<String, String> validateCategorySelection(Product product, String newCategory, String existingCategory) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (product == null) {
            errors.put("form", "message.form.review");
            return errors;
        }

        boolean hasNewCategory = !isBlank(newCategory);
        boolean hasExistingCategory = !isBlank(existingCategory);

        if (!hasNewCategory && !hasExistingCategory) {
            errors.put("categorySelection", "product.validation.category.required");
            product.setCategory(null);
            return errors;
        }

        if (hasNewCategory && hasExistingCategory) {
            errors.put("categorySelection", "product.validation.category.exclusive");
            return errors;
        }

        String selectedCategory = hasNewCategory ? newCategory.trim() : existingCategory.trim();
        String normalizedCategory = findCanonicalCategory(selectedCategory);

        if (normalizedCategory.length() > 80) {
            errors.put("categorySelection", "message.validation.max80");
            return errors;
        }

        product.setCategory(normalizedCategory);
        return errors;
    }

    private String findCanonicalCategory(String category) {
        String normalizedCategory = category.trim();

        for (String existingCategory : getCategories()) {
            if (!isBlank(existingCategory) && existingCategory.trim().equalsIgnoreCase(normalizedCategory)) {
                return existingCategory.trim();
            }
        }

        return normalizedCategory;
    }

    private String firstError(Map<String, String> errors) {
        return errors.values().iterator().next();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
