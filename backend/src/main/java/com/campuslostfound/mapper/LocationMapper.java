package com.campuslostfound.mapper;

import com.campuslostfound.dto.LocationDTO;
import com.campuslostfound.model.Location;
import org.mapstruct.Mapper;

/**
 * MapStruct-Mapper für Location-Entities und DTOs.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper extends GenericMapper<Location, LocationDTO> {
    // Standardimplementierungen aus GenericMapper werden verwendet
} 