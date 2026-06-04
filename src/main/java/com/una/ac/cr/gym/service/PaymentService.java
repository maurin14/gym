/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Payment;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.repository.PaymentRepository;
import com.una.ac.cr.gym.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



/**
 *
 * @author sharo
 */
@Service
public class PaymentService implements CRUD<Payment> {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public void delete(int id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public List<Payment> getAll() {
        List<Payment> payments = paymentRepository.findAll();
        populateClientNames(payments);
        return payments;
    }

    @Override
    public Payment getById(int id) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        populateClientName(payment);
        return payment;
    }

    public Page<Payment> getPage(String status, String paymentMethod, Integer branchId, Pageable pageable) {
        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasPaymentMethod = paymentMethod != null && !paymentMethod.trim().isEmpty();
        boolean hasBranch = branchId != null;

        if (hasStatus && hasPaymentMethod && hasBranch) {
            return withClientNames(paymentRepository.findByStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCaseAndBranch_Id(
                    status.trim(), paymentMethod.trim(), branchId, pageable));
        }

        if (hasStatus && hasPaymentMethod) {
            return withClientNames(paymentRepository.findByStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCase(
                    status.trim(), paymentMethod.trim(), pageable));
        }

        if (hasStatus && hasBranch) {
            return withClientNames(paymentRepository.findByStatusContainingIgnoreCaseAndBranch_Id(
                    status.trim(), branchId, pageable));
        }

        if (hasPaymentMethod && hasBranch) {
            return withClientNames(paymentRepository.findByPaymentMethodContainingIgnoreCaseAndBranch_Id(
                    paymentMethod.trim(), branchId, pageable));
        }

        if (hasStatus) {
            return withClientNames(paymentRepository.findByStatusContainingIgnoreCase(status.trim(), pageable));
        }

        if (hasPaymentMethod) {
            return withClientNames(paymentRepository.findByPaymentMethodContainingIgnoreCase(paymentMethod.trim(), pageable));
        }

        if (hasBranch) {
            return withClientNames(paymentRepository.findByBranch_Id(branchId, pageable));
        }

        return withClientNames(paymentRepository.findAll(pageable));
    }

    public Page<Payment> getUserPayments(Integer idUser, String status, String paymentMethod, Pageable pageable) {
        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasPaymentMethod = paymentMethod != null && !paymentMethod.trim().isEmpty();

        if (hasStatus && hasPaymentMethod) {
            return withClientNames(paymentRepository.findByIdUserAndStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCase(
                    idUser, status.trim(), paymentMethod.trim(), pageable));
        }

        if (hasStatus) {
            return withClientNames(paymentRepository.findByIdUserAndStatusContainingIgnoreCase(
                    idUser, status.trim(), pageable));
        }

        if (hasPaymentMethod) {
            return withClientNames(paymentRepository.findByIdUserAndPaymentMethodContainingIgnoreCase(
                    idUser, paymentMethod.trim(), pageable));
        }

        return withClientNames(paymentRepository.findByIdUser(idUser, pageable));
    }

    public Map<String, String> validateFields(Payment payment) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (payment == null) {
            errors.put("form", "message.form.review");
            return errors;
        }

        if (payment.getPaymentDate() == null) {
            errors.put("paymentDate", "message.validation.dateRequired");
        } else if (payment.getPaymentDate().isAfter(LocalDate.now())) {
            errors.put("paymentDate", "message.validation.dateValid");
        }

        if (payment.getIdUser() == null) {
            errors.put("idUser", "message.payment.selectClient");
        } else if (payment.getIdUser() < 1) {
            errors.put("idUser", "message.validation.value");
        }

        if (payment.getBranch() == null || payment.getBranch().getId() <= 0) {
            errors.put("branch", "message.validation.select");
        }

        if (payment.getAmount() == null) {
            errors.put("amount", "message.validation.required");
        } else if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("amount", "message.validation.value");
        }

        if (isBlank(payment.getPaymentMethod())) {
            errors.put("paymentMethod", "message.validation.select");
        } else if (!isAllowed(payment.getPaymentMethod(), "Efectivo", "Tarjeta", "SINPE", "Transferencia")) {
            errors.put("paymentMethod", "message.validation.select");
        }

        if (isBlank(payment.getStatus())) {
            errors.put("status", "message.validation.select");
        } else if (!isAllowed(payment.getStatus(), "Pagado", "Pendiente", "Anulado")) {
            errors.put("status", "message.validation.select");
        }

        if (isBlank(payment.getDescription())) {
            errors.put("description", "message.payment.descriptionRequired");
        } else if (payment.getDescription().length() > 255) {
            errors.put("description", "message.validation.max255");
        }

        return errors;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isAllowed(String value, String... allowedValues) {
        for (String allowedValue : allowedValues) {
            if (allowedValue.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private Page<Payment> withClientNames(Page<Payment> payments) {
        populateClientNames(payments.getContent());
        return payments;
    }

    private void populateClientNames(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return;
        }

        Set<Integer> userIds = new HashSet<>();

        for (Payment payment : payments) {
            if (payment != null && payment.getIdUser() != null) {
                userIds.add(payment.getIdUser());
            }
        }

        Map<Integer, User> usersById = new HashMap<>();
        userRepository.findAllById(userIds)
                .forEach(user -> usersById.put(user.getUserId(), user));

        for (Payment payment : payments) {
            if (payment == null || payment.getIdUser() == null) {
                continue;
            }

            User user = usersById.get(payment.getIdUser());
            payment.setClientName(user != null ? user.getFullName() : null);
        }
    }

    private void populateClientName(Payment payment) {
        if (payment == null || payment.getIdUser() == null) {
            return;
        }

        User user = userRepository.findById(payment.getIdUser()).orElse(null);
        payment.setClientName(user != null ? user.getFullName() : null);
    }
}
