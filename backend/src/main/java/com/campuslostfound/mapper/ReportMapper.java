package com.campuslostfound.mapper;

import com.campuslostfound.dto.ReportDTO;
import com.campuslostfound.model.Report;
import com.campuslostfound.model.ReportStatus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper extends GenericMapper<Report, ReportDTO> {
    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "itemIds", source = "items", qualifiedByName = "itemListToIdList")
    @Mapping(target = "status", expression = "java(report.getStatus().name())")
    ReportDTO toDTO(Report report);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "status", expression = "java(reportDTO.getStatus() != null ? com.campuslostfound.model.ReportStatus.valueOf(reportDTO.getStatus()) : null)")
    Report toEntity(ReportDTO reportDTO);

    @Named("itemListToIdList")
    public static java.util.List<Long> itemListToIdList(
            java.util.List<com.campuslostfound.model.Item> items) {
        if (items == null) return null;
        return items.stream()
                .map(com.campuslostfound.model.Item::getId)
                .collect(java.util.stream.Collectors.toList());
    }
}
