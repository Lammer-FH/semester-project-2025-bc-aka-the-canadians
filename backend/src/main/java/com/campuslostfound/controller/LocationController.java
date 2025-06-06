package com.campuslostfound.controller;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.dto.LocationDTO;
import com.campuslostfound.mapper.ItemMapper;
import com.campuslostfound.mapper.LocationMapper;
import com.campuslostfound.model.Item;
import com.campuslostfound.model.Location;
import com.campuslostfound.service.ItemService;
import com.campuslostfound.service.LocationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationMapper.toDTOList(locationService.getAllLocations()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        Optional<Location> location = locationService.getLocationById(id);

        return location.map(value -> ResponseEntity.ok(locationMapper.toDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/building/{building}")
    public ResponseEntity<List<LocationDTO>> getLocationsByBuilding(@PathVariable String building) {
        List<Location> locations = locationService.getLocationsByBuilding(building);
        return ResponseEntity.ok(locationMapper.toDTOList(locations));
    }

    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<LocationDTO>> getLocationsByFloor(@PathVariable String floor) {
        List<Location> locations = locationService.getLocationsByFloor(floor);
        return ResponseEntity.ok(locationMapper.toDTOList(locations));
    }

    @GetMapping("/search")
    public ResponseEntity<List<LocationDTO>> searchLocations(@RequestParam String query) {
        List<Location> locations = locationService.searchLocations(query);
        return ResponseEntity.ok(locationMapper.toDTOList(locations));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<LocationDTO>> findNearbyLocations(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radiusInKm) {
        List<Location> locations = locationService.findNearbyLocations(latitude, longitude, radiusInKm);
        return ResponseEntity.ok(locationMapper.toDTOList(locations));
    }

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

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {
        Location location = locationMapper.toEntity(locationDTO);
        Location savedLocation = locationService.createLocation(location);

        return ResponseEntity.status(HttpStatus.CREATED).body(locationMapper.toDTO(savedLocation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(
            @PathVariable Long id, @RequestBody LocationDTO locationDTO) {
        if (locationService.getLocationById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        locationDTO.setId(id);
        Location updatedLocation =
                locationService.updateLocation(id, locationMapper.toEntity(locationDTO));

        return ResponseEntity.ok(locationMapper.toDTO(updatedLocation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        if (locationService.getLocationById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        locationService.deleteLocation(id);

        return ResponseEntity.noContent().build();
    }
}
