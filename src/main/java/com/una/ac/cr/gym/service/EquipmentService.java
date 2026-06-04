/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.Equipment;
import com.una.ac.cr.gym.repository.BranchRepository;
import com.una.ac.cr.gym.repository.EquipmentRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author PC
 */
@Service
public class EquipmentService implements CRUD<Equipment>  {
@Autowired
private EquipmentRepository equipmentData;
@Autowired
private BranchRepository branchRepository;

    @Override
    public void save(Equipment t) {
        normalizeBranch(t);
        equipmentData.save(t);
    }

    @Override
    public void delete(int id) {
        equipmentData.deleteById(id);
    }

    @Override
    public List<Equipment> getAll() {
       return  equipmentData.findAll();
    }

 

    public void update(int id, Equipment t) {

    
    Equipment existing = equipmentData.findById(id).orElse(null);

    if (existing == null) {
        System.out.println("Equipo no existe");
        return;
    }

    existing.setName(t.getName());
    existing.setType(t.getType());
    existing.setState(t.getState());
    existing.setPurchaseDate(t.getPurchaseDate());
    existing.setCost(t.getCost());
    existing.setAvailable(t.getAvailable());

       if (t.getBranch() != null) {
        Branch branch = branchRepository
                .findById(t.getBranch().getId())
                .orElse(null);

        if (branch != null) {
            existing.setBranch(branch);
        }
    }

    equipmentData.save(existing);
    }

    public void toggleAvailability(int id) {
        Equipment existing = equipmentData.findById(id).orElse(null);

        if (existing == null) {
            return;
        }

        existing.setAvailable("true".equals(existing.getAvailable()) ? "false" : "true");
        equipmentData.save(existing);
    }

    public Map<String, String> validate(Equipment equipment) {
        Map<String, String> fieldErrors = new HashMap<>();

        if (equipment == null) {
            fieldErrors.put("name", "message.equipment.formIncomplete");
            return fieldErrors;
        }

        if (equipment.getName() == null || equipment.getName().trim().isEmpty()) {
            fieldErrors.put("name", "equipment.name.required");
        }

        if (equipment.getType() == null || equipment.getType().trim().isEmpty()) {
            fieldErrors.put("type", "equipment.type.required");
        }

        if (equipment.getState() == null || equipment.getState().trim().isEmpty()) {
            fieldErrors.put("state", "equipment.physicalCondition.required");
        }

        if (equipment.getPurchaseDate() == null) {
            fieldErrors.put("purchaseDate", "equipment.purchaseDate.required");
        }

        if (equipment.getCost() <= 0) {
            fieldErrors.put("cost", "equipment.cost.positive");
        }

        if (equipment.getAvailable() == null || equipment.getAvailable().trim().isEmpty()) {
            fieldErrors.put("available", "equipment.availability.required");
        }

        if (equipment.getBranch() == null || equipment.getBranch().getId() <= 0) {
            fieldErrors.put("branch.id", "equipment.branch.required");
        } else if (branchRepository.findById(equipment.getBranch().getId()).isEmpty()) {
            fieldErrors.put("branch.id", "equipment.branch.notFound");
        }

        return fieldErrors;
    }
    public Page<Equipment> getAll(Pageable pageable){
    return equipmentData.findAll(pageable);
}

public Page<Equipment> findByBranchId(int id, Pageable pageable) {
    return equipmentData.findByBranchId(id, pageable);
}

public Page<Equipment> findByCostBetween(double min, double max, Pageable pageable) {
    return equipmentData.findByCostBetween(min, max, pageable);
}

 
    public Equipment getById(int id) {
           return (Equipment) equipmentData.findById(id).orElse(null);
    }

    private void normalizeBranch(Equipment equipment) {
        if (equipment.getBranch() == null || equipment.getBranch().getId() <= 0) {
            equipment.setBranch(null);
            return;
        }

        Branch branch = branchRepository
                .findById(equipment.getBranch().getId())
                .orElse(null);

        equipment.setBranch(branch);
    }
   
}
