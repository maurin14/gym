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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public List<Map<String, Object>> getAllAttendances(HttpSession session) {
        return getAttendancesForSession(session)
                .stream()
                .map(this::toAttendanceMap)
                .toList();
    }

    @ResponseBody
    @GetMapping("/attendances/page")
    public Map<String, Object> getAttendancesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpSession session) {

        int currentPage = Math.max(page, 0);

        Page<Attendance> attendancePage =
                getAttendancesPageForSession(session, currentPage, size);

        if (currentPage >= attendancePage.getTotalPages()
                && attendancePage.getTotalPages() > 0) {

            currentPage = attendancePage.getTotalPages() - 1;
            attendancePage =
                    getAttendancesPageForSession(session, currentPage, size);
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
    @GetMapping("/client/attendances/data")
    public List<Map<String, Object>> getClientAttendances(HttpSession session) {

        User userSession = (User) session.getAttribute("user");

        if (userSession == null) {
            return List.of();
        }

        return attendanceService
                .getClientAttendancesPage(userSession.getUserId(), 0, 100)
                .getContent()
                .stream()
                .map(this::toAttendanceMap)
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
            return Map.of(
                    "currentPage", 1,
                    "totalPages", 0,
                    "attendances", List.of()
            );
        }

        int currentPage = Math.max(page, 0);

        Page<Attendance> attendancePage =
                attendanceService.getClientAttendancesPage(
                        userSession.getUserId(),
                        currentPage,
                        size
                );

        if (currentPage >= attendancePage.getTotalPages()
                && attendancePage.getTotalPages() > 0) {

            currentPage = attendancePage.getTotalPages() - 1;

            attendancePage =
                    attendanceService.getClientAttendancesPage(
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
    @GetMapping("/attendances/edit/{idAttendance}")
    public Map<String, Object> getAttendanceForEdit(
            @PathVariable int idAttendance,
            HttpSession session) {

        Attendance attendance =
                attendanceService.getAttendanceById(idAttendance);

        if (attendance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!canAccessAttendance(session, attendance)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Map<String, Object> map = new HashMap<>();

        map.put("idAttendance", attendance.getIdAttendance());
        map.put("attendanceDate", attendance.getAttendanceDate());
        map.put("attendanceStatus", attendance.getAttendanceStatus());
        map.put("observation", attendance.getObservation());

        map.put("clientId",
                attendance.getClient() != null
                ? attendance.getClient().getUserId()
                : "");

        map.put("classId",
                attendance.getGymClass() != null
                ? attendance.getGymClass().getIdClass()
                : "");

        return map;
    }

    @ResponseBody
    @GetMapping("/attendances/{idAttendance}")
    public Attendance getAttendanceById(
            @PathVariable int idAttendance,
            HttpSession session) {

        Attendance attendance =
                attendanceService.getAttendanceById(idAttendance);

        if (attendance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!canAccessAttendance(session, attendance)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return attendance;
    }

    @ResponseBody
    @PostMapping("/attendances")
    public ResponseEntity<Attendance> saveAttendance(
            @RequestBody Attendance attendance,
            HttpSession session) {

        User userSession = (User) session.getAttribute("user");

        if (userSession != null && "client".equals(userSession.getRole())) {

            attendance.setClient(userSession);

            if (attendance.getGymClass() != null
                    && attendanceService.isClientEnrolled(
                            userSession.getUserId(),
                            attendance.getGymClass().getIdClass())) {

                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        if (!canSaveAttendance(session, attendance)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(attendanceService.saveAttendance(attendance));
    }

    @ResponseBody
    @PutMapping("/attendances/{idAttendance}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable int idAttendance,
            @RequestBody Attendance attendance,
            HttpSession session) {

        Attendance currentAttendance =
                attendanceService.getAttendanceById(idAttendance);

        if (currentAttendance == null) {
            return ResponseEntity.notFound().build();
        }

        if (!canAccessAttendance(session, currentAttendance)
                || !canSaveAttendance(session, attendance)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Attendance updatedAttendance =
                attendanceService.updateAttendance(idAttendance, attendance);

        if (updatedAttendance == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedAttendance);
    }

    @ResponseBody
    @DeleteMapping("/attendances/{idAttendance}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable int idAttendance,
            HttpSession session) {

        Attendance attendance =
                attendanceService.getAttendanceById(idAttendance);

        if (attendance == null) {
            return ResponseEntity.notFound().build();
        }

        if (!canAccessAttendance(session, attendance)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        attendanceService.deleteAttendance(idAttendance);

        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @GetMapping("/attendances/clients")
    public List<User> getClients(HttpSession session) {

        User user = currentUser(session);

        if ("trainer".equals(user.getRole())) {
            return attendanceService.getTrainerClients(
                    user.getUserId(),
                    getCurrentBranchId(user)
            );
        }

        if (!"administrator".equals(user.getRole())) {
            return List.of();
        }

        return userService.filterUsers(null, "client");
    }

    private List<Attendance> getAttendancesForSession(HttpSession session) {

        User user = currentUser(session);

        if ("trainer".equals(user.getRole())) {
            return attendanceService.getTrainerAttendances(
                    user.getUserId(),
                    getCurrentBranchId(user)
            );
        }

        if ("client".equals(user.getRole())) {
            return attendanceService
                    .getClientAttendancesPage(user.getUserId(), 0, 100)
                    .getContent();
        }

        return attendanceService.getAllAttendances();
    }

    private Page<Attendance> getAttendancesPageForSession(
            HttpSession session,
            int page,
            int size) {

        User user = currentUser(session);

        if ("trainer".equals(user.getRole())) {
            return attendanceService.getTrainerAttendancesPage(
                    user.getUserId(),
                    getCurrentBranchId(user),
                    page,
                    size
            );
        }

        if ("client".equals(user.getRole())) {
            return attendanceService.getClientAttendancesPage(
                    user.getUserId(),
                    page,
                    size
            );
        }

        return attendanceService.getAttendancesPage(page, size);
    }

    private boolean canAccessAttendance(
            HttpSession session,
            Attendance attendance) {

        User user = currentUser(session);

        if ("administrator".equals(user.getRole())) {
            return true;
        }

        if ("client".equals(user.getRole())) {
            return attendance.getClient() != null
                    && attendance.getClient().getUserId()
                    .equals(user.getUserId());
        }

        return "trainer".equals(user.getRole())
                && attendance.getGymClass() != null
                && attendanceService.classBelongsToTrainer(
                        attendance.getGymClass().getIdClass(),
                        user.getUserId(),
                        getCurrentBranchId(user)
                );
    }

    private boolean canSaveAttendance(
            HttpSession session,
            Attendance attendance) {

        User user = currentUser(session);

        if ("administrator".equals(user.getRole())
                || "client".equals(user.getRole())) {
            return true;
        }

        if (!"trainer".equals(user.getRole())
                || attendance.getGymClass() == null) {
            return false;
        }

        return attendanceService.classBelongsToTrainer(
                attendance.getGymClass().getIdClass(),
                user.getUserId(),
                getCurrentBranchId(user)
        );
    }

    private User currentUser(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return user;
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

        map.put("branchName", getAttendanceBranchName(attendance));

        return map;
    }

    private Integer getCurrentBranchId(User user) {
        return user != null && user.getBranch() != null
                ? user.getBranch().getId()
                : null;
    }

    private String getAttendanceBranchName(Attendance attendance) {

        if (attendance == null || attendance.getGymClass() == null) {
            return "Sin sucursal";
        }

        if (attendance.getGymClass().getBranch() != null) {
            return attendance.getGymClass().getBranch().getName();
        }

        if (attendance.getGymClass().getTrainer() != null
                && attendance.getGymClass().getTrainer().getBranch() != null) {

            return attendance.getGymClass()
                    .getTrainer()
                    .getBranch()
                    .getName();
        }

        return "Sin sucursal";
    }
}