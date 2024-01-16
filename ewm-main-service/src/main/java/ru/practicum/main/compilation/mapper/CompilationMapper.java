package ru.practicum.main.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.service.EventService;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {EventService.class})
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    CompilationDto toDto(Compilation compilation);

    @Mapping(source = "events", target = "events")
    Compilation toEntity(NewCompilationDto newCompilationDto);

}
