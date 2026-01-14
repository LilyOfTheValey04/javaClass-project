package com.service;

import com.exception.ResourceNotFound;
import com.model.Category;
import com.model.Material;
import com.repository.CategoryRepository;
import com.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(Category.class, id));

        category.getMaterials()
                .forEach(m -> m.getCategories().remove(category));

        categoryRepository.delete(category);

    }
    @Transactional
    public Category getCategoryOrCreate(String name) {
        Optional<Category> categoryOptional = categoryRepository.findByName(name);
        return categoryOptional.orElseGet(()
                -> categoryRepository.save( Category.builder().name(name).build()));
    }

    @Transactional
    public Set<Category> getCategoriesOrCreate(Set<String> categoryNames) {
        if (categoryNames == null || categoryNames.isEmpty()) {
            return Set.of();
        }

        return categoryNames.stream()
                .map(this::getCategoryOrCreate)
                .collect(Collectors.toSet());
    }

}
