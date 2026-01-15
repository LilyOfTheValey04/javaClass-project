package com.example.demo.repository;

import com.model.Material;
import com.repository.MaterialRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Sql(scripts = {"/sql/owner_data.sql","/sql/material_data.sql","/sql/category_data.sql"})
public class MaterialRepositoryTest {
    @Autowired
    private MaterialRepository materialRepository;

    @Test
    void searchMaterialsPriceFromMinToMax_returnsOrderedMaterial() {
        // Given
        String name = "g";

        // Act
        List<Material> result = materialRepository.searchMaterialsPriceFromMinToMax(name);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(m -> m.getName().toLowerCase()
                .contains(name.toLowerCase())));

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getPrice() <= result.get(i + 1).getPrice());
        }
    }

    @Test
    void findAllByOwnerId_returnsMaterialsForOwner() {
        List<Material> materials = materialRepository.findAllByOwnerId(1L);
        assertThat(materials).isNotEmpty();
        assertThat(materials).allMatch(m -> m.getOwner().getId().equals(1L));
    }

    @Test
    void decreaseStockIfAvailable_reducesQuantity_whenEnoughStock() {
        Material material = materialRepository.findAll().get(0);
        int oldQuantity = material.getQuantity();


        int updatedRows = materialRepository.decreaseStockIfAvailable(material.getId(), 2);
        materialRepository.flush();

        assertThat(updatedRows).isEqualTo(1);
        Material updated = materialRepository.findById(material.getId()).orElseThrow();
        assertThat(updated.getQuantity()).isEqualTo(oldQuantity - 2);
    }

    @Test
    void decreaseStockIfAvailable_doesNothing_whenNotEnoughStock() {
        Material material = materialRepository.findAll().get(0);
        int oldQuantity = material.getQuantity();

        int updatedRows = materialRepository.decreaseStockIfAvailable(material.getId(), oldQuantity + 1);

        assertThat(updatedRows).isEqualTo(0);
        Material unchanged = materialRepository.findById(material.getId()).orElseThrow();
        assertThat(unchanged.getQuantity()).isEqualTo(oldQuantity);
    }

    @Test
    void searchMaterials_filtersByNameCategoryAndQuantity() {
        Set<String> categories = Set.of("Books");
        List<Material> results = materialRepository
                .searchMaterials("gatsby", categories, 5);

        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(m -> m.getName().toLowerCase().contains("gatsby"));
        assertThat(results).allMatch(m -> m.getQuantity() >= 5);
        assertThat(results).allMatch(m -> m.getCategories().stream()
                .anyMatch(c -> categories.contains(c.getName())));
    }

    @Test
    void searchMaterials_returnsEmpty_whenNoMatch() {
        List<Material> results = materialRepository.searchMaterials("nonexistent", null, null);
        assertThat(results).isEmpty();
    }

    @Test
    void searchMaterialsQuantityFromMaxToMin_ordersCorrectly() {
        List<Material> results = materialRepository.searchMaterialsQuantityFromMaxToMin(null);
        assertThat(results).isSortedAccordingTo((m1, m2) -> m2.getQuantity() - m1.getQuantity());
    }

    @Test
    void searchMaterialsPriceFromMinToMax_ordersCorrectly() {
        List<Material> results = materialRepository.searchMaterialsPriceFromMinToMax(null);
        assertThat(results).isSortedAccordingTo((m1, m2) -> Double.compare(m1.getPrice(), m2.getPrice()));
    }

}
