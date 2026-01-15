package com.example.demo.repository;

import com.model.Category;
import com.model.Material;
import com.repository.CategoryRepository;
import com.repository.MaterialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional
@Sql(scripts = {"/sql/owner_data.sql","/sql/material_data.sql","/sql/category_data.sql"})
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByName_returnsCategory()  {
        String name = "Book";

        Optional<Category> result = categoryRepository.findByName(name);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertTrue(result.isPresent());
        assertEquals(name,result.get().getName());
    }

    @Test
    void findByName_whenNotFound_returnsEmptyOptional() {
        String name = "NonExistentCategory";

        Optional<Category> result = categoryRepository.findByName(name);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Category should not be found");
    }
}
