/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author alira
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

}
