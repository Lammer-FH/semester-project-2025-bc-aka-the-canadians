package com.campuslostfound.mapper;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.model.Item;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper extends GenericMapper<Item, ItemDTO> {
    @Override
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "claimedByUserId", source = "claimedByUser.id")
    @Mapping(target = "status", expression = "java(item.getStatus().name())")
    ItemDTO toDTO(Item item);

    @Override
    @Mapping(target = "report", ignore = true)
    @Mapping(target = "claimedByUser", ignore = true)
    @Mapping(target = "status", expression = "java(itemDTO.getStatus() != null ? com.campuslostfound.model.ItemStatus.valueOf(itemDTO.getStatus()) : null)")
    Item toEntity(ItemDTO itemDTO);
}
