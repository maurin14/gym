/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

/**
 *
 * @author Amanda
 */


import com.una.ac.cr.gym.domain.GymClass;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GymClassRepository extends JpaRepository<GymClass, Integer> {
    List<GymClass> findByTrainer_UserId(Integer trainerId);

    Page<GymClass> findByTrainer_UserId(Integer trainerId, Pageable pageable);

    @Query("""
           SELECT gc FROM GymClass gc
           WHERE gc.trainer IS NOT NULL
           AND gc.trainer.userId = :trainerId
           AND (
               (gc.branch IS NOT NULL AND gc.branch.id = :branchId)
               OR (gc.branch IS NULL AND gc.trainer.branch IS NOT NULL
                   AND gc.trainer.branch.id = :branchId)
           )
           """)
    List<GymClass> findByTrainerAndBranch(@Param("trainerId") Integer trainerId,
            @Param("branchId") int branchId);

    @Query("""
           SELECT gc FROM GymClass gc
           WHERE gc.trainer IS NOT NULL
           AND gc.trainer.userId = :trainerId
           AND (
               (gc.branch IS NOT NULL AND gc.branch.id = :branchId)
               OR (gc.branch IS NULL AND gc.trainer.branch IS NOT NULL
                   AND gc.trainer.branch.id = :branchId)
           )
           """)
    Page<GymClass> findByTrainerAndBranch(@Param("trainerId") Integer trainerId,
            @Param("branchId") int branchId,
            Pageable pageable);

    List<GymClass> findByStatusTrue();

    Page<GymClass> findByStatusTrue(Pageable pageable);

    @Query("""
           SELECT gc FROM GymClass gc
           WHERE gc.status = true
           AND (
               (gc.branch IS NOT NULL AND gc.branch.id = :branchId)
               OR (gc.branch IS NULL AND gc.trainer IS NOT NULL
                   AND gc.trainer.branch IS NOT NULL
                   AND gc.trainer.branch.id = :branchId)
           )
           """)
    List<GymClass> findActiveByBranchOrTrainerBranch(@Param("branchId") int branchId);

    boolean existsByIdClassAndTrainer_UserId(int idClass, Integer trainerId);

    @Query("""
           SELECT CASE WHEN COUNT(gc) > 0 THEN true ELSE false END FROM GymClass gc
           WHERE gc.idClass = :classId
           AND gc.trainer IS NOT NULL
           AND gc.trainer.userId = :trainerId
           AND (
               (gc.branch IS NOT NULL AND gc.branch.id = :branchId)
               OR (gc.branch IS NULL AND gc.trainer.branch IS NOT NULL
                   AND gc.trainer.branch.id = :branchId)
           )
           """)
    boolean existsByIdClassAndTrainerAndBranch(@Param("classId") int classId,
            @Param("trainerId") Integer trainerId,
            @Param("branchId") int branchId);
}
