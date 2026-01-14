package com.startup.seeder;

import com.model.Category;
import com.model.Material;
import com.repository.MaterialRepository;
import com.repository.UserRepository;
import com.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Profile("dev")
@Order(1)
@Component
@RequiredArgsConstructor
public class MaterialSeeder implements CommandLineRunner {
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    @Override
    public void run(String... args) throws Exception {
        Material material1 = Material.builder()
                .name("The Great Gatsby")
                .owner(userRepository.findById(1L).orElseThrow(
                        () -> new RuntimeException("Material Seeding failed! Owner not exist")))
                .price(12.3)
                .author("F. Scott Fitzgerald")
                .categories(categoryService.getCategoriesOrCreate(Set.of("Classic", "Novel")))
                .description("A tale of wealth, love, and the American Dream.")
                .quantity(10)
                .build();

        Material material2 = Material.builder()
                .name("The Great Gatsby")
                .owner(userRepository.findById(1L).orElseThrow(
                        () -> new RuntimeException("Material Seeding failed! Owner not exist")))
                .price(10.3)
                .author("F. Scott Fitzgerald")
                .categories(categoryService.getCategoriesOrCreate(Set.of("Classic")))
                .description("A tale of wealth, love, and the American Dream.")
                .quantity(8)
                .build();

        Material material3 = Material.builder()
                .name("To Kill a Mockingbird")
                .owner(userRepository.findById(1L).orElseThrow(
                        () -> new RuntimeException("Material Seeding failed! Owner not exist")))
                .price(10.3)
                .author("Harper Lee")
                .categories(categoryService.getCategoriesOrCreate(Set.of("Southern Gothic", "Novel")))
                .description("A powerful story about racial injustice in the South. ")
                .quantity(8)
                .build();

        materialRepository.save(material1);
        materialRepository.save(material2);
        materialRepository.save(material3);
    }
}
