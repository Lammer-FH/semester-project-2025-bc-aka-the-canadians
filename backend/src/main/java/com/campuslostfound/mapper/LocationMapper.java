package com.campuslostfound.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.campuslostfound.dto.LocationDTO;
import com.campuslostfound.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(target = "reportIds", expression = "java(location.getReports() != null ? location.getReports().stream().map(r -> r.getId()).collect(Collectors.toList()) : null)")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    LocationDTO toDto(Location location);

    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    Location toEntity(LocationDTO dto);
}
