/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author PC
 */

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment , Integer>{
Page<Equipment> findByBranchId(int id, Pageable pageable);

Page<Equipment> findByCostBetween(double min, double max, Pageable pageable);
}