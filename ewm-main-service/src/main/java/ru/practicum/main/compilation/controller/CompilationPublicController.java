package ru.practicum.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.servise.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationPublicController {
    private final CompilationService compilationService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{compId}")
    public CompilationDto getCompilationsById(@PathVariable(name = "compId") @Positive Integer compId) {
        log.debug("Получение подборки событий по его id");
        return compilationService.getCompilationsById(compId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        log.debug("Получение подборок событий");
        return compilationService.getCompilations(pinned, from, size);
    }
}
