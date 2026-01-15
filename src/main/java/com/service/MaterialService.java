package com.service;

import com.dto.material.MaterialCreateRequestDTO;
import com.dto.material.MaterialCreateResponseDTO;
import com.exception.InsufficientQuantityException;
import com.exception.ResourceNotFound;
import com.mapper.MaterialMapper;
import com.model.Category;
import com.model.Material;
import com.model.User;
import com.repository.MaterialRepository;
import com.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final UserService userService;

    public Material getMaterialById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(Material.class, id));
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public List<Material> getMaterialsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFound(User.class, userId);
        }
        return materialRepository.findAllByOwnerId(userId);
    }

    public List<Material> searchMaterials(String name, Set<String> categories, Integer minQuantity) {
        return materialRepository.searchMaterials(name, categories, minQuantity);
    }

    public List<Material> searchMaterialDescQuantity(String name) {
        return materialRepository.searchMaterialsQuantityFromMaxToMin(name);
    }

    public List<Material> searchMaterialAscPrice(String name) {
        return materialRepository.searchMaterialsPriceFromMinToMax(name);
    }

    @Transactional
    public Material putMaterial(Long id, MaterialCreateRequestDTO materialCreateRequestDTO) {
        Material material = getMaterialById(id);

        materialMapper.updateMaterialFromRequestDTO(material, materialCreateRequestDTO);

        setMaterialCategories(material, materialCreateRequestDTO.categoryNames());

        if (materialCreateRequestDTO.ownerId() != null) {
            User owner = userService.getUserById(1L);
            material.setOwner(owner);
        }

        return materialRepository.save(material);
    }

    @Transactional
    public Material patchMaterialQuantity(Long id, Integer newQuantity) {
        Material material = getMaterialById(id);
        material.setQuantity(newQuantity);
        return materialRepository.save(material);
    }

    @Transactional
    public Material createMaterial(MaterialCreateRequestDTO materialCreateRequestDTO) {
        Material material = materialMapper.toMaterial(materialCreateRequestDTO);
        setMaterialCategories(material, materialCreateRequestDTO.categoryNames());
        User owner = userService.getUserById(materialCreateRequestDTO.ownerId());
        material.setOwner(owner);

        return materialRepository.save(material);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        if (materialRepository.existsById(id))
            materialRepository.deleteById(id);
        else
            throw new ResourceNotFound(Material.class, id);

    }

    public void reduceQuantity(Integer quantity,
                                Long materialId) {
        int updatedRows = materialRepository.decreaseStockIfAvailable(materialId, quantity);

        if (updatedRows == 0) {
            throw new InsufficientQuantityException(Material.class, quantity);
        }
    }

    private void setMaterialCategories(Material material, Set<String> categoryNames) {
        if (categoryNames != null && !categoryNames.isEmpty()) {
            Set<Category> categories = categoryService.getCategoriesOrCreate(categoryNames);
            material.setCategories(categories);
        }
    }
}
