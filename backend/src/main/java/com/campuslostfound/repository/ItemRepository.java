package com.campuslostfound.repository;

import com.campuslostfound.model.Item;
import com.campuslostfound.model.ItemStatus;
import com.campuslostfound.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.report.location.id = :locationId")
    List<Item> findByLocationId(@Param("locationId") Long locationId);
    List<Item> findByStatus(ItemStatus status);
    List<Item> findByCategoryIgnoreCase(String category);
    List<Item> findByReportUser(User user);
    List<Item> findByClaimedByUser(User user);
    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
