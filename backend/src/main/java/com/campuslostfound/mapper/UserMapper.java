package com.campuslostfound.mapper;

import com.campuslostfound.dto.UserDTO;
import com.campuslostfound.model.User;
import org.mapstruct.Mapper;

/**
 * MapStruct-Mapper für User-Entities und DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User, UserDTO> {
    // Standardimplementierungen aus GenericMapper werden verwendet
} 