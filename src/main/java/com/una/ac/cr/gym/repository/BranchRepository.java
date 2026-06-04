/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Branch;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



/**
 *
 * @author sharo
 */
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    Page<Branch> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Branch> findByActive(Boolean active, Pageable pageable);

    Page<Branch> findByIdAndActive(int id, Boolean active, Pageable pageable);

    Page<Branch> findById(int id, Pageable pageable);

    Page<Branch> findByNameContainingIgnoreCaseAndActive(String name, Boolean active, Pageable pageable);

    List<Branch> findByActiveTrue();

    @Query("""
           SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
           FROM Equipment e
           WHERE e.branch.id = :branchId
           """)
    boolean existsEquipmentByBranchId(@Param("branchId") int branchId);
}
