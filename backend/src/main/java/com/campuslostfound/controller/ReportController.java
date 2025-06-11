package com.campuslostfound.controller;

import com.campuslostfound.dto.ReportDTO;
import com.campuslostfound.dto.ReportCreateRequest;
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
    public ResponseEntity<List<ReportDTO>> getReports() {
        List<Report> reports = reportService.getAllReports();
        List<ReportDTO> reportDTOs = reportMapper.toDTOList(reports);

        return ResponseEntity.ok(reportDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable Long id) {
        Optional<Report> report = reportService.getReportById(id);

        return report.map(r -> ResponseEntity.ok(reportMapper.toDTO(r)))
            .orElse(ResponseEntity.notFound().build());
    }    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportCreateRequest request) {
        try {
            Report savedReport;
            
            // If items are provided, use the new method; otherwise use the old method for backward compatibility
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                savedReport = reportService.createReportWithItems(
                    request.getUserId(),
                    request.getLocationId(),
                    request.getType(),
                    request.getItems()
                );
            } else {
                savedReport = reportService.createReportFromIds(
                    request.getUserId(),
                    request.getLocationId(),
                    request.getType()
                );
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(reportMapper.toDTO(savedReport));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> updateReport(
        @PathVariable Long id, @RequestBody ReportDTO reportDTO) {
        try {
            Report updatedReport = reportService.updateReportById(
                id,
                reportDTO.getLocationId(),
                reportDTO.getStatus()
            );

            return ResponseEntity.ok(reportMapper.toDTO(updatedReport));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReportDTO> resolveReport(@PathVariable Long id) {
        try {
            Report resolvedReport = reportService.resolveReportById(id);
            return ResponseEntity.ok(reportMapper.toDTO(resolvedReport));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
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
