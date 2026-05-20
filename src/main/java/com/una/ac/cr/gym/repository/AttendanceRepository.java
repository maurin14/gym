/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.repository;

/**
 *
 * @author Amanda
 */


import com.una.ac.cr.gym.domain.Attendance;
import com.una.ac.cr.gym.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Page<Attendance> findByClient_UserId(Integer userId, Pageable pageable);

    boolean existsByClient_UserIdAndGymClass_IdClass(Integer userId, int classId);

    List<Attendance> findByGymClass_Trainer_UserId(Integer trainerId);

    Page<Attendance> findByGymClass_Trainer_UserId(Integer trainerId, Pageable pageable);

    @Query("""
           SELECT a FROM Attendance a
           WHERE a.gymClass IS NOT NULL
           AND a.gymClass.trainer IS NOT NULL
           AND a.gymClass.trainer.userId = :trainerId
           AND (
               (a.gymClass.branch IS NOT NULL AND a.gymClass.branch.id = :branchId)
               OR (a.gymClass.branch IS NULL
                   AND a.gymClass.trainer.branch IS NOT NULL
                   AND a.gymClass.trainer.branch.id = :branchId)
           )
           """)
    List<Attendance> findByTrainerAndBranch(@Param("trainerId") Integer trainerId,
            @Param("branchId") int branchId);

    @Query("""
           SELECT a FROM Attendance a
           WHERE a.gymClass IS NOT NULL
           AND a.gymClass.trainer IS NOT NULL
           AND a.gymClass.trainer.userId = :trainerId
           AND (
               (a.gymClass.branch IS NOT NULL AND a.gymClass.branch.id = :branchId)
               OR (a.gymClass.branch IS NULL
                   AND a.gymClass.trainer.branch IS NOT NULL
                   AND a.gymClass.trainer.branch.id = :branchId)
           )
           """)
    Page<Attendance> findByTrainerAndBranch(@Param("trainerId") Integer trainerId,
            @Param("branchId") int branchId,
            Pageable pageable);

    @Query("select distinct a.client from Attendance a "
            + "where a.gymClass.trainer.userId = :trainerId and a.client is not null")
    List<User> findDistinctClientsByTrainerId(@Param("trainerId") Integer trainerId);

    @Query("""
           SELECT DISTINCT a.client FROM Attendance a
           WHERE a.client IS NOT NULL
           AND a.gymClass IS NOT NULL
           AND a.gymClass.trainer IS NOT NULL
           AND a.gymClass.trainer.userId = :trainerId
           AND (
               (a.gymClass.branch IS NOT NULL AND a.gymClass.branch.id = :branchId)
               OR (a.gymClass.branch IS NULL
                   AND a.gymClass.trainer.branch IS NOT NULL
                   AND a.gymClass.trainer.branch.id = :branchId)
           )
           """)
    List<User> findDistinctClientsByTrainerAndBranch(@Param("trainerId") Integer trainerId,
            @Param("branchId") int branchId);
}
