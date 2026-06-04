/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 *
 * @author alira
 */

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByCategoryContainingIgnoreCaseAndPriceBetween(
            String category,
            double minPrice,
            double maxPrice,
            Pageable pageable
    );

    @Query("""
           SELECT p FROM Product p
           WHERE p.state = true
           AND (:search = '' OR LOWER(p.nameProduct) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))
           AND (:category = '' OR LOWER(p.category) = LOWER(:category))
           """)
    Page<Product> findActiveProductsForClient(
            @Param("search") String search,
            @Param("category") String category,
            Pageable pageable
    );

    @Query("SELECT DISTINCT TRIM(p.category) FROM Product p WHERE p.category IS NOT NULL AND TRIM(p.category) <> '' ORDER BY TRIM(p.category)")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT TRIM(p.category) FROM Product p WHERE p.state = true AND p.category IS NOT NULL AND TRIM(p.category) <> '' ORDER BY TRIM(p.category)")
    List<String> findDistinctActiveCategories();

    @Query("SELECT MIN(p.price) FROM Product p")
    Double findMinPrice();

    @Query("SELECT MAX(p.price) FROM Product p")
    Double findMaxPrice();
}
