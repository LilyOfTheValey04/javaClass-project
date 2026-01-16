package com.example.demo.controller;

import com.controller.MaterialController;
import com.dto.material.MaterialCreateRequestDTO;
import com.dto.material.MaterialCreateResponseDTO;
import com.exception.ResourceNotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapper.MaterialMapper;
import com.model.Material;
import com.model.User;
import com.model.Category;
import com.service.CategoryService;
import com.service.MaterialService;
import com.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MaterialControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaterialService materialService;

    @MockitoBean
    private MaterialMapper materialMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void getMaterialById_returnsMappedDto() throws Exception {
        Long id = 1L;

        User user = User.builder().username("ivan123").build();
        Mockito.when(userService.getUserById(id)).thenReturn(user);

        Set<Category> categories = Set.of(
                Category.builder().name("Southern Gothic").build(),
                Category.builder().name("Novel").build()
        );
        Mockito.when(categoryService.getCategoriesOrCreate(any())).thenReturn(categories);

        Material material = Material.builder()
                .name("To Kill a Mockingbird")
                .owner(user)
                .price(BigDecimal.valueOf(10.3))
                .author("Harper Lee")
                .categories(categories)
                .description("A powerful story about racial injustice in the South. ")
                .quantity(8)
                .build();

        MaterialCreateResponseDTO materialCreateResponseDTO = new MaterialCreateResponseDTO(
                "To Kill a Mockingbird",
                "A powerful story about racial injustice in the South. ",
                10.3,
                8,
                "Harper Lee",
                "ivan123",
                Set.of("Southern Gothic", "Novel")
        );

        Mockito.when(materialService.getMaterialById(id))
                .thenReturn(material);

        Mockito.when(materialMapper.toResponse(material))
                .thenReturn(materialCreateResponseDTO);

        mockMvc.perform(get("/api/materials/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name")
                        .value(materialCreateResponseDTO.name()))
                .andExpect(jsonPath("$.description")
                        .value(materialCreateResponseDTO.description()))
                .andExpect(jsonPath("$.price")
                        .value(materialCreateResponseDTO.price()))
                .andExpect(jsonPath("$.quantity").
                        value(materialCreateResponseDTO.quantity()))
                .andExpect(jsonPath("$.ownerUsername")
                        .value(materialCreateResponseDTO.ownerUsername()))
                .andExpect(jsonPath("$.categoryNames",
                        containsInAnyOrder(
                                materialCreateResponseDTO.categoryNames().toArray()
                        )));
    }

    @Test
    void getMaterialById_whenNotFound_returns404() throws Exception {
        Long id = 1L;

        Mockito.when(materialService.getMaterialById(id))
                .thenThrow(new ResourceNotFound(Material.class, id));

        mockMvc.perform(get("/api/materials/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void deleteMaterial_returnsDeleteMaterial() throws Exception {
        Long id = 1L;

        User user = User.builder().username("ivan123").build();
        Mockito.when(userService.getUserById(id)).thenReturn(user);

        Set<Category> categories = Set.of(
                Category.builder().name("Southern Gothic").build(),
                Category.builder().name("Novel").build()
        );
        Mockito.when(categoryService.getCategoriesOrCreate(any())).thenReturn(categories);

        Material material = Material.builder()
                .name("To Kill a Mockingbird")
                .owner(user)
                .price(BigDecimal.valueOf(10.3))
                .author("Harper Lee")
                .categories(categories)
                .description("A powerful story about racial injustice in the South. ")
                .quantity(8)
                .build();

        Mockito.doNothing().when(materialService).deleteMaterial(id);

        mockMvc.perform(delete("/api/materials/delete/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


    @Test
    void createMaterial_returnsMappedDTO() throws Exception {
        Long id = 1L;

        MaterialCreateRequestDTO materialCreateRequestDTO = new MaterialCreateRequestDTO(
                "To Kill a Mockingbird",
                "A powerful story about racial injustice in the South. ",
               BigDecimal.valueOf( 10.3),
                8,
                "Harper Lee",
                id,
                Set.of("Southern Gothic", "Novel")
        );
        MaterialCreateResponseDTO responseDTO = new MaterialCreateResponseDTO("To Kill a Mockingbird",
                "A powerful story about racial injustice in the South.",
                10.3,
                8,
                "Harper Lee",
                "ivan123",
                Set.of("Southern Gothic", "Novel"));

        Mockito.when(materialService.createMaterial(Mockito.any(MaterialCreateRequestDTO.class)))
                .thenReturn(new Material());
        Mockito.when(materialMapper.toResponse(Mockito.any(Material.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/materials/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(materialCreateRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("To Kill a Mockingbird"))
                .andExpect(jsonPath("$.ownerUsername").value("ivan123"));
    }

    @Test
    void createMaterial_whenNotFound() throws Exception{
        Long id = 1L;

        MaterialCreateRequestDTO materialCreateRequestDTO = new MaterialCreateRequestDTO(
                "To Kill a Mockingbird",
                "A powerful story about racial injustice in the South. ",
               BigDecimal.valueOf( 10.3),
                8,
                "Harper Lee",
                id,
                Set.of("Southern Gothic", "Novel")
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(materialCreateRequestDTO);

        Mockito.when(materialService.createMaterial(materialCreateRequestDTO))
                .thenThrow(new ResourceNotFound(Material.class, id));

        mockMvc.perform(post("/api/materials/create", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));

    }
}
