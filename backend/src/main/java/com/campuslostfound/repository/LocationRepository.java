package com.campuslostfound.repository;

import com.campuslostfound.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByBuilding(String building);
    Location findByNameAndBuilding(String name, String building);
} 