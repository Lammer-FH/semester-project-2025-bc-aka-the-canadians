package com.campuslostfound.mapper;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ItemMapper extends GenericMapper<Item, ItemDTO> {
    @Override
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "claimedByUserId", source = "claimedByUser.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "locationId", source = "report.location.id")
    @Mapping(target = "locationName", source = "report.location.name")
    @Mapping(target = "reportStatus", source = "report.status")
    @Mapping(target = "reportType", source = "report.type")
    @Mapping(target = "reporterUserId", source = "report.user.id")
    @Mapping(target = "reporterUserName", source = "report.user.name")
    ItemDTO toDTO(Item item);

    @Override
    @Mapping(target = "report", ignore = true)
    @Mapping(target = "claimedByUser", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    Item toEntity(ItemDTO dto);
}
