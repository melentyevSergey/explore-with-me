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

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.main.utility.Page.paged;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryVerifier verifier;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto save(CategoryDto categoryDto) {
        verifier.checkAbilityCreateNameCategory(categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toEntity(categoryDto)));
    }

    @Override
    @Transactional
    public void removeById(Integer catId) {
        verifier.checkAbilityRemoveCategory(catId);
        categoryRepository.deleteById(verifier.checkCategory(catId).getId());
    }

    @Override
    @Transactional
    public CategoryDto changeCategory(Integer catId, CategoryDto categoryDto) {
        verifier.checkAbilityChangeNameCategory(categoryDto.getName(), catId);
        Category cat = verifier.checkCategory(catId);
        cat.setName(categoryDto.getName());
        return categoryMapper.toDto(categoryRepository.save(cat));
    }

    @Override
    public CategoryDto getCategoriesById(Integer catId) {
        return categoryMapper.toDto(verifier.checkCategory(catId));
    }

    @Override
    @Transactional
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = paged(from, size);
        return categoryRepository.findAll(page).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Category checkCategory(Integer catId) {
        return verifier.checkCategory(catId);
    }
}
