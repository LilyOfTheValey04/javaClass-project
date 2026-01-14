package com.repository;

import com.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findAllByOwnerId(Long ownerId);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE Material m
            SET m.quantity = m.quantity - :amount
            WHERE m.id = :id AND m.quantity >= :amount
            """)
    int decreaseStockIfAvailable(
            @Param("id") Long id,
            @Param("amount") int amount
    );

    @Query("""
            SELECT m
            FROM Material m
            JOIN m.categories c
            WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:category IS NULL OR c.name IN :category)
              AND (:minQuantity IS NULL OR m.quantity >= :minQuantity)
            """)
    List<Material> searchMaterials(@Param("name") String name,
                                   @Param("category") Set<String> categories,
                                   @Param("minQuantity") Integer minQuantity);

    @Query("""
            SELECT m
            FROM Material m
            WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))
            ORDER BY m.quantity DESC
            """)
    List<Material> searchMaterialsQuantityFromMaxToMin(@Param("name") String name);

    @Query("""
            SELECT m
            FROM Material m
            WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))
            ORDER BY m.price ASC
            """)
    List<Material> searchMaterialsPriceFromMinToMax(@Param("name") String name);
}
