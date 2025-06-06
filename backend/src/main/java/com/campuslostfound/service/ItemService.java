package com.campuslostfound.service;

import com.campuslostfound.model.Item;
import com.campuslostfound.model.ItemStatus;
import com.campuslostfound.model.Location;
import com.campuslostfound.model.User;
import com.campuslostfound.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> getItemsByLocation(Location location) {
        return itemRepository.findByLocationId(location.getId());
    }

    public List<Item> getItemsByStatus(ItemStatus status) {
        return itemRepository.findByStatus(status);
    }

    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategoryIgnoreCase(category);
    }

    public List<Item> getItemsByUser(User user) {
        return itemRepository.findByReportUser(user);
    }

    public List<Item> getClaimedItemsByUser(User user) {
        return itemRepository.findByClaimedByUser(user);
    }

    @Transactional
    public Item createItem(Item item) {
        validateItem(item);
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long id, Item itemDetails) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
        
        validateItem(itemDetails);
        
        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());
        item.setCategory(itemDetails.getCategory());
        item.setImageUrl(itemDetails.getImageUrl());
        item.setStatus(itemDetails.getStatus());
        item.setReport(itemDetails.getReport());
        item.setClaimedByUser(itemDetails.getClaimedByUser());
        
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    public List<Item> searchItems(String query) {
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (item.getStatus() == null) {
            throw new IllegalArgumentException("Item status is required");
        }
        if (item.getReport() == null) {
            throw new IllegalArgumentException("Item must be associated with a report");
        }
    }
}
