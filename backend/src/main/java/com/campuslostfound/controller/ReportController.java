package com.campuslostfound.controller;

import com.campuslostfound.model.Report;
import com.campuslostfound.model.ReportStatus;
import com.campuslostfound.model.ReportType;
import com.campuslostfound.model.User;
import com.campuslostfound.service.ReportService;
import com.campuslostfound.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportType type) {
        
        if (status != null) {
            return ResponseEntity.ok(reportService.getReportsByStatus(status));
        } else if (type != null) {
            return ResponseEntity.ok(reportService.getReportsByType(type));
        } else {
            return ResponseEntity.ok(reportService.getAllReports());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Optional<Report> report = reportService.getReportById(id);
        return report.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        // Setze Erstellungsdatum
        report.setDateCreated(LocalDateTime.now());
        
        Report savedReport = reportService.saveReport(report);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReport);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable Long id, @RequestBody Report report) {
        if (!reportService.getReportById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        report.setId(id);
        return ResponseEntity.ok(reportService.saveReport(report));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        if (!reportService.getReportById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
} 