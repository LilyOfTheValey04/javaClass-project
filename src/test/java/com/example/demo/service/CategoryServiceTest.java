package com.example.demo.service;

import com.mapper.MaterialMapper;
import com.model.Category;
import com.repository.CategoryRepository;
import com.repository.MaterialRepository;
import com.repository.UserRepository;
import com.service.CategoryService;
import com.service.MaterialService;
import com.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoryServiceTest {
    private  CategoryRepository categoryRepository;
    private  MaterialRepository materialRepository;
    private CategoryService categoryService;
    @BeforeEach
    void setUp() {
        materialRepository = mock(MaterialRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(
                 categoryRepository, materialRepository);
    }

    @Test
    void getCategoryOrCreate_whenFound() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Books");

        when(categoryRepository.findByName("Books")).thenReturn(Optional.of(existing));

        Category result = categoryService.getCategoryOrCreate("Books");

        assertThat(result).isEqualTo(existing);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getCategoriesOrCreate_whenEmptyOrNull() {
        Set<Category> result = categoryService.getCategoriesOrCreate(Set.of());
        assertThat(result).isEmpty();

        result = categoryService.getCategoriesOrCreate(null);
        assertThat(result).isEmpty();
    }

    @Test
    void getCategoryOrCreate_whenNotFound() {
        when(categoryRepository.findByName("NewCategory")).thenReturn(Optional.empty());

        Category saved = new Category();
        saved.setId(2L);
        saved.setName("NewCategory");

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        Category result = categoryService.getCategoryOrCreate("NewCategory");

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("NewCategory");
        verify(categoryRepository).save(any(Category.class));
    }
}
