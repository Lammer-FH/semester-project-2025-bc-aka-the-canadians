package com.campuslostfound.repository;

import com.campuslostfound.model.Report;
import com.campuslostfound.model.ReportStatus;
import com.campuslostfound.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatus(ReportStatus status);

    List<Report> findByUser(User user);

    @Query("SELECT r FROM Report r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.location " +
           "LEFT JOIN FETCH r.items i " +
           "LEFT JOIN FETCH i.claimedByUser " +
           "WHERE r.id = :id")
    Optional<Report> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT DISTINCT r FROM Report r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.location " +
           "LEFT JOIN FETCH r.items i " +
           "LEFT JOIN FETCH i.claimedByUser")
    List<Report> findAllWithItems();
}
