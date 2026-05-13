/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



/**
 *
 * @author sharo
 */
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

    Page<Payment> findByIdUser(Integer idUser, Pageable pageable);

    Page<Payment> findByIdUserAndStatusContainingIgnoreCase(
            Integer idUser, String status, Pageable pageable);

    Page<Payment> findByIdUserAndPaymentMethodContainingIgnoreCase(
            Integer idUser, String paymentMethod, Pageable pageable);

    Page<Payment> findByIdUserAndStatusContainingIgnoreCaseAndPaymentMethodContainingIgnoreCase(
            Integer idUser, String status, String paymentMethod, Pageable pageable);
}
