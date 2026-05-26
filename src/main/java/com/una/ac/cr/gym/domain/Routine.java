package com.una.ac.cr.gym.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_routines")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_routine")
    private int idRoutine;

    @Column(name = "name_routine")
    private String nameRoutine;

    @Column(name = "description")
    private String description;

    @Column(name = "difficulty_level")
    private String difficultyLevel;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(name = "training_objective")
    private String trainingObjective;

    @Column(name = "quantity_exercises")
    private Integer quantityExercises;

    @Column(name = "routine_type")
    private String routineType;

    @Column(name = "exercises")
    private String exercises;

    @Column(name = "state")
    private boolean state;

    public Routine() {
    }

    public Routine(int idRoutine, String nameRoutine, String description,
                   String difficultyLevel, Integer estimatedDuration,
                   String trainingObjective, Integer quantityExercises,
                   String routineType, String exercises, boolean state) {
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

    public Integer getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Integer estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public String getTrainingObjective() {
        return trainingObjective;
    }

    public void setTrainingObjective(String trainingObjective) {
        this.trainingObjective = trainingObjective;
    }

    public Integer getQuantityExercises() {
        return quantityExercises;
    }

    public void setQuantityExercises(Integer quantityExercises) {
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
