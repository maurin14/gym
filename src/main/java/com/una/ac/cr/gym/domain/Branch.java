package com.una.ac.cr.gym.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "tb_branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres.")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "La dirección es obligatoria.")
    @Size(min = 5, max = 150, message = "La dirección debe tener entre 5 y 150 caracteres.")
    @Column(name = "address")
    private String address;

    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "\\d{8}", message = "El teléfono debe tener exactamente 8 dígitos.")
    @Column(name = "phone")
    private String phone;

    @Min(value = 1, message = "La capacidad debe ser mayor o igual a 1.")
    @Max(value = 1000, message = "La capacidad no debe superar 1000 personas.")
    @Column(name = "capacity")
    private int capacity;

    @NotNull(message = "La fecha de apertura es obligatoria.")
    @PastOrPresent(message = "La fecha de apertura no puede ser futura.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "active")
    private boolean active;

    @NotBlank(message = "Debe seleccionar una provincia.")
    @Column(name = "province")
    private String province;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Debe ingresar un correo electrónico válido.")
    @Size(max = 100, message = "El correo no debe superar 100 caracteres.")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "La imagen es obligatoria.")
    @Size(min = 5, max = 255, message = "La imagen debe tener entre 5 y 255 caracteres.")
    @Column(name = "image_url")
    private String imageUrl;

    public Branch() {
    }

    public Branch(int id, String name, String address, String phone, int capacity,
            LocalDate openingDate, boolean active, String province,
            String email, String imageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.capacity = capacity;
        this.openingDate = openingDate;
        this.active = active;
        this.province = province;
        this.email = email;
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
    
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    } 
    
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    } 
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    } 
    
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    } 
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    } 
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}