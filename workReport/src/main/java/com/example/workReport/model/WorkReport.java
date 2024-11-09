package com.example.workReport.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "work_reports")
public class WorkReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_date", unique = true)
    private LocalDate reportDate;
    
    @Column(name = "start_time")
    private String startTime;
    
    @Column(name = "end_time")
    private String endTime;
    
    @Column(name = "break_time")
    private String breakTime;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "status")
    private String workStatus;

    // Constructor with defaults
    public WorkReport() {
        this.reportDate = LocalDate.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getStartTime() {
        return formatTime(startTime);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return formatTime(endTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBreakTime() {
        return formatTime(breakTime);
    }

    public void setBreakTime(String breakTime) {
        this.breakTime = breakTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return workStatus;
    }

    public void setStatus(String status) {
        this.workStatus = status;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    private String formatTime(String time) {
        if (time == null || time.trim().isEmpty() || time.equals("-")) {
            return "-";
        }
        try {
            // Handle both HH:mm:ss and HH:mm formats
            LocalTime localTime = time.length() > 5 
                ? LocalTime.parse(time)
                : LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return time;
        }
    }
} 