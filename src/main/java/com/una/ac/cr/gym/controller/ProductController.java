/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Product;
import com.una.ac.cr.gym.service.ProductServices;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author alira
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductServices productService;

    @GetMapping({"", "/"})
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model
    ) {

        Double minProductPrice = productService.getMinProductPrice();
        Double maxProductPrice = productService.getMaxProductPrice();

        if (minPrice == null) {
            minPrice = minProductPrice;
        }

        if (maxPrice == null) {
            maxPrice = maxProductPrice;
        }

        Pageable pageable = PageRequest.of(page, 5);

        Page<Product> productPage = productService.getProductsFiltered(
                category,
                minPrice,
                maxPrice,
                pageable
        );

        model.addAttribute("title", "Lista de productos");
        model.addAttribute("products", productPage.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        model.addAttribute("category", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        model.addAttribute("categories", productService.getCategories());

        return "product/product_list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setState(true);

        model.addAttribute("title", "Agregar producto");
        model.addAttribute("product", product);
        model.addAttribute("action", "/products/save");
        return "product/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(Product product,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        product.setRegisterDate(LocalDate.now());
        product.setState(true);

        if (!imageFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + File.separator
                        + "src" + File.separator
                        + "main" + File.separator
                        + "resources" + File.separator
                        + "static" + File.separator
                        + "images" + File.separator
                        + "products";

                String originalFileName = imageFile.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + originalFileName;

                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File destinationFile = new File(directory, fileName);
                imageFile.transferTo(destinationFile);

                product.setImagePath("images/products/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("title", "Agregar producto");
                model.addAttribute("product", product);
                model.addAttribute("action", "/products/save");
                model.addAttribute("error", "Error al guardar la imagen: " + e.getMessage());
                return "product/product_form";
            }
        }

        String result = productService.saveProduct(product);

        if (!result.isEmpty()) {
            model.addAttribute("title", "Agregar producto");
            model.addAttribute("product", product);
            model.addAttribute("action", "/products/save");
            model.addAttribute("error", result);
            return "product/product_form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Producto guardado correctamente.");
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int idProduct, Model model) {
        Product product = productService.getProduct(idProduct);

        if (product == null) {
            model.addAttribute("title", "Lista de productos");
            model.addAttribute("products", productService.getProducts());
            model.addAttribute("error", "No se encontró el producto solicitado.");
            return "product/product_list";
        }

        model.addAttribute("title", "Editar producto");
        model.addAttribute("product", product);
        model.addAttribute("action", "/products/update");
        return "product/product_form";
    }

    @PostMapping("/update")
    public String updateProduct(Product product,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        Product currentProduct = productService.getProduct(product.getIdProduct());

        if (currentProduct == null) {
            model.addAttribute("title", "Lista de productos");
            model.addAttribute("products", productService.getProducts());
            model.addAttribute("error", "No se encontró el producto solicitado.");
            return "product/product_list";
        }

        product.setRegisterDate(currentProduct.getRegisterDate());

        if (!imageFile.isEmpty()) {
            try {
                String uploadDir = System.getProperty("user.dir") + File.separator
                        + "src" + File.separator
                        + "main" + File.separator
                        + "resources" + File.separator
                        + "static" + File.separator
                        + "images" + File.separator
                        + "products";

                String originalFileName = imageFile.getOriginalFilename();
                String fileName = System.currentTimeMillis() + "_" + originalFileName;

                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File destinationFile = new File(directory, fileName);
                imageFile.transferTo(destinationFile);

                product.setImagePath("images/products/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("title", "Editar producto");
                model.addAttribute("product", product);
                model.addAttribute("action", "/products/update");
                model.addAttribute("error", "Error al guardar la imagen: " + e.getMessage());
                return "product/product_form";
            }
        }

        String result = productService.updateProduct(product);

        if (!result.isEmpty()) {
            model.addAttribute("title", "Editar producto");
            model.addAttribute("product", product);
            model.addAttribute("action", "/products/update");
            model.addAttribute("error", result);
            return "product/product_form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Producto editado correctamente.");
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") int idProduct, Model model, RedirectAttributes redirectAttributes) {
        String result = productService.deleteProduct(idProduct);

        if (!result.isEmpty()) {
            model.addAttribute("title", "Lista de productos");
            model.addAttribute("products", productService.getProducts());
            model.addAttribute("error", result);
            return "product/product_list";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado correctamente.");
        return "redirect:/products";
    }

    @GetMapping("/details/{id}")
    public String showProductDetails(@PathVariable("id") int idProduct, Model model) {
        Product product = productService.getProduct(idProduct);

        if (product == null) {
            model.addAttribute("title", "Lista de productos");
            model.addAttribute("products", productService.getProducts());
            model.addAttribute("error", "No se encontró el producto solicitado.");
            return "product/product_list";
        }

        model.addAttribute("title", "Detalle del producto");
        model.addAttribute("product", product);
        return "product/product_details";
    }
}
