package com.campuslostfound.dto;

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
    private Boolean status;
    private List<Long> itemIds;

    private LocationDTO location;
    private UserDTO user;
}
