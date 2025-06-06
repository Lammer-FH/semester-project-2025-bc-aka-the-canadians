package com.campuslostfound.service;

import com.campuslostfound.model.Report;
import com.campuslostfound.model.ReportStatus;
import com.campuslostfound.model.User;
import com.campuslostfound.repository.ReportRepository;
import com.campuslostfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    public List<Report> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    public List<Report> getReportsByUser(User user) {
        return reportRepository.findByUser(user);
    }

    public List<Report> getReportsByUserId(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return reportRepository.findByUser(user);
    }

    @Transactional
    public Report createReport(Report report) {
        validateReport(report);
        report.setStatus(ReportStatus.OPEN);
        return reportRepository.save(report);
    }

    @Transactional
    public Report updateReport(Report report) {
        validateReport(report);
        return reportRepository.save(report);
    }

    @Transactional
    public Report updateReportStatus(Long id, ReportStatus status) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + id));
        report.setStatus(status);
        return reportRepository.save(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new IllegalArgumentException("Report not found with id: " + id);
        }
        reportRepository.deleteById(id);
    }

    public List<Report> searchReports(String query) {
        return reportRepository.findByDescriptionContainingIgnoreCase(query);
    }

    private void validateReport(Report report) {
        if (report.getTitle() == null || report.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Report title is required");
        }
        if (report.getDescription() == null || report.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Report description is required");
        }
        if (report.getUser() == null) {
            throw new IllegalArgumentException("Report must be associated with a user");
        }
    }

    private void validateStatusTransition(ReportStatus currentStatus, ReportStatus newStatus) {
        // Erlaubte Status-Übergänge
        switch (currentStatus) {
            case OPEN:
                if (newStatus != ReportStatus.IN_PROGRESS && newStatus != ReportStatus.CLOSED) {
                    throw new IllegalArgumentException("Invalid status transition from OPEN");
                }
                break;
            case IN_PROGRESS:
                if (newStatus != ReportStatus.RESOLVED && newStatus != ReportStatus.CLOSED) {
                    throw new IllegalArgumentException("Invalid status transition from IN_PROGRESS");
                }
                break;
            case RESOLVED:
                if (newStatus != ReportStatus.CLOSED) {
                    throw new IllegalArgumentException("Invalid status transition from RESOLVED");
                }
                break;
            case CLOSED:
                throw new IllegalArgumentException("Cannot change status of a CLOSED report");
        }
    }
}
