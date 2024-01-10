package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.utility.Utility;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.utility.Page.paged;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final Utility utility;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto save(CategoryDto categoryDto) {
        utility.checkAbilityCreateNameCategory(categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toEntity(categoryDto)));
    }

    @Override
    @Transactional
    public void removeById(Integer catId) {
        utility.checkAbilityRemoveCategory(catId);
        categoryRepository.deleteById(utility.checkCategory(catId).getId());
    }

    @Override
    @Transactional
    public CategoryDto changeCategory(Integer catId, CategoryDto categoryDto) {
        utility.checkAbilityChangeNameCategory(categoryDto.getName(), catId);
        Category cat = utility.checkCategory(catId);
        cat.setName(categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(cat));
    }

    @Override
    public CategoryDto getCategoriesById(Integer catId) {
        return categoryMapper.toDto(utility.checkCategory(catId));
    }

    @Override
    @Transactional
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = paged(from, size);
        return categoryRepository.findAll(page).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}
