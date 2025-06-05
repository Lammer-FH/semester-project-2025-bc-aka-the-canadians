package com.campuslostfound.controller;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.mapper.ItemMapper;
import com.campuslostfound.model.Item;
import com.campuslostfound.service.ItemService;

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
    private final ItemMapper itemMapper;

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        return ResponseEntity.ok(itemMapper.toDTOList(itemService.getAllItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItemById(id);

        return item.map(value -> ResponseEntity.ok(itemMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        Item item = itemMapper.toEntity(itemDTO);
        Item savedItem = itemService.saveItem(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.toDTO(savedItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        if (itemService.getItemById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Setzen der ID, um sicherzustellen, dass das richtige Item aktualisiert wird
        itemDTO.setId(id);

        Item updatedItem = itemService.saveItem(itemMapper.toEntity(itemDTO));

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
