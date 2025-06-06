package com.campuslostfound.service;

import com.campuslostfound.model.Location;
import com.campuslostfound.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public List<Location> getLocationsByBuilding(String building) {
        return locationRepository.findByBuildingIgnoreCase(building);
    }

    public List<Location> getLocationsByFloor(String floor) {
        return locationRepository.findByFloorIgnoreCase(floor);
    }

    @Transactional
    public Location createLocation(Location location) {
        validateLocation(location);
        return locationRepository.save(location);
    }

    @Transactional
    public Location updateLocation(Long id, Location locationDetails) {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));
        
        validateLocation(locationDetails);
        
        location.setName(locationDetails.getName());
        location.setDescription(locationDetails.getDescription());
        location.setBuilding(locationDetails.getBuilding());
        location.setFloor(locationDetails.getFloor());
        location.setRoomNumber(locationDetails.getRoomNumber());
        location.setLatitude(locationDetails.getLatitude());
        location.setLongitude(locationDetails.getLongitude());
        
        return locationRepository.save(location);
    }

    @Transactional
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }

    public List<Location> searchLocations(String query) {
        return locationRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    public List<Location> findNearbyLocations(Double latitude, Double longitude, Double radiusInKm) {
        // Haversine-Formel für Entfernungsberechnung
        return locationRepository.findAll().stream()
            .filter(loc -> loc.getLatitude() != null && loc.getLongitude() != null)
            .filter(loc -> calculateDistance(
                latitude, longitude,
                loc.getLatitude(), loc.getLongitude()
            ) <= radiusInKm)
            .toList();
    }

    private void validateLocation(Location location) {
        if (location.getName() == null || location.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Location name is required");
        }
        if (location.getLatitude() != null && (location.getLatitude() < -90 || location.getLatitude() > 90)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (location.getLongitude() != null && (location.getLongitude() < -180 || location.getLongitude() > 180)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
    }

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // Erdradius in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
