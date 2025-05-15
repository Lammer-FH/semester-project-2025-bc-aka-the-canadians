package com.campuslostfound.controller;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.dto.LocationDTO;
import com.campuslostfound.mapper.ItemMapper;
import com.campuslostfound.mapper.LocationMapper;
import com.campuslostfound.model.Item;
import com.campuslostfound.model.Location;
import com.campuslostfound.service.ItemService;
import com.campuslostfound.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;


import java.util.List;
import java.util.Optional;

@RestController 
@RequiredArgsConstructor
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;
    private final ItemService itemService;
    private final LocationMapper locationMapper;
    private final ItemMapper itemMapper;

    /**
     * Gibt alle Locations zurück, optional gefiltert nach Gebäude.
     */
    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations(
            @RequestParam(required = false) String building) {
        
        List<Location> locations;
        
        if (building != null) {
            locations = locationService.getLocationsByBuilding(building);
        } else {
            locations = locationService.getAllLocations();
        }
        
        return ResponseEntity.ok(locationMapper.toDTOList(locations));
    }

    /**
     * Gibt eine bestimmte Location anhand ihrer ID zurück.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        Optional<Location> location = locationService.getLocationById(id);
        return location.map(value -> ResponseEntity.ok(locationMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Gibt alle Items zurück, die an einer bestimmten Location gefunden/verloren wurden.
     */
    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemDTO>> getItemsByLocation(@PathVariable Long id) {
        Optional<Location> location = locationService.getLocationById(id);
        
        if (location.isPresent()) {
            List<Item> items = itemService.getItemsByLocation(location.get());
            return ResponseEntity.ok(itemMapper.toDTOList(items));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Erstellt eine neue Location.
     */
    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {
        Location location = locationMapper.toEntity(locationDTO);
        Location savedLocation = locationService.saveLocation(location);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationMapper.toDTO(savedLocation));
    }

    /**
     * Aktualisiert eine bestehende Location.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable Long id, @RequestBody LocationDTO locationDTO) {
        if (!locationService.getLocationById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        locationDTO.setId(id);
        Location updatedLocation = locationService.saveLocation(locationMapper.toEntity(locationDTO));
        
        return ResponseEntity.ok(locationMapper.toDTO(updatedLocation));
    }

    /**
     * Löscht eine Location anhand ihrer ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        if (!locationService.getLocationById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        locationService.deleteLocation(id);
        
        return ResponseEntity.noContent().build();
    }
} 