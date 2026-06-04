/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.repository.BranchRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;



/**
 *
 * @author sharo
 */
@Service
public class BranchService implements CRUD<Branch> {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void ensureBranchSchemaMatchesDomain() {
        try {
            String columnType = jdbcTemplate.queryForObject("""
                    SELECT COLUMN_TYPE
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = DATABASE()
                      AND TABLE_NAME = 'tb_branches'
                      AND COLUMN_NAME = 'province'
                      AND IS_NULLABLE = 'NO'
                    """, String.class);

            jdbcTemplate.execute("ALTER TABLE tb_branches MODIFY COLUMN province " + columnType + " NULL");
        } catch (EmptyResultDataAccessException ex) {
            // The column does not exist or already allows nulls, so the domain mapping is compatible.
        }
    }

    @Override
    public void save(Branch branch) {
        branchRepository.save(branch);
    }

    @Override
    public void delete(int id) {
        if (hasEquipments(id)) {
            throw new DataIntegrityViolationException("La sucursal tiene equipos asociados.");
        }

        branchRepository.deleteById(id);
    }

    public boolean hasEquipments(int id) {
        return branchRepository.existsEquipmentByBranchId(id);
    }

    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    public List<Branch> getActiveBranches() {
        return branchRepository.findByActiveTrue();
    }

    @Override
    public Branch getById(int id) {
        return branchRepository.findById(id).orElse(null);
    }

    public Page<Branch> getPage(String name, String active, Pageable pageable) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasActive = active != null && !active.trim().isEmpty();

        if (hasName && hasActive) {
            return branchRepository.findByNameContainingIgnoreCaseAndActive(
                    name.trim(), Boolean.parseBoolean(active), pageable);
        }

        if (hasName) {
            return branchRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }

        if (hasActive) {
            return branchRepository.findByActive(Boolean.parseBoolean(active), pageable);
        }

        return branchRepository.findAll(pageable);
    }

    public Page<Branch> getAdminPage(Integer branchId, String active, Pageable pageable) {
        boolean hasBranch = branchId != null && branchId > 0;
        boolean hasActive = active != null && !active.trim().isEmpty();

        if (hasBranch && hasActive) {
            return branchRepository.findByIdAndActive(
                    branchId, Boolean.parseBoolean(active), pageable);
        }

        if (hasBranch) {
            return branchRepository.findById(branchId, pageable);
        }

        if (hasActive) {
            return branchRepository.findByActive(Boolean.parseBoolean(active), pageable);
        }

        return branchRepository.findAll(pageable);
    }

    public Map<String, String> validateFields(Branch branch) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (branch == null) {
            errors.put("form", "message.form.review");
            return errors;
        }

        if (isBlank(branch.getImageUrl())) {
            errors.put("imageUrl", "message.validation.required");
        } else if (branch.getImageUrl().trim().length() > 255) {
            errors.put("imageUrl", "message.validation.max255");
        }

        if (branch.getOpeningDate() == null) {
            errors.put("openingDate", "message.validation.dateRequired");
        } else if (branch.getOpeningDate().isAfter(LocalDate.now())) {
            errors.put("openingDate", "message.validation.dateValid");
        }

        if (isBlank(branch.getName())) {
            errors.put("name", "message.validation.required");
        } else if (branch.getName().trim().length() > 100) {
            errors.put("name", "message.validation.max100");
        }

        if (isBlank(branch.getAddress())) {
            errors.put("address", "message.validation.required");
        } else if (branch.getAddress().trim().length() > 150) {
            errors.put("address", "message.validation.max150");
        }

        if (isBlank(branch.getPhone())) {
            errors.put("phone", "message.validation.required");
        } else if (!branch.getPhone().matches("^[0-9]{8}$")) {
            errors.put("phone", "message.validation.value");
        }

        if (isBlank(branch.getEmail())) {
            errors.put("email", "branch.validation.email.required");
        } else if (!branch.getEmail().trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.put("email", "branch.validation.email.invalid");
        } else if (branch.getEmail().trim().length() > 120) {
            errors.put("email", "message.validation.max120");
        } else {
            branch.setEmail(branch.getEmail().trim());
        }

        if (branch.getCapacity() == null) {
            errors.put("capacity", "message.validation.required");
        } else if (branch.getCapacity() < 1) {
            errors.put("capacity", "message.validation.value");
        }

        if (branch.getActive() == null) {
            errors.put("active", "message.validation.select");
        }

        return errors;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
