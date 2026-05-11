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

/**
 *
 * @author alira
 */
@Entity
@Table(name = "tb_routines")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRoutine;

    private String nameRoutine;
    private String description;
    private String difficultyLevel;
    private int estimatedDuration;
    private String trainingObjective;
    private int quantityExercises;
    private String routineType;
    private String exercises;
    private boolean state;

    public Routine() {
    }

    public Routine(int idRoutine, String nameRoutine, String description,
                   String difficultyLevel, int estimatedDuration,
                   String trainingObjective,
                   int quantityExercises, String routineType,
                   String exercises, boolean state) {
        this.idRoutine = idRoutine;
        this.nameRoutine = nameRoutine;
        this.description = description;
        this.difficultyLevel = difficultyLevel;
        this.estimatedDuration = estimatedDuration;
        this.trainingObjective = trainingObjective;
        this.quantityExercises = quantityExercises;
        this.routineType = routineType;
        this.exercises = exercises;
        this.state = state;
    }

    public int getIdRoutine() {
        return idRoutine;
    }

    public void setIdRoutine(int idRoutine) {
        this.idRoutine = idRoutine;
    }

    public String getNameRoutine() {
        return nameRoutine;
    }

    public void setNameRoutine(String nameRoutine) {
        this.nameRoutine = nameRoutine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public String getTrainingObjective() {
        return trainingObjective;
    }

    public void setTrainingObjective(String trainingObjective) {
        this.trainingObjective = trainingObjective;
    }

    public int getQuantityExercises() {
        return quantityExercises;
    }

    public void setQuantityExercises(int quantityExercises) {
        this.quantityExercises = quantityExercises;
    }

    public String getRoutineType() {
        return routineType;
    }

    public void setRoutineType(String routineType) {
        this.routineType = routineType;
    }

    public String getExercises() {
        return exercises;
    }

    public void setExercises(String exercises) {
        this.exercises = exercises;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
