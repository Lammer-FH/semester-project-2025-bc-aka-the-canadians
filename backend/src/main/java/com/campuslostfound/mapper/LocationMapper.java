package com.campuslostfound.mapper;

import com.campuslostfound.dto.LocationDTO;
import com.campuslostfound.model.Location;
import com.campuslostfound.model.Report;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LocationMapper extends GenericMapper<Location, LocationDTO> {
    @Override
    @Mapping(target = "reportIds", source = "reports", qualifiedByName = "reportListToIdList")
    LocationDTO toDTO(Location location);

    @Override
    @Mapping(target = "reports", ignore = true)
    Location toEntity(LocationDTO dto);

    @Named("reportListToIdList")
    public static List<Long> reportListToIdList(List<Report> reports) {
        if (reports == null) return null;
        return reports.stream().map(Report::getId).collect(Collectors.toList());
    }
}
