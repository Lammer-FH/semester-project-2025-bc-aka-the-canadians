package com.campuslostfound.controller;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.mapper.ItemMapper;
import com.campuslostfound.model.Item;
import com.campuslostfound.model.ItemStatus;
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



    /**
     * Gibt alle Items zurück, optional gefiltert nach Status oder Kategorie.
     */
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        
        List<Item> items;
        
        if (status != null) {
            try {
                ItemStatus itemStatus = ItemStatus.valueOf(status);
                items = itemService.getItemsByStatus(itemStatus);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else if (category != null) {
            items = itemService.getItemsByCategory(category);
        } else {
            items = itemService.getAllItems();
        }
        
        return ResponseEntity.ok(itemMapper.toDTOList(items));
    }

    /**
     * Gibt ein bestimmtes Item anhand seiner ID zurück.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItemById(id);
        return item.map(value -> ResponseEntity.ok(itemMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Erstellt ein neues Item.
     */
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        // Konvertierung von DTO zu Entity
        Item item = itemMapper.toEntity(itemDTO);
        
        // Speichern der Entity
        Item savedItem = itemService.saveItem(item);
        
        // Konvertierung zurück zu DTO für die Antwort
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemMapper.toDTO(savedItem));
    }

    /**
     * Aktualisiert ein bestehendes Item.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
        // Prüfen, ob Item existiert
        if (!itemService.getItemById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Setzen der ID, um sicherzustellen, dass das richtige Item aktualisiert wird
        itemDTO.setId(id);
        
        // Konvertierung und Speichern
        Item updatedItem = itemService.saveItem(itemMapper.toEntity(itemDTO));
        
        // Konvertierung zurück zu DTO
        return ResponseEntity.ok(itemMapper.toDTO(updatedItem));
    }

    /**
     * Löscht ein Item anhand seiner ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        // Prüfen, ob Item existiert
        if (!itemService.getItemById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Löschen des Items
        itemService.deleteItem(id);
        
        return ResponseEntity.noContent().build();
    }
} 