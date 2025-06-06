package com.campuslostfound.controller;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.mapper.ItemMapper;
import com.campuslostfound.model.Item;
import com.campuslostfound.model.ItemStatus;
import com.campuslostfound.model.User;
import com.campuslostfound.service.ItemService;
import com.campuslostfound.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String search) {
        
        List<Item> items;
        if (search != null && !search.trim().isEmpty()) {
            items = itemService.searchItems(search);
        } else if (status != null) {
            items = itemService.getItemsByStatus(ItemStatus.valueOf(status.toUpperCase()));
        } else if (category != null) {
            items = itemService.getItemsByCategory(category);
        } else {
            items = itemService.getAllItems();
        }
        return ResponseEntity.ok(itemMapper.toDTOList(items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItemById(id);

        return item.map(value -> ResponseEntity.ok(itemMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ItemDTO>> getItemsByStatus(@PathVariable String status) {
        try {
            ItemStatus itemStatus = ItemStatus.valueOf(status.toUpperCase());
            List<Item> items = itemService.getItemsByStatus(itemStatus);
            return ResponseEntity.ok(itemMapper.toDTOList(items));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ItemDTO>> getItemsByCategory(@PathVariable String category) {
        List<Item> items = itemService.getItemsByCategory(category);
        return ResponseEntity.ok(itemMapper.toDTOList(items));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemDTO>> getItemsByUser(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Item> items = itemService.getItemsByUser(user.get());
            return ResponseEntity.ok(itemMapper.toDTOList(items));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/claimed/{userId}")
    public ResponseEntity<List<ItemDTO>> getClaimedItemsByUser(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Item> items = itemService.getClaimedItemsByUser(user.get());
            return ResponseEntity.ok(itemMapper.toDTOList(items));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItems(@RequestParam String query) {
        List<Item> items = itemService.searchItems(query);
        return ResponseEntity.ok(itemMapper.toDTOList(items));
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        Item item = itemMapper.toEntity(itemDTO);
        Item savedItem = itemService.createItem(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.toDTO(savedItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        if (itemService.getItemById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Setzen der ID, um sicherzustellen, dass das richtige Item aktualisiert wird
        itemDTO.setId(id);

        Item updatedItem = itemService.updateItem(id, itemMapper.toEntity(itemDTO));

        return ResponseEntity.ok(itemMapper.toDTO(updatedItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemService.getItemById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        itemService.deleteItem(id);

        return ResponseEntity.noContent().build();
    }
}
