package com.campuslostfound.repository;

import com.campuslostfound.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByName(String name);
    List<Location> findByBuildingIgnoreCase(String building);
    List<Location> findByFloorIgnoreCase(String floor);
    List<Location> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
