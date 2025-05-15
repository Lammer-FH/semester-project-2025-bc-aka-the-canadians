package com.campuslostfound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String imageUrl;
    private LocalDateTime dateReported;
    private LocationDTO location;
    private String status;
    private UserDTO reportedBy;
} 