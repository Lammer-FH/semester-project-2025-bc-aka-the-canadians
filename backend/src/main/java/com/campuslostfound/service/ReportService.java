package com.campuslostfound.service;

import com.campuslostfound.model.Report;
import com.campuslostfound.model.ReportStatus;
import com.campuslostfound.model.ReportType;
import com.campuslostfound.model.User;
import com.campuslostfound.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    public List<Report> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    public List<Report> getReportsByType(ReportType type) {
        return reportRepository.findByType(type);
    }

    public List<Report> getReportsByUser(User user) {
        return reportRepository.findByReportedBy(user);
    }

    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
} 