package com.campuslostfound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Long id;
    private String name;
    private String description;
    private String building;
    private String floor;
    private String roomNumber;
} 