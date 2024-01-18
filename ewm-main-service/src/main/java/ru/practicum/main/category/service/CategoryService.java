package ru.practicum.main.category.service;

import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    void removeById(Integer catId);

    CategoryDto changeCategory(Integer catId, CategoryDto categoryDto);

    CategoryDto getCategoriesById(Integer catId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    Category checkCategory(Integer catId);
}
