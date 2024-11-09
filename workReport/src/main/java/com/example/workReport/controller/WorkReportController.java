package com.example.workReport.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.workReport.model.WorkReport;
import com.example.workReport.service.WorkReportService;

@Controller
public class WorkReportController {

    @Autowired
    private WorkReportService workReportService;

    @GetMapping("/")
    public String showReportList(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        
        LocalDate now = LocalDate.now();
        year = year != null ? year : now.getYear();
        month = month != null ? month : now.getMonthValue();

        YearMonth currentMonth = YearMonth.of(year, month);
        YearMonth prevMonth = currentMonth.minusMonths(1);
        YearMonth nextMonth = currentMonth.plusMonths(1);

        List<WorkReport> reports = workReportService.getMonthlyReports(year, month);
        double totalHours = workReportService.calculateTotalHours(reports);

        model.addAttribute("reports", reports);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("prevMonth", prevMonth);
        model.addAttribute("nextMonth", nextMonth);
        model.addAttribute("totalHours", totalHours);

        return "index";
    }

    @PostMapping("/api/reports")
    @ResponseBody
    public ResponseEntity<?> saveReport(@RequestBody Map<String, String> payload) {
        try {
            WorkReport workReport = new WorkReport();
            
            // Parse and set the report date
            String reportDate = payload.get("reportDate");
            if (reportDate != null && !reportDate.isEmpty()) {
                workReport.setReportDate(LocalDate.parse(reportDate));
            }

            // Set work status
            workReport.setWorkStatus(payload.getOrDefault("workStatus", "欠勤"));
            
            // Handle time fields properly
            String startTime = payload.get("startTime");
            if (startTime != null && !startTime.isEmpty()) {
                workReport.setStartTime(startTime);
            }
            
            String endTime = payload.get("endTime");
            if (endTime != null && !endTime.isEmpty()) {
                workReport.setEndTime(endTime);
            }
            
            String breakTime = payload.get("breakTime");
            if (breakTime != null && !breakTime.isEmpty()) {
                workReport.setBreakTime(breakTime);
            }
            
            workReport.setDescription(payload.get("description"));

            WorkReport savedReport = workReportService.saveWorkReport(workReport);
            return ResponseEntity.ok(savedReport);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("保存に失敗しました: " + e.getMessage());
        }
    }

    // Error handler for invalid requests
    @ResponseBody
    @GetMapping("/error")
    public ResponseEntity<String> handleError() {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("エラーが発生しました。");
    }
} 