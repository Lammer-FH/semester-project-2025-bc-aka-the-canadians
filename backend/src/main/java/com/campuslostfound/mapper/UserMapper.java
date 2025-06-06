package com.campuslostfound.mapper;

import com.campuslostfound.dto.UserDTO;
import com.campuslostfound.model.User;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User, UserDTO> {
    @Override
    @Mapping(target = "reportIds", source = "reports", qualifiedByName = "reportListToIdList")
    @Mapping(target = "claimedItemIds", source = "claimedItems", qualifiedByName = "itemListToIdList")
    UserDTO toDTO(User user);

    @Override
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "claimedItems", ignore = true)
    User toEntity(UserDTO dto);

    @Named("reportListToIdList")
    public static java.util.List<Long> reportListToIdList(java.util.List<com.campuslostfound.model.Report> reports) {
        if (reports == null) return null;
        return reports.stream().map(com.campuslostfound.model.Report::getId).collect(java.util.stream.Collectors.toList());
    }

    @Named("itemListToIdList")
    public static java.util.List<Long> itemListToIdList(java.util.List<com.campuslostfound.model.Item> items) {
        if (items == null) return null;
        return items.stream().map(com.campuslostfound.model.Item::getId).collect(java.util.stream.Collectors.toList());
    }
}
