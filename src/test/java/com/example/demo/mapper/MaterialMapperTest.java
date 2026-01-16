package com.example.demo.mapper;

import com.dto.material.MaterialCreateRequestDTO;
import com.dto.material.MaterialCreateResponseDTO;
import com.mapper.MaterialMapper;
import com.model.Category;
import com.model.Material;
import com.model.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class MaterialMapperTest {
    private final MaterialMapper underTest = Mappers.getMapper(MaterialMapper.class);
    private static Stream<Arguments> materialProvider() {
        Category cat1 = new Category();
        Category cat2 = new Category();
        cat1.setId(1L);
        cat2.setId(2L);
        cat1.setName("cat1");
        cat2.setName("cat2");

        User john = new User();
        john.setId(1L);
        john.setUsername("john");

        return Stream.of(
                Arguments.of(
                        Material.builder()
                                .id(1L)
                                .name("name1")
                                .owner(john)
                                .categories(Set.of(cat2))
                                .build(),
                        "john",
                        Set.of("cat2")
                ),
                Arguments.of(
                        Material.builder()
                                .id(2L)
                                .name("name2")
                                .owner(null)
                                .categories(null)
                                .build(),
                        "unknown",
                        Set.of()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("materialProvider")
    void toResponseTest(Material material, String expectedOwner, Set<String> expectedCategories) {
        MaterialCreateResponseDTO result = underTest.toResponse(material);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(material.getName());
        assertThat(result.description()).isEqualTo(material.getDescription());
        assertThat(result.author()).isEqualTo(material.getAuthor());
        assertThat(result.price()).isEqualTo(material.getPrice());
        assertThat(result.quantity()).isEqualTo(material.getQuantity());
        assertThat(result.ownerUsername()).isEqualTo(expectedOwner);
        assertThat(result.categoryNames()).isEqualTo(expectedCategories);
    }

    @ParameterizedTest
    @MethodSource("materialProvider")
    void updateMaterialFromRequestDTOTest(Material existingMaterial, String expectedOwner, Set<String> expectedCategories) {
        MaterialCreateRequestDTO requestDTO = new MaterialCreateRequestDTO(
                "To Kill a Mockingbird",
                "A powerful story about racial injustice in the South. ",
                BigDecimal.valueOf(10.3),
                8,
                "Harper Lee",
                1L,
                Set.of("Southern Gothic", "Novel"));

        underTest.updateMaterialFromRequestDTO(existingMaterial, requestDTO);

        assertThat(existingMaterial.getName()).isEqualTo("To Kill a Mockingbird");
        assertThat(existingMaterial.getDescription()).isEqualTo("A powerful story about racial injustice in the South. ");
        assertThat(existingMaterial.getAuthor()).isEqualTo("Harper Lee");
        assertThat(existingMaterial.getPrice()).isEqualTo(10.3);
        assertThat(existingMaterial.getQuantity()).isEqualTo(8);
    }

}
