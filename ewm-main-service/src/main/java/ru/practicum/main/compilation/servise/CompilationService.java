package ru.practicum.main.compilation.servise;

import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilations(NewCompilationDto newCompilationDto);

    void deleteCompilations(Integer compId);

    CompilationDto changeCompilations(Integer compId, UpdateCompilationRequest updateCompilationRequest);

    CompilationDto getCompilationsById(Integer compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}
