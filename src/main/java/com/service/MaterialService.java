package com.service;

import com.exception.ResourceNotFound;
import com.dto.material.MaterialCreateRequsetDTO;
import com.mapper.MaterialMapper;
import com.model.Category;
import com.model.Material;
import com.model.Review;
import com.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialService {
    private  final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;
    private final CategoryService categoryService;

    public Material getMaterialById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(Material.class, id));
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @Transactional
    public Material createMaterial(MaterialCreateRequsetDTO materialCreateRequestDTO) {
        Material material = materialMapper.toMaterial(materialCreateRequestDTO);
        Set<String> categoryNames = materialCreateRequestDTO.categoryNames();

        if (categoryNames != null && !categoryNames.isEmpty()) {
            Set<Category> categories = categoryNames.stream()
                    .map(categoryService::getCategoryOrCreate)
                    .collect(Collectors.toSet());

            material.setCategories(categories);
        }

        return materialRepository.save(material);
    }
}
