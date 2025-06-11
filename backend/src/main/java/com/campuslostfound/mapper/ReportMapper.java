package com.campuslostfound.mapper;

import com.campuslostfound.dto.ReportDTO;
import com.campuslostfound.model.Item;
import com.campuslostfound.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {LocationMapper.class, UserMapper.class, ItemMapper.class})
public interface ReportMapper extends GenericMapper<Report, ReportDTO> {
    @Named("reportItemListToIdList")
    static List<Long> itemListToIdList(
        List<Item> items) {
        return UserMapper.itemListToIdList(items);
    }

    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "itemIds", source = "items", qualifiedByName = "reportItemListToIdList")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "items", source = "items")
    ReportDTO toDTO(Report report);

    @Override
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "items", ignore = true)
    Report toEntity(ReportDTO dto);

    List<ReportDTO> toDTOList(List<Report> reports);
}
