package com.campuslostfound.dto;

import java.sql.Timestamp;

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
    private String category;
    private String imageUrl;
    private String status;
    private Long reportId;
    private Long claimedByUserId;
    private Timestamp createdAt;
}
