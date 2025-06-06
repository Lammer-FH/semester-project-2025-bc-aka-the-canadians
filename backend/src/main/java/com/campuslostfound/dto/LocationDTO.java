package com.campuslostfound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private Long id;
    private String name;
    private String description;
    private String building;
    private String floor;
    private String roomNumber;
    private Double latitude;
    private Double longitude;
    private Timestamp createdAt;
    private List<Long> reportIds;
}
