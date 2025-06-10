package com.campuslostfound.dto;

import java.sql.Timestamp;

import com.campuslostfound.model.ItemStatus;
import com.campuslostfound.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private Long reportId;
    private Long claimedByUserId;
    private Timestamp createdAt;
    private Long locationId;
    private String locationName;
    private ItemStatus status;
    private ReportStatus reportStatus;
    private Long reporterUserId;
    private String reporterUserName;
}
