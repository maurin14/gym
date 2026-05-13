/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.domain;

/**
 *
 * @author Amanda
 */


import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tb_classes")
public class GymClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_class")
    private int idClass;

    @Column(name = "class_type")
    private String classType;

    @Column(name = "class_date")
    private LocalDate classDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "max_capacity")
    private int maxCapacity;

    @ManyToOne
    @JoinColumn(name = "trainer_id", referencedColumnName = "userId")
    private User trainer;

    @Column(name = "duration")
    private int duration;

    @Column(name = "description")
    private String description;

    @Column(name = "enrolled_count")
    private int enrolledCount;

    @Column(name = "difficulty_level")
    private String difficultyLevel;

    @Column(name = "status")
    private boolean status;

    public GymClass() {
    }

    public int getIdClass() {
        return idClass;
    }

    public void setIdClass(int idClass) {
        this.idClass = idClass;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public LocalDate getClassDate() {
        return classDate;
    }

    public void setClassDate(LocalDate classDate) {
        this.classDate = classDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public User getTrainer() {
        return trainer;
    }

    public void setTrainer(User trainer) {
        this.trainer = trainer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}