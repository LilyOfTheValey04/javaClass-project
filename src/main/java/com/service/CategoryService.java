package com.service;

import com.model.Category;
import com.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category getCategoryOrCreate(String name) {
        Optional<Category> categoryOptional = categoryRepository.findByName(name);
        return categoryOptional.orElseGet(()
                -> categoryRepository.save( Category.builder().name(name).build()));
    }
}
