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

        existingItem.setName(name);
        existingItem.setDescription(description);
        existingItem.setStatus(status);

        if (claimedByUserId != null) {
            User claimedByUser = userService.getUserById(claimedByUserId).orElseThrow(
                () -> new IllegalArgumentException("User not found with id: " + claimedByUserId));
            existingItem.setClaimedByUser(claimedByUser);
        } else {
            existingItem.setClaimedByUser(null);
        }

        return itemRepository.save(existingItem);
    }

    public Item createItemFromReportId(String name, String description, Long reportId) {
        Report report = reportService.getReportById(reportId).orElseThrow(
            () -> new IllegalArgumentException("Report not found with id: " + reportId));

        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setReport(report);
        item.setStatus(ItemStatus.UNCLAIMED);

        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
