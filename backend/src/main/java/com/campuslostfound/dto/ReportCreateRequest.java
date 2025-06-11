package com.campuslostfound.dto;

import com.campuslostfound.model.ReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportCreateRequest {
    private Long userId;
    private Long locationId;
    private ReportType type;
    private List<ItemCreateRequest> items;
}
