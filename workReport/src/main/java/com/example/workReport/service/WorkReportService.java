package com.example.workReport.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.workReport.model.WorkReport;
import com.example.workReport.repository.WorkReportRepository;

@Service
@Transactional
public class WorkReportService {
    
    @Autowired
    private WorkReportRepository workReportRepository;

    private static final Logger logger = LoggerFactory.getLogger(WorkReportService.class);

    public List<WorkReport> getMonthlyReports(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // Get existing reports for the month
        List<WorkReport> existingReports = workReportRepository
            .findByReportDateBetweenOrderByReportDateAsc(startDate, endDate);
        
        // Create a map of existing reports with date as key
        Map<LocalDate, WorkReport> reportMap = new HashMap<>();
        for (WorkReport report : existingReports) {
            reportMap.put(report.getReportDate(), report);
        }
        
        // Create list for all days in the month
        List<WorkReport> allReports = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        // Fill in all dates of the month
        while (!currentDate.isAfter(endDate)) {
            WorkReport report = reportMap.getOrDefault(currentDate, 
                createEmptyReport(currentDate));
            allReports.add(report);
            currentDate = currentDate.plusDays(1);
        }
        
        return allReports;
    }

    private WorkReport createEmptyReport(LocalDate date) {
        WorkReport report = new WorkReport();
        report.setReportDate(date);
        report.setWorkStatus("欠勤"); // Default status for new reports
        return report;
    }

    @Transactional
    public WorkReport saveWorkReport(WorkReport workReport) {
        try {
            Optional<WorkReport> existingReport = workReportRepository
                .findByReportDate(workReport.getReportDate());
            
            if (existingReport.isPresent()) {
                WorkReport report = existingReport.get();
                report.setStartTime(workReport.getStartTime());
                report.setEndTime(workReport.getEndTime());
                report.setBreakTime(workReport.getBreakTime());
                report.setDescription(workReport.getDescription());
                report.setWorkStatus(workReport.getWorkStatus());
                return workReportRepository.save(report);
            }
            
            // If no existing report, save the new one
            if (workReport.getWorkStatus() == null) {
                workReport.setWorkStatus("欠勤");
            }
            return workReportRepository.save(workReport);
        } catch (Exception e) {
            logger.error("Failed to save work report", e);
            throw new RuntimeException("作業報告の保存に失敗しました: " + e.getMessage());
        }
    }

    // Utility methods that might be needed later
    public Optional<WorkReport> getWorkReportById(Long id) {
        return workReportRepository.findById(id);
    }

    public void deleteWorkReport(Long id) {
        workReportRepository.deleteById(id);
    }

    public double calculateTotalHours(List<WorkReport> reports) {
        double totalHours = 0;
        
        for (WorkReport report : reports) {
            if (report.getStartTime() != null && !report.getStartTime().equals("-") &&
                report.getEndTime() != null && !report.getEndTime().equals("-") &&
                report.getBreakTime() != null && !report.getBreakTime().equals("-")) {
                try {
                    LocalTime startTime = LocalTime.parse(report.getStartTime());
                    LocalTime endTime = LocalTime.parse(report.getEndTime());
                    LocalTime breakTime = LocalTime.parse(report.getBreakTime());
                    
                    double workHours = Duration.between(startTime, endTime).toHours();
                    double breakHours = Duration.between(LocalTime.MIN, breakTime).toHours();
                    
                    totalHours += (workHours - breakHours);
                } catch (Exception e) {
                    logger.error("Error calculating hours for date: " + report.getReportDate(), e);
                }
            }
        }
        
        return totalHours;
    }
} 