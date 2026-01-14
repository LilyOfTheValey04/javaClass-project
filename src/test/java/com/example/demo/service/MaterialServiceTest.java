package com.example.demo.service;

import com.dto.material.MaterialCreateRequestDTO;
import com.dto.material.MaterialCreateResponseDTO;
import com.exception.ResourceNotFound;
import com.mapper.MaterialMapper;
import com.model.Category;
import com.model.Material;
import com.model.User;
import com.repository.MaterialRepository;
import com.repository.UserRepository;
import com.service.CategoryService;
import com.service.MaterialService;
import com.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MaterialServiceTest {
    private MaterialRepository materialRepository;
    private MaterialService materialService;
    private MaterialMapper materialMapper;
    private UserService userService;
    private UserRepository userRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        materialRepository = mock(MaterialRepository.class);
        userService = mock(UserService.class);
        materialMapper = mock(MaterialMapper.class);
        userRepository = mock(UserRepository.class);
        categoryService = mock(CategoryService.class);
        materialService = new MaterialService(
                materialRepository, materialMapper, categoryService, userRepository,  userService);
    }

    @Test
    void getMaterialOrThrow_returnsMaterial_whenFound() {

        User user = User.builder().username("ivan123").build();

        Set<Category> categories = Set.of(
                Category.builder().name("Southern Gothic").build(),
                Category.builder().name("Novel").build()
        );

        Material material = Material.builder()
                .name("To Kill a Mockingbird")
                .owner(user)
                .price(10.3)
                .author("Harper Lee")
                .categories(categories)
                .description("A powerful story about racial injustice in the South. ")
                .quantity(8)
                .build();

        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

        // Act
        Material result = materialService.getMaterialById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(material.getId(), result.getId());
        assertEquals(material.getAuthor(), result.getAuthor());
        assertEquals(material.getPrice(), result.getPrice());
        assertEquals(material.getName(), result.getName());
        assertEquals(material.getQuantity(), result.getQuantity());
        assertEquals(material.getOwner(), result.getOwner());

        verify(materialRepository, times(1)).findById(1L);
    }

    @Test
    void getMaterialOrThrow_returnsMaterial_whenNotFound(){
        when(materialRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFound.class,
                () -> materialService.getMaterialById(99L));

        verify(materialRepository, times(1)).findById(99L);
    }

    @Test
    void getMaterialOrThrow_returnsUser_whenFound() {

        User user = User.builder().username("ivan123").build();

        Set<Category> categories = Set.of(
                Category.builder().name("Southern Gothic").build(),
                Category.builder().name("Novel").build()
        );

        Material material = Material.builder()
                .name("To Kill a Mockingbird")
                .owner(user)
                .price(10.3)
                .author("Harper Lee")
                .categories(categories)
                .description("A powerful story about racial injustice in the South. ")
                .quantity(8)
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(materialRepository.findAllByOwnerId(1L)).thenReturn(List.of(material));

        // Act
        List<Material> results = materialService.getMaterialsByUser(1L);

        // Assert
        assertNotNull(results);
        assertEquals(material, results.get(0));


        verify(materialRepository, times(1)).findAllByOwnerId(1L);
    }

    @Test
    void getMaterialOrThrow_returnsUser_whenNotFound(){
        when(materialRepository.findAllByOwnerId(99L)).thenReturn(List.of());
        when(userRepository.existsById(99L)).thenReturn(true);
        // Act
        List<Material> results = materialService.getMaterialsByUser(99L);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty(), "Expected empty list when user has no materials");

        verify(materialRepository, times(1)).findAllByOwnerId(99L);
    }

    @Test
    void createMaterial_returnsMaterial(){
        MaterialCreateRequestDTO materialCreateRequestDTO = new MaterialCreateRequestDTO(
                "To Kill a Mockingbird",
                "A powerful story about racial injustice in the South. ",
                10.3,
                8,
                "Harper Lee",
                1L,
                Set.of("Southern Gothic", "Novel")
        );
        Material material = new Material();
        User owner = new User();
        owner.setId(1L);

        when(materialMapper.toMaterial(materialCreateRequestDTO)).thenReturn(material);
        when(userService.getUserById(1L)).thenReturn(owner);
        when(materialRepository.save(material)).thenReturn(material);

        Material result = materialService.createMaterial(materialCreateRequestDTO);

        assertNotNull(result);
        assertEquals(owner, result.getOwner());
        verify(materialRepository).save(material);
    }

    @Test
    void patchMaterialQuantity_returnsMaterial() {
        Long materialId = 1L;
        Integer newQuantity = 50;

        Material material = new Material();
        material.setId(materialId);
        material.setQuantity(10);

        MaterialService spyService = spy(materialService);
        doReturn(material).when(spyService).getMaterialById(materialId);

        when(materialRepository.save(material)).thenReturn(material);

        Material result = spyService.patchMaterialQuantity(materialId, newQuantity);

        // Assert
        assertNotNull(result);
        assertEquals(newQuantity, result.getQuantity());
        verify(materialRepository).save(material);

    }
}
