package com.campuslostfound.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateCreated;
    private String type;
    private String status;
    private UserDTO reportedBy;
    private List<ItemDTO> items;
}