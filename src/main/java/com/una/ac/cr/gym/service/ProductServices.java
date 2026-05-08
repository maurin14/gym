/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Product;
import com.una.ac.cr.gym.repository.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author alira
 */
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

    public String saveProduct(Product product) {
        String validation = validateProduct(product, false);

        if (!validation.isEmpty()) {
            return validation;
        }

        productRepository.save(product);
        return "";
    }

    public String updateProduct(Product product) {
        String validation = validateProduct(product, true);

        if (!validation.isEmpty()) {
            return validation;
        }

        productRepository.save(product);
        return "";
    }

    public String deleteProduct(int idProduct) {
        if (idProduct <= 0) {
            return "Id de producto inválido.";
        }

        if (!productRepository.existsById(idProduct)) {
            return "No se encontró el producto solicitado.";
        }

        productRepository.deleteById(idProduct);
        return "";
    }

    private String validateProduct(Product product, boolean isUpdate) {
        if (product == null) {
            return "El producto es inválido.";
        }

        if (isUpdate && product.getIdProduct() <= 0) {
            return "Id de producto inválido.";
        }

        if (product.getNameProduct() == null || product.getNameProduct().trim().isEmpty()) {
            return "El nombre del producto es obligatorio.";
        }

        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            return "La categoría es obligatoria.";
        }

        if (product.getPrice() <= 0) {
            return "El precio debe ser mayor a 0.";
        }

        if (product.getQuantityStock() < 0) {
            return "La cantidad en stock no puede ser negativa.";
        }

        if (product.getRegisterDate() == null) {
            return "La fecha de registro es obligatoria.";
        }

        if (product.getRegisterDate().isAfter(LocalDate.now())) {
            return "La fecha de registro no puede ser futura.";
        }

        if (product.getDescription() != null && product.getDescription().length() > 255) {
            return "La descripción no puede exceder los 255 caracteres.";
        }

        if (product.getImagePath() != null && product.getImagePath().length() > 255) {
            return "La ruta de la imagen es demasiado larga.";
        }

        return "";
    }
}