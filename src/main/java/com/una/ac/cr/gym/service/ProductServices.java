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
            return "Id de producto invalido.";
        }

        if (!productRepository.existsById(idProduct)) {
            return "No se encontro el producto solicitado.";
        }

        productRepository.deleteById(idProduct);
        return "";
    }

    public Map<String, String> validateFields(Product product, boolean isUpdate) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (product == null) {
            errors.put("form", "No se pudo guardar. Revise los campos marcados.");
            return errors;
        }

        if (isUpdate && product.getIdProduct() <= 0) {
            errors.put("form", "No se pudo actualizar el registro.");
        }

        if (isBlank(product.getNameProduct())) {
            errors.put("nameProduct", "Este campo es obligatorio.");
        } else if (product.getNameProduct().trim().length() > 100) {
            errors.put("nameProduct", "Ingrese 100 caracteres o menos.");
        }

        if (isBlank(product.getCategory())) {
            errors.put("category", "Este campo es obligatorio.");
        } else if (product.getCategory().trim().length() > 80) {
            errors.put("category", "Ingrese 80 caracteres o menos.");
        }

        if (product.getPrice() == null) {
            errors.put("price", "Este campo es obligatorio.");
        } else if (product.getPrice() < 0) {
            errors.put("price", "El precio debe ser mayor o igual a 0.");
        }

        if (product.getQuantityStock() == null) {
            errors.put("quantityStock", "Este campo es obligatorio.");
        } else if (product.getQuantityStock() < 0) {
            errors.put("quantityStock", "Ingrese un valor valido.");
        }

        if (product.getRegisterDate() == null) {
            errors.put("registerDate", "La fecha es obligatoria.");
        } else if (product.getRegisterDate().isAfter(LocalDate.now())) {
            errors.put("registerDate", "Ingrese una fecha valida.");
        }

        if (isBlank(product.getDescription())) {
            errors.put("description", "Este campo es obligatorio.");
        } else if (product.getDescription().length() > 255) {
            errors.put("description", "Ingrese 255 caracteres o menos.");
        }

        if (product.getImagePath() != null && product.getImagePath().length() > 255) {
            errors.put("imagePath", "Ingrese 255 caracteres o menos.");
        }

        return errors;
    }

    private String firstError(Map<String, String> errors) {
        return errors.values().iterator().next();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
