package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Attendance;
import com.una.ac.cr.gym.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    @Override
    @EntityGraph(attributePaths = {
        "client",
        "gymClass",
        "gymClass.trainer",
        "gymClass.branch"
    })
    List<Attendance> findAll();

    @Override
    @EntityGraph(attributePaths = {
        "client",
        "gymClass",
        "gymClass.trainer",
        "gymClass.branch"
    })
    Page<Attendance> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {
        "client",
        "gymClass",
        "gymClass.trainer",
        "gymClass.branch"
    })
    Optional<Attendance> findById(Integer id);

    @EntityGraph(attributePaths = {
        "client",
        "gymClass",
        "gymClass.trainer",
        "gymClass.branch"
    })
    Page<Attendance> findByClient_UserId(Integer userId, Pageable pageable);

    boolean existsByClient_UserIdAndGymClass_IdClass(Integer userId, int classId);

    @EntityGraph(attributePaths = {
        "client",
        "gymClass",
        "gymClass.trainer",
        "gymClass.branch"
    })
    List<Attendance> findByGymClass_Trainer_UserId(Integer trainerId);

    @EntityGraph(attributePaths = {
        "client",
        "gymClass",
        "gymClass.trainer",
        "gymClass.branch"
    })
    Page<Attendance> findByGymClass_Trainer_UserId(Integer trainerId, Pageable pageable);

    @EntityGraph(attributePaths = {
        "client",
        "gymClass",
        "gymClass.trainer",
        "gymClass.branch"
    })
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

    @EntityGraph(attributePaths = {
        "client",
        "gymClass",
        "gymClass.trainer",
        "gymClass.branch"
    })
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

    @Query("""
           SELECT DISTINCT a.client FROM Attendance a
           WHERE a.gymClass.trainer.userId = :trainerId
           AND a.client IS NOT NULL
           """)
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