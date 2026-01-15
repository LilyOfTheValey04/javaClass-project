package com.example.demo.controller;

import com.controller.CategoryController;
import com.controller.ReviewController;
import com.dto.category.CategoryResponseDTO;
import com.dto.review.ReviewCreateResponseDTO;
import com.mapper.CategoryMapper;
import com.mapper.ReviewMapper;
import com.model.Category;
import com.model.Review;
import com.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryMapper categoryMapper;

    @Test
    void getAllCategory_returnsList() throws Exception {

        Category category = new Category();

        CategoryResponseDTO dto = new CategoryResponseDTO(
                "Book"
        );

        when(categoryService.getAllCategories()).thenReturn(List.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(dto);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Book"));
    }

    @Test
    void deleteCategory_returnsNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/delete/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void deleteCategory_whenIdIsNegative_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/api/categories/delete/-1"))
                .andExpect(status().isBadRequest());
    }
}
