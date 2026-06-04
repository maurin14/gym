package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Product;
import com.una.ac.cr.gym.service.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/client/products")
public class ClientProductController {

    @Autowired
    private ProductServices productService;

    @GetMapping({"", "/"})
    public String showProductCatalog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String category,
            Model model) {

        addProductCatalogAttributes(model, page, size, search, category);
        model.addAttribute("title", "title.product.catalog");
        return "client/product_catalog";
    }

    @GetMapping("/fragment")
    public String showProductCatalogFragment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String category,
            Model model) {

        addProductCatalogAttributes(model, page, size, search, category);
        return "client/fragments/product_cards :: productResults";
    }

    @GetMapping("/{id}")
    public String showProductDetail(@PathVariable("id") int idProduct, Model model) {
        Product product = productService.getProduct(idProduct);

        if (product == null || !product.isState()) {
            return "redirect:/client/products";
        }

        model.addAttribute("title", "title.product.detail");
        model.addAttribute("product", product);
        return "client/product_detail";
    }

    private void addProductCatalogAttributes(Model model, int page, int size, String search, String category) {
        int pageSize = normalizeSize(size);
        int currentPage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Product> productPage = productService.getClientProducts(search, category, pageable);

        if (currentPage >= productPage.getTotalPages() && productPage.getTotalPages() > 0) {
            currentPage = productPage.getTotalPages() - 1;
            pageable = PageRequest.of(currentPage, pageSize);
            productPage = productService.getClientProducts(search, category, pageable);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("categories", productService.getActiveCategories());
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return 6;
        }

        return Math.min(size, 12);
    }
}
