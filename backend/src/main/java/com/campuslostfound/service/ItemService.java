package com.campuslostfound.service;

import com.campuslostfound.model.*;
import com.campuslostfound.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ReportService reportService;
    private final UserService userService;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> getItemsByLocation(Location location) {
        return itemRepository.findByLocationId(location.getId());
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Long itemId, String name, String description, ItemStatus status, Long claimedByUserId) {
        Item existingItem = itemRepository.findById(itemId).orElseThrow(
            () -> new IllegalArgumentException("Item not found with id: " + itemId));

        // Prevent editing items from resolved reports
        if (existingItem.getReport() != null && existingItem.getReport().getStatus() == ReportStatus.RESOLVED) {
            throw new IllegalArgumentException("Cannot edit items from a resolved report. Resolved reports are immutable.");
        }

        // Store old status to check for changes
        ItemStatus oldStatus = existingItem.getStatus();

        if (name != null) {
            existingItem.setName(name);
        }
        if (description != null) {
            existingItem.setDescription(description);
        }
        if (status != null) {
            existingItem.setStatus(status);

            // Handle claimed by user
            if (status == ItemStatus.CLAIMED && claimedByUserId != null) {
                User claimedByUser = userService.getUserById(claimedByUserId).orElseThrow(
                    () -> new IllegalArgumentException("User not found with id: " + claimedByUserId));
                existingItem.setClaimedByUser(claimedByUser);
            } else if (status == ItemStatus.UNCLAIMED) {
                existingItem.setClaimedByUser(null);
            }
        }

        Item savedItem = itemRepository.save(existingItem);

        // Update report status if item status changed
        if (oldStatus != savedItem.getStatus() && savedItem.getReport() != null) {
            reportService.updateReportStatusBasedOnItems(savedItem.getReport().getId());
        }

        return savedItem;
    }

    public Item createItemFromReportId(String name, String description, Long reportId) {
        Report report = reportService.getReportById(reportId).orElseThrow(
            () -> new IllegalArgumentException("Report not found with id: " + reportId));

        // Prevent adding items to resolved reports
        if (report.getStatus() == ReportStatus.RESOLVED) {
            throw new IllegalArgumentException("Cannot add items to a resolved report. Resolved reports are immutable.");
        }

        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setReport(report);
        item.setStatus(ItemStatus.UNCLAIMED);

        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        // Get the item first to check if it's the last item in a report
        Item item = itemRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("Item not found with id: " + id));

        Report report = item.getReport();

        // Prevent deleting items from resolved reports
        if (report != null && report.getStatus() == ReportStatus.RESOLVED) {
            throw new IllegalArgumentException("Cannot delete items from a resolved report. Resolved reports are immutable.");
        }

        // Delete the item
        itemRepository.deleteById(id);

        // If the item was part of a report, check if it was the last item
        if (report != null) {
            // Refresh the report to get updated item count
            Optional<Report> updatedReport = reportService.getReportById(report.getId());

            if (updatedReport.isPresent()) {
                List<Item> remainingItems = updatedReport.get().getItems();

                // If no items remain, delete the empty report
                if (remainingItems == null || remainingItems.isEmpty()) {
                    reportService.deleteReport(report.getId());
                }
            }
        }
    }
}
