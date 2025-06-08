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

@RestController @RequiredArgsConstructor
    @RequestMapping("/api/v1/locations") public class LocationController {

private
  final LocationService locationService;
private
  final ItemService itemService;
private
  final LocationMapper locationMapper;
private
  final ItemMapper itemMapper;

  @GetMapping public ResponseEntity<List<LocationDTO>> getAllLocations() {
    return ResponseEntity.ok(
        locationMapper.toDTOList(locationService.getAllLocations()));
  }

  @GetMapping("/{id}") public ResponseEntity<LocationDTO> getLocationById(
      @PathVariable Long id) {
    Optional<Location> location = locationService.getLocationById(id);

    return location.map(value->ResponseEntity.ok(locationMapper.toDTO(value)))
        .orElseGet(()->ResponseEntity.notFound().build());
  }

  @GetMapping("/{id}/items") public ResponseEntity<
      List<ItemDTO>> getItemsByLocation(@PathVariable Long id) {
    Optional<Location> location = locationService.getLocationById(id);

    if (location.isPresent()) {
      List<Item> items = itemService.getItemsByLocation(location.get());
      return ResponseEntity.ok(itemMapper.toDTOList(items));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping public ResponseEntity<LocationDTO>
  createLocation(@RequestBody LocationDTO locationDTO) {
    Location location = locationMapper.toEntity(locationDTO);
    Location savedLocation = locationService.saveLocation(location);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(locationMapper.toDTO(savedLocation));
  }

  @PutMapping("/{id}") public ResponseEntity<LocationDTO> updateLocation(
      @PathVariable Long id, @RequestBody LocationDTO locationDTO) {
    Optional<Location> existingLocation = locationService.getLocationById(id);
    if (existingLocation.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    // locationDTO.setId(id);
    existingLocation.get().setName(locationDTO.getName());
    existingLocation.get().setDescription(locationDTO.getDescription());
    locationService.saveLocation(existingLocation.get());

    return ResponseEntity.ok(locationMapper.toDTO(existingLocation.get()));
  }

  @DeleteMapping("/{id}") public ResponseEntity<Void> deleteLocation(
      @PathVariable Long id) {
    if (locationService.getLocationById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    locationService.deleteLocation(id);

    return ResponseEntity.noContent().build();
  }
}
