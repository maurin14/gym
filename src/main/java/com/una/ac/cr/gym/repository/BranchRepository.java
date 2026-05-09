package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Branch;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Integer> {

    Page<Branch> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Branch> findByActive(boolean active, Pageable pageable);

    Page<Branch> findByProvinceContainingIgnoreCase(String province, Pageable pageable);

    Page<Branch> findByNameContainingIgnoreCaseAndActive(
            String name, boolean active, Pageable pageable);

    Page<Branch> findByNameContainingIgnoreCaseAndProvinceContainingIgnoreCase(
            String name, String province, Pageable pageable);

    Page<Branch> findByProvinceContainingIgnoreCaseAndActive(
            String province, boolean active, Pageable pageable);

    Page<Branch> findByNameContainingIgnoreCaseAndProvinceContainingIgnoreCaseAndActive(
            String name, String province, boolean active, Pageable pageable);

    List<Branch> findTop3ByActiveTrueOrderByIdAsc();

    List<Branch> findByActiveTrue();
}