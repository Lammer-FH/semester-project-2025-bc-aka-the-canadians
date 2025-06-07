package com.campuslostfound.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

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

    private ReportDTO report;
}
