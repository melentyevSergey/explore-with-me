package ru.practicum.main.stat.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.stat.dto.EndpointHitDto;
import ru.practicum.main.stat.service.model.EndpointHit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EndpointHitMapper {
    EndpointHitMapper INSTANCE = Mappers.getMapper(EndpointHitMapper.class);

    EndpointHit toEntity(EndpointHitDto hitDto);

    EndpointHitDto toDto(EndpointHit hit);
}