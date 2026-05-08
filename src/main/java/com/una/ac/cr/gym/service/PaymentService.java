package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Payment;
import com.una.ac.cr.gym.repository.PaymentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements CRUD<Payment> {

    @Autowired
    private PaymentRepository paymentRepository;

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
        return paymentRepository.findAll();
    }

    @Override
    public Payment getById(int id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Page<Payment> getPage(String status, String paymentMethod, Integer branchId, Pageable pageable) {

        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasPaymentMethod = paymentMethod != null && !paymentMethod.trim().isEmpty();
        boolean hasBranch = branchId != null;

        if (hasStatus && hasPaymentMethod && hasBranch) {
            return paymentRepository.findByStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCaseAndBranch_Id(
                    status.trim(), paymentMethod.trim(), branchId, pageable);
        }

        if (hasStatus && hasPaymentMethod) {
            return paymentRepository.findByStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCase(
                    status.trim(), paymentMethod.trim(), pageable);
        }

        if (hasStatus && hasBranch) {
            return paymentRepository.findByStatusContainingIgnoreCaseAndBranch_Id(
                    status.trim(), branchId, pageable);
        }

        if (hasPaymentMethod && hasBranch) {
            return paymentRepository.findByPaymentMethodContainingIgnoreCaseAndBranch_Id(
                    paymentMethod.trim(), branchId, pageable);
        }

        if (hasStatus) {
            return paymentRepository.findByStatusContainingIgnoreCase(status.trim(), pageable);
        }

        if (hasPaymentMethod) {
            return paymentRepository.findByPaymentMethodContainingIgnoreCase(paymentMethod.trim(), pageable);
        }

        if (hasBranch) {
            return paymentRepository.findByBranch_Id(branchId, pageable);
        }

        return paymentRepository.findAll(pageable);
    }

    public Page<Payment> getUserPayments(Integer userId, String status, String paymentMethod, Pageable pageable) {

        boolean hasStatus = status != null && !status.trim().isEmpty();
        boolean hasPaymentMethod = paymentMethod != null && !paymentMethod.trim().isEmpty();

        if (hasStatus && hasPaymentMethod) {
            return paymentRepository.findByUserIdAndStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCase(
                    userId, status.trim(), paymentMethod.trim(), pageable);
        }

        if (hasStatus) {
            return paymentRepository.findByUserIdAndStatusContainingIgnoreCase(userId, status.trim(), pageable);
        }

        if (hasPaymentMethod) {
            return paymentRepository.findByUserIdAndPaymentMethodContainingIgnoreCase(userId, paymentMethod.trim(), pageable);
        }

        return paymentRepository.findByUserId(userId, pageable);
    }
}