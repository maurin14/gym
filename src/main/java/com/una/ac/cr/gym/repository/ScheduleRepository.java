/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Schedule;
import java.time.LocalTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author PC
 */

public interface ScheduleRepository extends JpaRepository<Schedule, Integer>{
    Page<Schedule> findByBranchId(Integer branchId, Pageable pageable);
    Page<Schedule> findByStartTimeBetween(LocalTime start, LocalTime end, Pageable pageable);
}
