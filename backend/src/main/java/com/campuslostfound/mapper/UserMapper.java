package com.campuslostfound.mapper;

import com.campuslostfound.dto.UserDTO;
import com.campuslostfound.model.User;
import org.mapstruct.*;
import java.util.stream.Collectors;

/**
 * MapStruct-Mapper für User-Entities und DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "reportIds", expression = "java(user.getReports() != null ? user.getReports().stream().map(r -> r.getId()).collect(Collectors.toList()) : null)")
    @Mapping(target = "claimedItemIds", expression = "java(user.getClaimedItems() != null ? user.getClaimedItems().stream().map(i -> i.getId()).collect(Collectors.toList()) : null)")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "createdAt", source = "createdAt")
    UserDTO toDto(User user);

    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "claimedItems", ignore = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "createdAt", source = "createdAt")
    User toEntity(UserDTO dto);
}