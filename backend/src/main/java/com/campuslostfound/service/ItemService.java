package com.campuslostfound.service;

import com.campuslostfound.model.Item;
import com.campuslostfound.model.ItemStatus;
import com.campuslostfound.model.Location;
import com.campuslostfound.model.Report;
import com.campuslostfound.repository.ItemRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ReportService reportService;

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

    public Item updateItem(Long itemId, String name, String description) {
        Item existingItem = itemRepository.findById(itemId).orElseThrow(
            () -> new IllegalArgumentException("Item not found with id: " + itemId));

        existingItem.setName(name);
        existingItem.setDescription(description);

        return itemRepository.save(existingItem);
    }

    public Item createItemFromReportId(String name, String description, Long reportId, ItemStatus status) {
        Report report = reportService.getReportById(reportId).orElseThrow(
            () -> new IllegalArgumentException("Report not found with id: " + reportId));

        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setReport(report);
        item.setStatus(status);

        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}
