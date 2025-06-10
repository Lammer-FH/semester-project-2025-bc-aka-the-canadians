package com.campuslostfound.dto;

import com.campuslostfound.model.ReportStatus;
import com.campuslostfound.model.ReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Long id;
    private Long userId;
    private Long locationId;
    private Timestamp createdAt;
    private ReportType type;
    private ReportStatus status;
    private List<Long> itemIds;
    private LocationDTO location;
    private UserDTO user;
    private List<ItemDTO> items;
}
