package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.repository.BranchRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Override
    public Branch getById(int id) {
        return branchRepository.findById(id).orElse(null);
    }

    public List<Branch> getTopActiveBranches() {
        return branchRepository.findTop3ByActiveTrueOrderByIdAsc();
    }

    public List<Branch> getActiveBranches() {
        return branchRepository.findByActiveTrue();
    }

    public Page<Branch> getPage(String name, String active, String province, Pageable pageable) {

        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasActive = active != null && !active.trim().isEmpty();
        boolean hasProvince = province != null && !province.trim().isEmpty();

        if (hasName && hasProvince && hasActive) {
            return branchRepository.findByNameContainingIgnoreCaseAndProvinceContainingIgnoreCaseAndActive(
                    name.trim(), province.trim(), Boolean.parseBoolean(active), pageable);
        }

        if (hasName && hasProvince) {
            return branchRepository.findByNameContainingIgnoreCaseAndProvinceContainingIgnoreCase(
                    name.trim(), province.trim(), pageable);
        }

        if (hasName && hasActive) {
            return branchRepository.findByNameContainingIgnoreCaseAndActive(
                    name.trim(), Boolean.parseBoolean(active), pageable);
        }

        if (hasProvince && hasActive) {
            return branchRepository.findByProvinceContainingIgnoreCaseAndActive(
                    province.trim(), Boolean.parseBoolean(active), pageable);
        }

        if (hasName) {
            return branchRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }

        if (hasProvince) {
            return branchRepository.findByProvinceContainingIgnoreCase(province.trim(), pageable);
        }

        if (hasActive) {
            return branchRepository.findByActive(Boolean.parseBoolean(active), pageable);
        }

        return branchRepository.findAll(pageable);
    }
}