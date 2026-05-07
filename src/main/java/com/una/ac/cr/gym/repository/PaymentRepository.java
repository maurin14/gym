package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Page<Payment> findByStatusContainingIgnoreCase(String status, Pageable pageable);

    Page<Payment> findByPaymentMethodContainingIgnoreCase(String paymentMethod, Pageable pageable);

    Page<Payment> findByBranch_Id(Integer branchId, Pageable pageable);

    Page<Payment> findByStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCase(
            String status, String paymentMethod, Pageable pageable);

    Page<Payment> findByStatusContainingIgnoreCaseAndBranch_Id(
            String status, Integer branchId, Pageable pageable);

    Page<Payment> findByPaymentMethodContainingIgnoreCaseAndBranch_Id(
            String paymentMethod, Integer branchId, Pageable pageable);

    Page<Payment> findByStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCaseAndBranch_Id(
            String status, String paymentMethod, Integer branchId, Pageable pageable);

    Page<Payment> findByUserId(Integer userId, Pageable pageable);

    Page<Payment> findByUserIdAndStatusContainingIgnoreCase(
            Integer userId, String status, Pageable pageable);

    Page<Payment> findByUserIdAndPaymentMethodContainingIgnoreCase(
            Integer userId, String paymentMethod, Pageable pageable);

    Page<Payment> findByUserIdAndStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCase(
            Integer userId, String status, String paymentMethod, Pageable pageable);
}