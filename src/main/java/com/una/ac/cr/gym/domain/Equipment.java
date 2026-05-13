/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author PC
 */
@Entity
@Table(name = "Equipment" )

public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
     private int id;
    private String name;
    private String type;
    private String state;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd") //Para que la fecha se pase bien
    private LocalDate purchaseDate;
    
    
    private double cost;
    private String available;
   @ManyToOne
@JoinColumn(name = "branch_id")
    private Branch branch;
 public Equipment( String name, String tipe, String state, LocalDate purchaseDate, double cost, String available, Branch branch) {
        this.id = 0;
        this.name = name;
        this.type = tipe;
        this.state = state;
        this.purchaseDate = purchaseDate;
        this.cost = cost;
        this.available = available;
        this.branch = branch;
    }
    public Equipment(int id, String name, String tipe, String state, LocalDate purchaseDate, double cost, String available, Branch branch) {
        this.id = id;
        this.name = name;
        this.type = tipe;
        this.state = state;
        this.purchaseDate = purchaseDate;
        this.cost = cost;
        this.available = available;
        this.branch = branch;
    }

    public Equipment() {
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public double getCost() {
        return cost;
    }

    public String getAvailable() {
        return available;
    }
}