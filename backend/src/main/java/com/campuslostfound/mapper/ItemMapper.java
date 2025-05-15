package com.campuslostfound.mapper;

import com.campuslostfound.dto.ItemDTO;
import com.campuslostfound.model.Item;
import com.campuslostfound.model.ItemStatus;
import org.mapstruct.*;

/**
 * MapStruct-Mapper für Item-Entities und DTOs.
 */
@Mapper(componentModel = "spring", 
        uses = {LocationMapper.class, UserMapper.class})
public interface ItemMapper extends GenericMapper<Item, ItemDTO> {

    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    @Override
    ItemDTO toDTO(Item entity);

    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    @Override
    Item toEntity(ItemDTO dto);

    /**
     * Konvertiert einen Status-String in ein ItemStatus-Enum.
     */
    default ItemStatus mapStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return ItemStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ItemStatus.LOST; // Default-Wert
        }
    }
} 