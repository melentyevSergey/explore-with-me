package ru.practicum.main.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.location.dto.LocationDto;
import ru.practicum.main.location.model.Location;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    Location toEntity(LocationDto locationDto);

    LocationDto toDto(Location location);
}
