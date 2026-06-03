package com.una.ac.cr.gym.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "tb_reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reportId")
    private Integer reportId;

    @Column(name = "reportType")
    private String reportType;

    @Column(name = "generationDate")
    private String generationDate;

    @Column(name = "generatedBy")
    private Integer generatedBy;

    @Column(name = "description")
    private String description;

    @Column(name = "startDate")
    private String startDate;

    @Column(name = "endDate")
    private String endDate;

    @Column(name = "format")
    private String format;

    @Column(name = "reportStatus")
    private String reportStatus;

    @Column(name = "filePath")
    private String filePath;

    @Transient //No esta en la bd, la uso solo para mostrar el nombre del admin
    private String userName;

    public Report() {
    }

    public Report(String reportType, String generationDate, Integer generatedBy, String description, String startDate, String endDate, String format, String reportStatus, String filePath, String userName) {
        this.reportType = reportType;
        this.generationDate = generationDate;
        this.generatedBy = generatedBy;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.format = format;
        this.reportStatus = reportStatus;
        this.filePath = filePath;
        this.userName = userName;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getGenerationDate() {
        return generationDate;
    }
    
    public String getFormattedGenerationDate() {
        try {
            DateTimeFormatter output = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            if(this.generationDate.contains("T")){
                DateTimeFormatter input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                return LocalDateTime.parse(this.generationDate, input).format(output);
            }

            DateTimeFormatter input = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(this.generationDate, input).format(output);

        } catch (Exception e) {
            return this.generationDate;
        }
    }

    public String getFormattedStartDate() {
        try {
            DateTimeFormatter output = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(this.startDate).format(output);
        } catch (Exception e) {
            return this.startDate;
        }
    }

    public String getFormattedEndDate() {
        try {
            DateTimeFormatter output = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(this.endDate).format(output);
        } catch (Exception e) {
            return this.endDate;
        }
    }

    public void setGenerationDate(String generationDate) {
        this.generationDate = generationDate;
    }

    public Integer getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(Integer generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}