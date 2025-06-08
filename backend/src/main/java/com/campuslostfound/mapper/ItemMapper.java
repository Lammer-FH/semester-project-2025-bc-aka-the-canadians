package com.campuslostfound.mapper;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.model.Item;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ReportMapper.class, UserMapper.class})
public interface ItemMapper {
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "claimedByUserId", source = "claimedByUser.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "report", source = "report")
    ItemDTO toDTO(Item item);

    @Mapping(target = "report", ignore = true)
    @Mapping(target = "claimedByUser", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    Item toEntity(ItemDTO dto);

    List<ItemDTO> toDTOList(List<Item> allItems);
}
