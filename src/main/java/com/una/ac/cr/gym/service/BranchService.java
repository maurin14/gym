/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.repository.BranchRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



/**
 *
 * @author sharo
 */
@Service
public class BranchService implements CRUD<Branch> {

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public void save(Branch branch) {
        branchRepository.save(branch);
    }

    @Override
    public void delete(int id) {
        branchRepository.deleteById(id);
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
}