package com.campuslostfound.mapper;

import com.campuslostfound.dto.ReportDTO;
import com.campuslostfound.model.Report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "itemIds", source = "items", qualifiedByName = "itemListToIdList")
    ReportDTO toDTO(Report report);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "items", ignore = true)
    Report toEntity(ReportDTO dto);

    @Named("itemListToIdList")
    public static java.util.List<Long> itemListToIdList(
            java.util.List<com.campuslostfound.model.Item> items) {
        if (items == null) return null;
        return items.stream()
                .map(com.campuslostfound.model.Item::getId)
                .collect(java.util.stream.Collectors.toList());
    }

    List<ReportDTO> toDTOList(List<Report> reports);
}
