/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Branch;
import com.una.ac.cr.gym.domain.Schedule;
import com.una.ac.cr.gym.repository.BranchRepository;
import com.una.ac.cr.gym.repository.ScheduleRepository;
import java.time.LocalTime;
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
public class ScheduleService implements CRUD<Schedule>{
@Autowired
private ScheduleRepository scheduleData;
@Autowired
private BranchRepository branchRe;
    @Override
    public void save(Schedule t) {
        normalizeBranch(t);
        scheduleData.save(t);
    }

    @Override
    public void delete(int id) {
        scheduleData.deleteById(id);
    }

    @Override
    public List<Schedule> getAll() {
return scheduleData.findAll();
    }



    public void update(int id, Schedule t) {
    Schedule existing = scheduleData.findById(id).orElse(null);

    
    if (existing == null) {
        System.out.println("Schedule no existe");
        return;
    }

    
    existing.setDayOfWeek(t.getDayOfWeek());
    existing.setStartTime(t.getStartTime());
    existing.setEndTime(t.getEndTime());
    existing.setScheduleType(t.getScheduleType());
    existing.setActive(t.isActive());
    
    if (t.getBranch() != null) {
        Branch branch = branchRe
                .findById(t.getBranch().getId())
                .orElse(null);

        if (branch != null) {
            existing.setBranch(branch);
        }
    }

    scheduleData.save(existing);
    }

    public void toggleStatus(int id) {
        Schedule existing = scheduleData.findById(id).orElse(null);

        if (existing == null) {
            return;
        }

        existing.setActive(!existing.isActive());
        scheduleData.save(existing);
    }

    public Map<String, String> validate(Schedule schedule) {
        Map<String, String> fieldErrors = new HashMap<>();

        if (schedule == null) {
            fieldErrors.put("scheduleType", "Complete la informacion del horario.");
            return fieldErrors;
        }

        if (schedule.getBranch() == null || schedule.getBranch().getId() <= 0) {
            fieldErrors.put("branch.id", "Seleccione una sucursal.");
        } else if (branchRe.findById(schedule.getBranch().getId()).isEmpty()) {
            fieldErrors.put("branch.id", "La sucursal seleccionada no existe.");
        }

        if (schedule.getDayOfWeek() == null) {
            fieldErrors.put("dayOfWeek", "Seleccione el dia del horario.");
        }

        if (schedule.getStartTime() == null) {
            fieldErrors.put("startTime", "La hora de inicio es obligatoria.");
        }

        if (schedule.getEndTime() == null) {
            fieldErrors.put("endTime", "La hora de fin es obligatoria.");
        }

        if (schedule.getStartTime() != null
                && schedule.getEndTime() != null
                && !schedule.getEndTime().isAfter(schedule.getStartTime())) {
            fieldErrors.put("endTime", "La hora de fin debe ser mayor que la hora de inicio.");
        }

        if (schedule.getScheduleType() == null || schedule.getScheduleType().trim().isEmpty()) {
            fieldErrors.put("scheduleType", "El tipo de horario es obligatorio.");
        }

        return fieldErrors;
    }
    public Page<Schedule> getbyId(Integer i,Pageable pageable) {
        return scheduleData.findByBranchId(i,pageable);
    }
    public Page<Schedule> findByBranchIdAndStartTimeBetween( LocalTime start, LocalTime end, Pageable pageable) {
        return scheduleData.findByStartTimeBetween( start, end, pageable);
    }
 public Page<Schedule> getAll(Pageable pageable) {
    return scheduleData.findAll(pageable);
}

        @Override
        public Schedule getById(int id) {
            return (Schedule)scheduleData.findById(id).orElse(null);        }

    private void normalizeBranch(Schedule schedule) {
        if (schedule.getBranch() == null || schedule.getBranch().getId() <= 0) {
            schedule.setBranch(null);
            return;
        }

        Branch branch = branchRe.findById(schedule.getBranch().getId()).orElse(null);
        schedule.setBranch(branch);
    }
    }
