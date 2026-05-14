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
import java.util.List;
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
    }
