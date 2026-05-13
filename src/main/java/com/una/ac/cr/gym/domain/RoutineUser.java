/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 *
 * @author alira
 */
@Entity
@Table(name = "tb_routine_users")
public class RoutineUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRoutineUser;

    private int idRoutine;
    private int idUser;
    private LocalDate assignDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean state;

    public RoutineUser() {
    }

    public RoutineUser(int idRoutineUser, int idRoutine, int idUser,
                       LocalDate assignDate, LocalDate startDate,
                       LocalDate endDate, boolean state) {
        this.idRoutineUser = idRoutineUser;
        this.idRoutine = idRoutine;
        this.idUser = idUser;
        this.assignDate = assignDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
    }

    public int getIdRoutineUser() {
        return idRoutineUser;
    }

    public void setIdRoutineUser(int idRoutineUser) {
        this.idRoutineUser = idRoutineUser;
    }

    public int getIdRoutine() {
        return idRoutine;
    }

    public void setIdRoutine(int idRoutine) {
        this.idRoutine = idRoutine;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public LocalDate getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(LocalDate assignDate) {
        this.assignDate = assignDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}


