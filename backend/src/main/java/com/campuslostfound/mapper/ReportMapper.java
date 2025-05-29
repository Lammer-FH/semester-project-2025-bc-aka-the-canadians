package com.campuslostfound.mapper;

import com.campuslostfound.dto.ReportDTO;
import com.campuslostfound.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class, UserMapper.class, LocationMapper.class})
public interface ReportMapper {

    @Mapping(target = "type", expression = "java(report.getType().name())")
    @Mapping(target = "status", expression = "java(report.getStatus().name())")
    ReportDTO toDTO(Report report);

    List<ReportDTO> toDTOList(List<Report> reports);
}