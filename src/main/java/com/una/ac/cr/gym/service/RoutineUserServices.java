/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.RoutineUser;
import com.una.ac.cr.gym.repository.RoutineUserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author alira
 */
@Service
public class RoutineUserServices {

    @Autowired
    private RoutineUserRepository routineUserRepository;

    public List<RoutineUser> getAssignments() {
        return routineUserRepository.findAll();
    }

    public RoutineUser getAssignment(int idRoutineUser) {
        if (idRoutineUser <= 0) {
            return null;
        }
        return routineUserRepository.findById(idRoutineUser).orElse(null);
    }

    public String saveAssignment(RoutineUser routineUser) {

        if (routineUser == null) {
            return "La asignación es nula";
        }

        if (routineUser.getIdRoutine() <= 0) {
            return "Rutina inválida";
        }

        if (routineUser.getIdUser() <= 0) {
            return "Usuario inválido";
        }

        routineUserRepository.save(routineUser);
        return "";
    }

    public String deleteAssignment(int idRoutineUser) {

        RoutineUser assignment = getAssignment(idRoutineUser);

        if (assignment == null) {
            return "Asignación no encontrada";
        }

        assignment.setState(false);
        routineUserRepository.save(assignment);

        return "";
    }
}