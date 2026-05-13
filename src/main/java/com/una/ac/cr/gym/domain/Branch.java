/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;


/**
 *
 * @author sharo
 */
@Entity
@Table(name = "tb_branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100, message = "El nombre no debe superar 100 caracteres.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "La dirección no puede estar vacía.")
    @Size(max = 150, message = "La dirección no debe superar 150 caracteres.")
    @Column(name = "address", nullable = false, length = 150)
    private String address;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "^[0-9]{8}$", message = "El teléfono debe tener 8 dígitos y solo números.")
    @Column(name = "phone", nullable = false, length = 8)
    private String phone;

    @NotNull(message = "La capacidad es obligatoria.")
    @Min(value = 1, message = "La capacidad debe ser mayor a cero.")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull(message = "La fecha de apertura es obligatoria.")
    @PastOrPresent(message = "La fecha de apertura no puede ser futura.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    @NotNull(message = "Debe seleccionar el estado de la sucursal.")
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotBlank(message = "La imagen es obligatoria.")
    @Size(max = 255, message = "La imagen no debe superar 255 caracteres.")
    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    public Branch() {
    }

    public Branch(int id, String name, String address, String phone, Integer capacity,
                  LocalDate openingDate, Boolean active, String imageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.capacity = capacity;
        this.openingDate = openingDate;
        this.active = active;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    } 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    } 

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    } 

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    } 

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    } 

    public boolean isActive() {
        return active != null && active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    } 

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
