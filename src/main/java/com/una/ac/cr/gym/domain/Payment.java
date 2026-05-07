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

@Entity
@Table(name = "tb_payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0.")
    @Column(name = "amount")
    private BigDecimal amount;

    @NotNull(message = "La fecha de pago es obligatoria.")
    @PastOrPresent(message = "La fecha de pago no puede ser futura.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @NotBlank(message = "El método de pago es obligatorio.")
    @Column(name = "payment_method")
    private String paymentMethod;

    @NotBlank(message = "El estado es obligatorio.")
    @Column(name = "status")
    private String status;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(min = 5, max = 255, message = "La descripción debe tener entre 5 y 255 caracteres.")
    @Column(name = "description")
    private String description;

    @NotNull(message = "El usuario es obligatorio.")
    @Min(value = 1, message = "El usuario debe ser mayor a 0.")
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    public Payment() {
    }

    public Payment(int id, BigDecimal amount, LocalDate paymentDate, String paymentMethod,
            String status, String description, Integer userId, Branch branch) {
        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.description = description;
        this.userId = userId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}