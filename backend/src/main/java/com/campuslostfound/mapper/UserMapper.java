package com.campuslostfound.mapper;

import com.campuslostfound.dto.UserDTO;
import com.campuslostfound.model.Item;
import com.campuslostfound.model.Report;
import com.campuslostfound.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<User, UserDTO> {
    @Named("reportListToIdList")
    static List<Long> reportListToIdList(List<Report> reports) {
        if (reports == null) return null;
        return reports.stream()
            .map(Report::getId)
            .collect(Collectors.toList());
    }

    @Named("itemListToIdList")
    static List<Long> itemListToIdList(List<Item> items) {
        if (items == null) return null;
        return items.stream()
            .map(Item::getId)
            .collect(Collectors.toList());
    }

    @Override
    @Mapping(target = "reportIds", source = "reports", qualifiedByName = "reportListToIdList")
    @Mapping(target = "claimedItemIds", source = "claimedItems", qualifiedByName = "itemListToIdList")
    UserDTO toDTO(User user);

    @Override
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "claimedItems", ignore = true)
    User toEntity(UserDTO dto);
}
