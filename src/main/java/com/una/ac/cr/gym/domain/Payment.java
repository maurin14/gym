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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;



/**
 *
 * @author sharo
 */

@Entity
@Table(name = "tb_payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero.")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @NotNull(message = "La fecha de pago es obligatoria.")
    @PastOrPresent(message = "La fecha de pago no puede ser futura.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotBlank(message = "Debe seleccionar un método de pago.")
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @NotBlank(message = "Debe seleccionar el estado del pago.")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Size(max = 255, message = "La descripción no debe superar 255 caracteres.")
    @Column(name = "description", length = 255)
    private String description;

    @NotNull(message = "Debe seleccionar un cliente.")
    @Min(value = 1, message = "Debe seleccionar un cliente válido.")
    @Column(name = "id_user", nullable = false)
    private Integer idUser;

    @NotNull(message = "Debe seleccionar una sucursal.")
    @ManyToOne
    @JoinColumn(name = "id_branch", nullable = false)
    private Branch branch;

    public Payment() {
    }

    public Payment(int id, BigDecimal amount, LocalDate paymentDate, String paymentMethod,
                   String status, String description, Integer idUser, Branch branch) {
        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.description = description;
        this.idUser = idUser;
        this.branch = branch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    } 

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    } 

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    } 

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    } 

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    } 

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    } 

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    } 

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
