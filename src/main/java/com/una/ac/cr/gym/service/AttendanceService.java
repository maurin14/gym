package com.una.ac.cr.gym.service;

import com.una.ac.cr.gym.domain.Attendance;
import com.una.ac.cr.gym.domain.GymClass;
import com.una.ac.cr.gym.repository.AttendanceRepository;
import com.una.ac.cr.gym.repository.GymClassRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final GymClassRepository gymClassRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
            GymClassRepository gymClassRepository) {

        this.attendanceRepository = attendanceRepository;
        this.gymClassRepository = gymClassRepository;
    }

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Page<Attendance> getAttendancesPage(int page, int size) {
        return attendanceRepository.findAll(PageRequest.of(page, size));
    }

    public Attendance getAttendanceById(int idAttendance) {
        return attendanceRepository.findById(idAttendance).orElse(null);
    }

    public Attendance saveAttendance(Attendance attendance) {

        attendance.setRegisterDate(LocalDate.now());
        attendance.setStatus(true);

        if (attendance.getGymClass() != null) {

            GymClass gymClass = gymClassRepository
                    .findById(attendance.getGymClass().getIdClass())
                    .orElse(null);

            if (gymClass != null) {

                gymClass.setEnrolledCount(gymClass.getEnrolledCount() + 1);

                gymClassRepository.save(gymClass);

                attendance.setGymClass(gymClass);
            }
        }

        return attendanceRepository.save(attendance);
    }

    public Attendance updateAttendance(int idAttendance, Attendance attendance) {
        Attendance currentAttendance = attendanceRepository.findById(idAttendance).orElse(null);

        if (currentAttendance == null) {
            return null;
        }

        attendance.setIdAttendance(idAttendance);
        attendance.setRegisterDate(currentAttendance.getRegisterDate());
        attendance.setStatus(currentAttendance.isStatus());

        GymClass currentClass = currentAttendance.getGymClass();
        GymClass newClass = null;

        if (attendance.getGymClass() != null) {
            newClass = gymClassRepository
                    .findById(attendance.getGymClass().getIdClass())
                    .orElse(null);
            attendance.setGymClass(newClass);
        }

        Integer currentClassId = currentClass != null ? currentClass.getIdClass() : null;
        Integer newClassId = newClass != null ? newClass.getIdClass() : null;

        if (currentClassId != null && !currentClassId.equals(newClassId)
                && currentClass.getEnrolledCount() > 0) {
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

        Attendance attendance = attendanceRepository.findById(idAttendance).orElse(null);

        if (attendance != null && attendance.getGymClass() != null) {

            GymClass gymClass = gymClassRepository
                    .findById(attendance.getGymClass().getIdClass())
                    .orElse(null);

            if (gymClass != null && gymClass.getEnrolledCount() > 0) {
                gymClass.setEnrolledCount(gymClass.getEnrolledCount() - 1);
                gymClassRepository.save(gymClass);
            }
        }

        attendanceRepository.deleteById(idAttendance);
    }
}
