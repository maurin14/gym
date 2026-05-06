/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.una.ac.cr.gym.service;

/**
 *
 * @author Amanda
 */

import com.una.ac.cr.gym.domain.Attendance;
import com.una.ac.cr.gym.repository.AttendanceRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Attendance getAttendanceById(int idAttendance) {
        return attendanceRepository.findById(idAttendance).orElse(null);
    }

    public Attendance saveAttendance(Attendance attendance) {
        attendance.setRegisterDate(LocalDate.now());
        attendance.setStatus(true);
        return attendanceRepository.save(attendance);
    }

    public Attendance updateAttendance(int idAttendance, Attendance attendance) {
        attendance.setIdAttendance(idAttendance);
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(int idAttendance) {
        attendanceRepository.deleteById(idAttendance);
    }
}
