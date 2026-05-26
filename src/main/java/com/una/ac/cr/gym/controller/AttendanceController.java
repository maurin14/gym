package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Attendance;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.AttendanceService;
import com.una.ac.cr.gym.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;

    public AttendanceController(AttendanceService attendanceService,
            UserService userService) {
        this.attendanceService = attendanceService;
        this.userService = userService;
    }

    @GetMapping("/trainer/attendances")
    public String trainerAttendancesList() {
        return "redirect:/admin/attendances";
    }

    @GetMapping("/admin/attendances")
    public String adminAttendancesList() {
        return "trainer/attendances/list";
    }

    @GetMapping("/trainer/attendances/form")
    public String trainerAttendancesForm() {
        return "redirect:/admin/attendances/form";
    }

    @GetMapping("/admin/attendances/form")
    public String adminAttendancesForm() {
        return "trainer/attendances/form";
    }

    @GetMapping("/trainer/attendances/form/{idAttendance}")
    public String trainerAttendancesEdit(@PathVariable int idAttendance) {
        return "redirect:/admin/attendances/form/" + idAttendance;
    }

    @GetMapping("/admin/attendances/form/{idAttendance}")
    public String adminAttendancesEdit(@PathVariable int idAttendance) {
        return "trainer/attendances/form";
    }

    @GetMapping("/client/attendances")
    public String clientAttendancesList() {
        return "client/attendances/list";
    }

    @GetMapping("/client/attendances/form")
    public String clientAttendancesForm() {
        return "client/attendances/form";
    }

    @ResponseBody
    @GetMapping("/attendances/page")
    public Map<String, Object> getAttendancesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        List<Map<String, Object>> attendances = attendanceService.getAllAttendances()
                .stream()
                .map(this::convertAttendanceToMap)
                .toList();

        int start = page * size;
        int end = Math.min(start + size, attendances.size());

        List<Map<String, Object>> pageContent =
                start < attendances.size() ? attendances.subList(start, end) : List.of();

        int totalPages = (int) Math.ceil((double) attendances.size() / size);

        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", page + 1);
        response.put("totalPages", totalPages);
        response.put("attendances", pageContent);

        return response;
    }

    @ResponseBody
    @GetMapping("/attendances")
    public List<Map<String, Object>> getAllAttendances() {
        return attendanceService.getAllAttendances()
                .stream()
                .map(this::convertAttendanceToMap)
                .toList();
    }

    @ResponseBody
    @GetMapping("/client/attendances/data")
    public List<Map<String, Object>> getClientAttendances(HttpSession session) {
        User userSession = (User) session.getAttribute("user");

        if (userSession == null) {
            return List.of();
        }

        return attendanceService.getAllAttendances()
                .stream()
                .filter(attendance ->
                        attendance.getClient() != null
                        && attendance.getClient().getUserId() == userSession.getUserId()
                )
                .map(this::convertClientAttendanceToMap)
                .toList();
    }

    @ResponseBody
    @GetMapping("/attendances/edit/{idAttendance}")
    public Map<String, Object> getAttendanceForEdit(@PathVariable int idAttendance) {
        Attendance attendance = attendanceService.getAttendanceById(idAttendance);

        Map<String, Object> map = new HashMap<>();

        map.put("idAttendance", attendance.getIdAttendance());
        map.put("attendanceDate", attendance.getAttendanceDate());
        map.put("attendanceStatus", attendance.getAttendanceStatus());
        map.put("observation", attendance.getObservation());

        map.put("clientId", attendance.getClient() != null
                ? attendance.getClient().getUserId()
                : "");

        map.put("classId", attendance.getGymClass() != null
                ? attendance.getGymClass().getIdClass()
                : "");

        return map;
    }

    @ResponseBody
    @GetMapping("/attendances/{idAttendance}")
    public Attendance getAttendanceById(@PathVariable int idAttendance) {
        return attendanceService.getAttendanceById(idAttendance);
    }

    @ResponseBody
    @PostMapping("/attendances")
    public Attendance saveAttendance(@RequestBody Attendance attendance,
            HttpSession session) {

        User userSession = (User) session.getAttribute("user");

        if (userSession != null && "client".equals(userSession.getRole())) {
            attendance.setClient(userSession);
        }

        return attendanceService.saveAttendance(attendance);
    }

    @ResponseBody
    @PutMapping("/attendances/{idAttendance}")
    public Attendance updateAttendance(@PathVariable int idAttendance,
            @RequestBody Attendance attendance) {
        return attendanceService.updateAttendance(idAttendance, attendance);
    }

    @ResponseBody
    @DeleteMapping("/attendances/{idAttendance}")
    public void deleteAttendance(@PathVariable int idAttendance) {
        attendanceService.deleteAttendance(idAttendance);
    }

    @ResponseBody
    @GetMapping("/attendances/clients")
    public List<User> getClients() {
        return userService.filterUsers(null, "client");
    }

    private Map<String, Object> convertAttendanceToMap(Attendance attendance) {
        Map<String, Object> map = new HashMap<>();

        map.put("idAttendance", attendance.getIdAttendance());
        map.put("attendanceDate", attendance.getAttendanceDate());
        map.put("attendanceStatus", attendance.getAttendanceStatus());
        map.put("observation", attendance.getObservation());
        map.put("registerDate", attendance.getRegisterDate());

        map.put("clientName", attendance.getClient() != null
                ? attendance.getClient().getFullName()
                : "Sin cliente");

        map.put("classType", attendance.getGymClass() != null
                ? attendance.getGymClass().getClassType()
                : "Sin clase");

        return map;
    }

    private Map<String, Object> convertClientAttendanceToMap(Attendance attendance) {
        Map<String, Object> map = new HashMap<>();

        map.put("idAttendance", attendance.getIdAttendance());
        map.put("attendanceDate", attendance.getAttendanceDate());
        map.put("attendanceStatus", attendance.getAttendanceStatus());
        map.put("observation", attendance.getObservation());
        map.put("registerDate", attendance.getRegisterDate());

        map.put("classType", attendance.getGymClass() != null
                ? attendance.getGymClass().getClassType()
                : "Sin clase");

        return map;
    }
}