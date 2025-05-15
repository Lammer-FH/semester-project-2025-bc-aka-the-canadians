package com.campuslostfound.service;

import com.campuslostfound.model.Item;
import com.campuslostfound.model.ItemStatus;
import com.campuslostfound.model.Location;
import com.campuslostfound.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<Item> getItemsByStatus(ItemStatus status) {
        return itemRepository.findByStatus(status);
    }

    public List<Item> getItemsByLocation(Location location) {
        return itemRepository.findByLocation(location);
    }

    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategory(category);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
} 