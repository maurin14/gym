package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Attendance;
import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.repository.AttendanceRepository;
import com.una.ac.cr.gym.repository.GymClassRepository;
import com.una.ac.cr.gym.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final GymClassRepository gymClassRepository;
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             GymClassRepository gymClassRepository,
                             UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.gymClassRepository = gymClassRepository;
        this.userRepository = userRepository;
    }

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAllWithRelations(PageRequest.of(0, 100)).getContent();
    }

    public Page<Attendance> getAttendancesPage(int page, int size) {
        return attendanceRepository.findAllWithRelations(PageRequest.of(page, size));
    }

    public List<Attendance> getTrainerAttendances(Integer trainerId) {
        return attendanceRepository.findByGymClass_Trainer_UserId(trainerId, PageRequest.of(0, 100)).getContent();
    }

    public List<Attendance> getTrainerAttendances(Integer trainerId, Integer branchId) {
        if (branchId == null || branchId <= 0) {
            return List.of();
        }
        return attendanceRepository.findByTrainerAndBranch(trainerId, branchId, PageRequest.of(0, 100)).getContent();
    }

    public Page<Attendance> getTrainerAttendancesPage(Integer trainerId, int page, int size) {
        return attendanceRepository.findByGymClass_Trainer_UserId(trainerId, PageRequest.of(page, size));
    }

    public Page<Attendance> getTrainerAttendancesPage(Integer trainerId, Integer branchId, int page, int size) {
        if (branchId == null || branchId <= 0) {
            return Page.empty(PageRequest.of(page, size));
        }
        return attendanceRepository.findByTrainerAndBranch(trainerId, branchId, PageRequest.of(page, size));
    }

    public List<User> getTrainerClients(Integer trainerId) {
        return attendanceRepository.findDistinctClientsByTrainerId(trainerId);
    }

    public List<User> getTrainerClients(Integer trainerId, Integer branchId) {
        if (branchId == null || branchId <= 0) {
            return List.of();
        }
        return attendanceRepository.findDistinctClientsByTrainerAndBranch(trainerId, branchId);
    }

    public boolean classBelongsToTrainer(int classId, Integer trainerId) {
        return gymClassRepository.existsByIdClassAndTrainer_UserId(classId, trainerId);
    }

    public boolean classBelongsToTrainer(int classId, Integer trainerId, Integer branchId) {
        if (branchId == null || branchId <= 0) {
            return false;
        }
        return gymClassRepository.existsByIdClassAndTrainerAndBranch(classId, trainerId, branchId);
    }

    public Page<Attendance> getClientAttendancesPage(Integer userId, int page, int size) {
        return attendanceRepository.findByClient_UserId(userId, PageRequest.of(page, size));
    }

    public Attendance getAttendanceById(int idAttendance) {
        return attendanceRepository.findByIdWithRelations(idAttendance).orElse(null);
    }

    public boolean isClientEnrolled(Integer clientId, int classId) {
        return attendanceRepository.existsByClient_UserIdAndGymClass_IdClass(clientId, classId);
    }

    public String enrollClientInClass(User client, int classId) {
        if (client == null) {
            return "Debe iniciar sesion.";
        }

        GymClass gymClass = gymClassRepository.findByIdWithRelations(classId).orElse(null);

        if (gymClass == null) {
            return "Clase no encontrada.";
        }

        if (!gymClass.isStatus()) {
            return "La clase no esta disponible.";
        }

        if (isClientEnrolled(client.getUserId(), classId)) {
            return "Ya estas inscrito en esta clase.";
        }

        if (gymClass.getEnrolledCount() >= gymClass.getMaxCapacity()) {
            return "No hay cupos disponibles.";
        }

        Attendance attendance = new Attendance();
        attendance.setClient(client);
        attendance.setGymClass(gymClass);
        attendance.setAttendanceDate(gymClass.getClassDate());
        attendance.setAttendanceStatus("Inscrito");
        attendance.setObservation("Inscripcion desde sucursal");

        saveAttendance(attendance);

        return "";
    }

    public Attendance saveAttendance(Attendance attendance) {
        attendance.setRegisterDate(LocalDate.now());
        attendance.setStatus(true);

        loadClient(attendance);
        loadGymClass(attendance, true);

        return attendanceRepository.save(attendance);
    }

    public Attendance updateAttendance(int idAttendance, Attendance attendance) {
        Attendance currentAttendance = attendanceRepository.findByIdWithRelations(idAttendance).orElse(null);

        if (currentAttendance == null) {
            return null;
        }

        attendance.setIdAttendance(idAttendance);
        attendance.setRegisterDate(currentAttendance.getRegisterDate());
        attendance.setStatus(currentAttendance.isStatus());

        loadClient(attendance);

        GymClass currentClass = currentAttendance.getGymClass();
        GymClass newClass = null;

        if (attendance.getGymClass() != null) {
            newClass = gymClassRepository.findById(attendance.getGymClass().getIdClass()).orElse(null);
            attendance.setGymClass(newClass);
        }

        Integer currentClassId = currentClass != null ? currentClass.getIdClass() : null;
        Integer newClassId = newClass != null ? newClass.getIdClass() : null;

        if (currentClassId != null && !currentClassId.equals(newClassId) && currentClass.getEnrolledCount() > 0) {
            currentClass.setEnrolledCount(currentClass.getEnrolledCount() - 1);
            gymClassRepository.save(currentClass);
        }

        if (newClassId != null && !newClassId.equals(currentClassId)) {
            newClass.setEnrolledCount(newClass.getEnrolledCount() + 1);
            gymClassRepository.save(newClass);
        }

        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(int idAttendance) {
        Attendance attendance = attendanceRepository.findByIdWithRelations(idAttendance).orElse(null);

        if (attendance != null && attendance.getGymClass() != null) {
            GymClass gymClass = gymClassRepository.findById(attendance.getGymClass().getIdClass()).orElse(null);
            if (gymClass != null && gymClass.getEnrolledCount() > 0) {
                gymClass.setEnrolledCount(gymClass.getEnrolledCount() - 1);
                gymClassRepository.save(gymClass);
            }
        }

        attendanceRepository.deleteById(idAttendance);
    }

    private void loadClient(Attendance attendance) {
        if (attendance.getClient() == null || attendance.getClient().getUserId() == null) return;
        User client = userRepository.findById(attendance.getClient().getUserId()).orElse(null);
        attendance.setClient(client);
    }

    private void loadGymClass(Attendance attendance, boolean increaseEnrolledCount) {
        if (attendance.getGymClass() == null) return;
        GymClass gymClass = gymClassRepository.findById(attendance.getGymClass().getIdClass()).orElse(null);
        if (gymClass == null) {
            attendance.setGymClass(null);
            return;
        }

        if (increaseEnrolledCount) {
            gymClass.setEnrolledCount(gymClass.getEnrolledCount() + 1);
            gymClassRepository.save(gymClass);
        }

        attendance.setGymClass(gymClass);
    }
}