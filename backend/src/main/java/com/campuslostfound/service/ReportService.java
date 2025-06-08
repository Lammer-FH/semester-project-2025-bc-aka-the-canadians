package com.campuslostfound.service;

import com.campuslostfound.model.Location;
import com.campuslostfound.model.Report;
import com.campuslostfound.model.ReportStatus;
import com.campuslostfound.model.User;
import com.campuslostfound.repository.ReportRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final LocationService locationService;

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

    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    public Report createReportFromIds(Long userId, Long locationId, Boolean status) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("Location ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        User user = userService.getUserById(userId).orElseThrow(
            () -> new IllegalArgumentException("User not found with id: " + userId));

        Location location =
            locationService.getLocationById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        Report report = new Report();
        report.setUser(user);
        report.setLocation(location);
        report.setStatus(status);

        return reportRepository.save(report);
    }

    public Report updateReport(Long reportId, Long locationId, Boolean status) {
        Report existingReport = reportRepository.findById(reportId).orElseThrow(
            () -> new IllegalArgumentException("Report not found with id: " + reportId));

        if (locationId != null) {
            Location location =
                locationService.getLocationById(locationId)
                    .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));
            existingReport.setLocation(location);
        }

        if (status != null) {
            existingReport.setStatus(status);
        }

        return reportRepository.save(existingReport);
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}
