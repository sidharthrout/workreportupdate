package com.example.workReport.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.workReport.model.WorkReport;

public interface WorkReportRepository extends JpaRepository<WorkReport, Long> {
    List<WorkReport> findByReportDateBetweenOrderByReportDateAsc(LocalDate startDate, LocalDate endDate);
    Optional<WorkReport> findByReportDate(LocalDate reportDate);
} 