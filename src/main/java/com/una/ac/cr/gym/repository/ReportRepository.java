package com.una.ac.cr.gym.repository;

import com.una.ac.cr.gym.domain.Report;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Integer>{
    public List<Report> findByReportType(String reportType);
    public List<Report> findByReportStatus(String reportStatus);
    public List<Report> findByReportTypeAndReportStatus(String reportType, String reportStatus);
    public Page<Report> findByReportType(String reportType, Pageable pageable);
    public Page<Report> findByReportStatus(String reportStatus, Pageable pageable);
    public Page<Report> findByReportTypeAndReportStatus(String reportType, String reportStatus, Pageable pageable);
}
