package com.campuslostfound.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.campuslostfound.dto.ReportDTO;
import com.campuslostfound.model.Report;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "itemIds", expression = "java(report.getItems() != null ? report.getItems().stream().map(i -> i.getId()).collect(Collectors.toList()) : null)")
    @Mapping(target = "id", source = "id")
    ReportDTO toDto(Report report);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "id", source = "id")
    Report toEntity(ReportDTO dto);
}
