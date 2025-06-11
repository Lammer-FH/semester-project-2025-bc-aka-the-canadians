package com.campuslostfound.service;

import com.campuslostfound.model.*;
import com.campuslostfound.dto.ItemCreateRequest;
import com.campuslostfound.repository.ReportRepository;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final LocationService locationService;

    public List<Report> getAllReports() {
        return reportRepository.findAllWithItems();
    }

    public Optional<Report> getReportById(Long id) {
        return reportRepository.findByIdWithItems(id);
    }

    public List<Report> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    public List<Report> getReportsByUser(User user) {
        return reportRepository.findByUser(user);
    }

    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }    public Report createReportFromIds(Long userId, Long locationId, ReportType type) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("Location ID cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        User user = userService.getUserById(userId).orElseThrow(
            () -> new IllegalArgumentException("User not found with id: " + userId));

        Location location =
            locationService.getLocationById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        Report report = new Report();
        report.setUser(user);
        report.setLocation(location);
        report.setType(type);
        report.setStatus(ReportStatus.OPEN);

        return reportRepository.save(report);
    }

    @Transactional
    public Report createReportWithItems(Long userId, Long locationId, ReportType type, List<ItemCreateRequest> itemRequests) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("Location ID cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new IllegalArgumentException("At least one item must be provided");
        }

        User user = userService.getUserById(userId).orElseThrow(
            () -> new IllegalArgumentException("User not found with id: " + userId));

        Location location =
            locationService.getLocationById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        // Create the report
        Report report = new Report();
        report.setUser(user);
        report.setLocation(location);
        report.setType(type);
        report.setStatus(ReportStatus.OPEN);
        report.setItems(new ArrayList<>());

        // Save the report first to get the ID
        report = reportRepository.save(report);

        // Create all items
        for (ItemCreateRequest itemRequest : itemRequests) {
            Item item = new Item();
            item.setName(itemRequest.getName());
            item.setDescription(itemRequest.getDescription());
            item.setReport(report);
            item.setStatus(ItemStatus.UNCLAIMED);

            report.getItems().add(item);
        }

        // Save the report with items
        return reportRepository.save(report);
    }

    public Report updateReportById(Long reportId, Long locationId, ReportStatus status) {
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
    }    public Report resolveReportById(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(
            () -> new IllegalArgumentException("Report not found with id: " + reportId));

        report.setStatus(ReportStatus.RESOLVED);
        return reportRepository.save(report);
    }

    public void updateReportStatusBasedOnItems(Long reportId) {
        Report report = reportRepository.findByIdWithItems(reportId).orElseThrow(
            () -> new IllegalArgumentException("Report not found with id: " + reportId));

        // Check if all items are claimed
        boolean allItemsClaimed = report.getItems() != null && 
                                  !report.getItems().isEmpty() &&
                                  report.getItems().stream().allMatch(item -> item.getStatus() == ItemStatus.CLAIMED);

        if (allItemsClaimed && report.getStatus() != ReportStatus.RESOLVED) {
            report.setStatus(ReportStatus.RESOLVED);
            reportRepository.save(report);
        } else if (!allItemsClaimed && report.getStatus() == ReportStatus.RESOLVED) {
            report.setStatus(ReportStatus.OPEN);
            reportRepository.save(report);
        }
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}
