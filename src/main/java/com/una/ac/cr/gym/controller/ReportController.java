package com.una.ac.cr.gym.controller;

import com.una.ac.cr.gym.domain.Report;
import com.una.ac.cr.gym.service.ReportService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService rService;
    
    private Report report;

    @GetMapping({"", "/"})
    public String index(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(required = false) String reportType,
                        @RequestParam(required = false) String reportStatus,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirect) {

        String access = rService.validateAdministratorAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }

        if((reportType != null && !reportType.trim().isEmpty())
                || (reportStatus != null && !reportStatus.trim().isEmpty())){

            model.addAttribute("reports", rService.filterReports(reportType, reportStatus));
            model.addAttribute("reportType", reportType);
            model.addAttribute("reportStatus", reportStatus);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);

            return "report/listReport";
        }

        int size = 5;
        Page<Report> reportPage = rService.getReportsByPage(page, size);

        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reportPage.getTotalPages());
        model.addAttribute("reportType", reportType);
        model.addAttribute("reportStatus", reportStatus);

        return "report/listReport";
    }

    @GetMapping("/add")
    public String add(Model model, HttpSession session, RedirectAttributes redirect) {
        String access = rService.validateAdministratorAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        Report r = rService.createNewReport(session);
        model.addAttribute("reportNew", r);
        return "report/formReport";
    }

    @PostMapping("/save")
    public String save(Report reportNew, Model model, RedirectAttributes redirect, HttpSession session) {
        String access = rService.validateAdministratorAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }

        Map<String, String> fieldErrors = rService.validateFields(reportNew);

        if (!fieldErrors.isEmpty()) {
            model.addAttribute("reportNew", reportNew);
            model.addAttribute("fieldErrors", fieldErrors);
            model.addAttribute("messageError", "No se pudo guardar. Revise los campos marcados.");
            return "report/formReport";
        }

        boolean isNew = reportNew.getReportId() == null;
        rService.save(reportNew);
        boolean saved = true;

        if (saved) {
            if (isNew) {
                redirect.addFlashAttribute("messageSuccess", "Reporte guardado correctamente");
            } else {
                redirect.addFlashAttribute("messageSuccess", "Reporte actualizado correctamente");
            }
        } else {
            redirect.addFlashAttribute("messageError", "No se pudo guardar el reporte");
        }

        return "redirect:/reports";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") Integer id, Model model, HttpSession session, RedirectAttributes redirect) {
        String access = rService.validateAdministratorAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        Report report = rService.getReportById(id);

        if (report == null) {
            redirect.addFlashAttribute("messageError", "Reporte no encontrado");
            return "redirect:/reports";
        }

        model.addAttribute("reportNew", report);
        return "report/formReport";
    }

    @GetMapping("/view")
    public String view(@RequestParam("id") Integer id, Model model, HttpSession session, RedirectAttributes redirect) {
        String access = rService.validateAdministratorAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        Report report = rService.getReportById(id);

        if (report == null) {
            redirect.addFlashAttribute("messageError", "Reporte no encontrado");
            return "redirect:/reports";
        }

        model.addAttribute("reportView", report);
        model.addAttribute("reportData", rService.getPreviewData(report.getReportType()));
        return "report/viewReport";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id, HttpSession session, RedirectAttributes redirect) {
        String access = rService.validateAdministratorAccess(session);

        if(access != null){
            redirect.addFlashAttribute("messageError", access);
            return "redirect:/";
        }
        
        rService.delete(id);
        boolean deleted = true;

        if (deleted) {
            redirect.addFlashAttribute("messageSuccess", "Reporte eliminado correctamente");
        } else {
            redirect.addFlashAttribute("messageError", "No se pudo eliminar el reporte");
        }

        return "redirect:/reports";
    }
    
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam("id") Integer id) {
        return rService.downloadReport(id);
    }
}
