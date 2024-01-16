package ru.practicum.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.servise.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationAdminController {
    private final CompilationService compilationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationDto createCompilations(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.debug("Добавление новой подборки (подборка может не содержать событий)");
        return compilationService.createCompilations(newCompilationDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{compId}")
    public void deleteCompilations(@PathVariable(name = "compId") Integer compId) {
        log.debug("Удаление подбрки");
        compilationService.deleteCompilations(compId);
    }

    @PatchMapping(path = "/{compId}")
    public CompilationDto changeCompilations(@PathVariable(name = "compId") Integer compId,
                                             @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.debug("Обновить информацию о подборке");
        return compilationService.changeCompilations(compId, updateCompilationRequest);
    }
}
