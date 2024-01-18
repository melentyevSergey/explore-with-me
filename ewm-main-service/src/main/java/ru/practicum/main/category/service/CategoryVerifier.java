package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.service.EventVerifier;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryVerifier {
    private final CategoryRepository categoryRepository;
    private final EventVerifier eventVerifier;

    public Category checkCategory(Integer catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Категория с идентификатором =%d не найдена", catId)));
    }

    void checkAbilityCreateNameCategory(String name) {
        if (!categoryRepository.findCategoryByName(name).isEmpty()) {
            throw new ConflictException("Данное название категории уже занято!");
        }
    }

    void checkAbilityChangeNameCategory(String name, Integer catId) {
        for (Category cat : categoryRepository.findCategoryByName(name)) {
            if (!cat.getId().equals(catId)) {
                throw new ConflictException("Данное название категории уже занято!");
            }
        }
    }

    void checkAbilityRemoveCategory(Integer catId) {
        eventVerifier.checkAbilityRemoveCategory(catId);
    }
}
