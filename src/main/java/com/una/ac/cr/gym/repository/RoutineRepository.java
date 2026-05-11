/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author alira
 */
public interface RoutineRepository extends JpaRepository<Routine, Integer> {

    Page<Routine> findByDifficultyLevel(String difficultyLevel, Pageable pageable);

    Page<Routine> findByRoutineType(String routineType, Pageable pageable);

    Page<Routine> findByDifficultyLevelAndRoutineType(String difficultyLevel, String routineType, Pageable pageable);
}
