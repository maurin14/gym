package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Product;
import com.una.ac.cr.gym.service.ProductServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client/products")
public class ClientProductController {

    @Autowired
    private ProductServices productService;

    @GetMapping({"", "/"})
    public String showProductCatalog(Model model) {
        List<Product> products = productService.getProducts()
                .stream()
                .filter(Product::isState)
                .toList();

        model.addAttribute("title", "Catálogo de productos");
        model.addAttribute("products", products);
        return "client/product_catalog";
    }

    @GetMapping("/{id}")
    public String showProductDetail(@PathVariable("id") int idProduct, Model model) {
        Product product = productService.getProduct(idProduct);

        if (product == null || !product.isState()) {
            return "redirect:/client/products";
        }

        model.addAttribute("title", "Detalle del producto");
        model.addAttribute("product", product);
        return "client/product_detail";
    }
}
