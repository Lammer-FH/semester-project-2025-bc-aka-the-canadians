package com.campuslostfound.controller;

import com.campuslostfound.dto.ReportDTO;
import com.campuslostfound.mapper.ReportMapper;
import com.campuslostfound.model.Report;
import com.campuslostfound.service.ReportService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportMapper reportMapper;

    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        List<ReportDTO> reportDTOs = reportMapper.toDTOList(reports);

        return ResponseEntity.ok(reportDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long id) {
        Optional<Report> report = reportService.getReportById(id);

        return report.map(r -> ResponseEntity.ok(reportMapper.toDTO(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportDTO) {
        Report report = reportMapper.toEntity(reportDTO);
        Report savedReport = reportService.saveReport(report);

        return ResponseEntity.status(HttpStatus.CREATED).body(reportMapper.toDTO(savedReport));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> updateReport(
            @PathVariable Long id, @RequestBody ReportDTO reportDTO) {
        if (reportService.getReportById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reportDTO.setId(id);
        Report updatedReport = reportService.saveReport(reportMapper.toEntity(reportDTO));

        return ResponseEntity.ok(reportMapper.toDTO(updatedReport));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        if (reportService.getReportById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reportService.deleteReport(id);

        return ResponseEntity.noContent().build();
    }
}
