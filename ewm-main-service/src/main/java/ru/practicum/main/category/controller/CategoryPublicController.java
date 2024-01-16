package ru.practicum.main.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.service.CategoryService;

import javax.validation.constraints.*;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryPublicController {
    private final CategoryService catService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{catId}")
    public CategoryDto getCategoriesById(@PathVariable @NotNull Integer catId) {
        log.debug("Попытка получить категорию по идентификатору {}", catId);
        return catService.getCategoriesById(catId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CategoryDto> getCategory(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.debug("Попытка получить категории");
        return catService.getCategories(from, size);
    }

}
