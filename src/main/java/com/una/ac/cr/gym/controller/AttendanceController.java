package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Attendance;
import com.una.ac.cr.gym.domain.User;
import com.una.ac.cr.gym.service.AttendanceService;
import com.una.ac.cr.gym.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String trainerAttendancesList(Model model) {
        model.addAttribute("attendanceBasePath", "/trainer/attendances");
        return "trainer/attendances/list";
    }

    @GetMapping("/trainer/attendances/form")
    public String trainerAttendancesForm(Model model) {
        model.addAttribute("attendanceBasePath", "/trainer/attendances");
        return "trainer/attendances/form";
    }

    @GetMapping("/trainer/attendances/form/{idAttendance}")
    public String trainerAttendancesEdit(@PathVariable int idAttendance, Model model) {
        model.addAttribute("attendanceBasePath", "/trainer/attendances");
        return "trainer/attendances/form";
    }

    @GetMapping("/admin/attendance")
    public String adminAttendanceList(HttpSession session, Model model) {
        User userSession = (User) session.getAttribute("user");

        if (userSession == null) {
            return "redirect:/login";
        }

        if (!"administrator".equals(userSession.getRole())) {
            return "redirect:/client/home";
        }

        model.addAttribute("attendanceBasePath", "/admin/attendance");
        return "admin/attendance";
    }

    @GetMapping("/admin/attendance/form")
    public String adminAttendanceForm(HttpSession session) {
        User userSession = (User) session.getAttribute("user");

        if (userSession == null) {
            return "redirect:/login";
        }

        if (!"administrator".equals(userSession.getRole())) {
            return "redirect:/client/home";
        }

        return "admin/attendance_form";
    }

    @GetMapping("/admin/attendance/form/{idAttendance}")
    public String adminAttendanceEdit(@PathVariable int idAttendance,
            HttpSession session) {
        User userSession = (User) session.getAttribute("user");

        if (userSession == null) {
            return "redirect:/login";
        }

        if (!"administrator".equals(userSession.getRole())) {
            return "redirect:/client/home";
        }

        return "admin/attendance_form";
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
    @GetMapping("/attendances")
    public List<Map<String, Object>> getAllAttendances() {

        return attendanceService.getAllAttendances()
                .stream()
                .map(attendance -> {

                    Map<String, Object> map = new HashMap<>();

                    map.put("idAttendance", attendance.getIdAttendance());
                    map.put("attendanceDate", attendance.getAttendanceDate());
                    map.put("attendanceStatus", attendance.getAttendanceStatus());
                    map.put("observation", attendance.getObservation());
                    map.put("registerDate", attendance.getRegisterDate());

                    map.put("clientName",
                            attendance.getClient() != null
                            ? attendance.getClient().getFullName()
                            : "Sin cliente");

                    map.put("classType",
                            attendance.getGymClass() != null
                            ? attendance.getGymClass().getClassType()
                            : "Sin clase");

                    return map;
                })
                .toList();
    }

    @ResponseBody
    @GetMapping("/attendances/page")
    public Map<String, Object> getAttendancesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        int currentPage = Math.max(page, 0);
        Page<Attendance> attendancePage = attendanceService.getAttendancesPage(currentPage, size);

        if (currentPage >= attendancePage.getTotalPages() && attendancePage.getTotalPages() > 0) {
            currentPage = attendancePage.getTotalPages() - 1;
            attendancePage = attendanceService.getAttendancesPage(currentPage, size);
        }

        List<Map<String, Object>> pageContent = attendancePage.getContent()
                .stream()
                .map(attendance -> {

                    Map<String, Object> map = new HashMap<>();

                    map.put("idAttendance", attendance.getIdAttendance());
                    map.put("attendanceDate", attendance.getAttendanceDate());
                    map.put("attendanceStatus", attendance.getAttendanceStatus());
                    map.put("observation", attendance.getObservation());
                    map.put("registerDate", attendance.getRegisterDate());

                    map.put("clientName",
                            attendance.getClient() != null
                            ? attendance.getClient().getFullName()
                            : "Sin cliente");

                    map.put("classType",
                            attendance.getGymClass() != null
                            ? attendance.getGymClass().getClassType()
                            : "Sin clase");

                    return map;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();

        response.put("currentPage", currentPage + 1);
        response.put("totalPages", attendancePage.getTotalPages());
        response.put("attendances", pageContent);

        return response;
    }

    @ResponseBody
    @GetMapping("/client/attendances/data")
    public List<Map<String, Object>> getClientAttendances(
            HttpSession session) {

        User userSession =
                (User) session.getAttribute("user");

        if (userSession == null) {
            return List.of();
        }

        return attendanceService.getAllAttendances()
                .stream()
                .filter(attendance ->
                        attendance.getClient() != null
                        && attendance.getClient().getUserId()
                        == userSession.getUserId()
                )
                .map(attendance -> {

                    Map<String, Object> map = new HashMap<>();

                    map.put("idAttendance", attendance.getIdAttendance());
                    map.put("attendanceDate", attendance.getAttendanceDate());
                    map.put("attendanceStatus", attendance.getAttendanceStatus());
                    map.put("observation", attendance.getObservation());
                    map.put("registerDate", attendance.getRegisterDate());

                    map.put("classType",
                            attendance.getGymClass() != null
                            ? attendance.getGymClass().getClassType()
                            : "Sin clase");

                    return map;
                })
                .toList();
    }

    @ResponseBody
    @GetMapping("/client/attendances/page")
    public Map<String, Object> getClientAttendancesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpSession session) {

        User userSession = (User) session.getAttribute("user");

        if (userSession == null) {
            return Map.of("currentPage", 1, "totalPages", 0, "attendances", List.of());
        }

        int currentPage = Math.max(page, 0);
        Page<Attendance> attendancePage = attendanceService.getClientAttendancesPage(
                userSession.getUserId(),
                currentPage,
                size
        );

        if (currentPage >= attendancePage.getTotalPages() && attendancePage.getTotalPages() > 0) {
            currentPage = attendancePage.getTotalPages() - 1;
            attendancePage = attendanceService.getClientAttendancesPage(
                    userSession.getUserId(),
                    currentPage,
                    size
            );
        }

        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", currentPage + 1);
        response.put("totalPages", attendancePage.getTotalPages());
        response.put("attendances", attendancePage.getContent()
                .stream()
                .map(this::toAttendanceMap)
                .toList());

        return response;
    }

    @ResponseBody
    @GetMapping("/attendances/{idAttendance}")
    public Attendance getAttendanceById(
            @PathVariable int idAttendance) {

        return attendanceService.getAttendanceById(idAttendance);
    }

    @ResponseBody
    @PostMapping("/attendances")
    public Attendance saveAttendance(
            @RequestBody Attendance attendance,
            HttpSession session) {

        User userSession =
                (User) session.getAttribute("user");

        if (userSession != null
                && "client".equals(userSession.getRole())) {

            attendance.setClient(userSession);
        }

        return attendanceService.saveAttendance(attendance);
    }

    @ResponseBody
    @PutMapping("/attendances/{idAttendance}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable int idAttendance,
            @RequestBody Attendance attendance) {

        Attendance updatedAttendance = attendanceService.updateAttendance(
                idAttendance,
                attendance
        );

        if (updatedAttendance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedAttendance);
    }

    @ResponseBody
    @DeleteMapping("/attendances/{idAttendance}")
    public void deleteAttendance(
            @PathVariable int idAttendance) {

        attendanceService.deleteAttendance(idAttendance);
    }

    @ResponseBody
    @GetMapping("/attendances/clients")
    public List<User> getClients() {
        return userService.filterUsers(null, "client");
    }

    private Map<String, Object> toAttendanceMap(Attendance attendance) {
        Map<String, Object> map = new HashMap<>();

        map.put("idAttendance", attendance.getIdAttendance());
        map.put("attendanceDate", attendance.getAttendanceDate());
        map.put("attendanceStatus", attendance.getAttendanceStatus());
        map.put("observation", attendance.getObservation());
        map.put("registerDate", attendance.getRegisterDate());

        map.put("clientName",
                attendance.getClient() != null
                ? attendance.getClient().getFullName()
                : "Sin cliente");

        map.put("classType",
                attendance.getGymClass() != null
                ? attendance.getGymClass().getClassType()
                : "Sin clase");

        return map;
    }
}
