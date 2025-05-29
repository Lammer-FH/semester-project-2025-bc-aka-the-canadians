package com.campuslostfound.mapper;

import com.campuslostfound.dto.ReportDTO;
import com.campuslostfound.model.Report;
import com.campuslostfound.model.ReportStatus;
import com.campuslostfound.model.ReportType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface ReportMapper extends GenericMapper<Report, ReportDTO> {

    @Mapping(target = "type", expression = "java(report.getType() != null ? report.getType().name() : null)")
    @Mapping(target = "status", expression = "java(report.getStatus() != null ? report.getStatus().name() : null)")
    @Override
    ReportDTO toDTO(Report report);

    @Mapping(target = "type", expression = "java(mapType(dto.getType()))")
    @Mapping(target = "status", expression = "java(mapStatus(dto.getStatus()))")
    @Override
    Report toEntity(ReportDTO dto);

    default ReportStatus mapStatus(String status) {
        try {
            return status != null ? ReportStatus.valueOf(status) : null;
        } catch (IllegalArgumentException e) {
            return ReportStatus.OPEN;
        }
    }

    default ReportType mapType(String type) {
        try {
            return type != null ? ReportType.valueOf(type) : null;
        } catch (IllegalArgumentException e) {
            return ReportType.OTHER;
        }
    }
}