package com.controller;

import com.dto.material.MaterialCreateRequestDTO;
import com.dto.material.MaterialCreateResponseDTO;
import com.mapper.MaterialMapper;
import com.model.Material;
import com.service.MaterialService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/materials")
@Validated
public class MaterialController {
    private final MaterialService materialService;
    private final MaterialMapper materialMapper;

    @GetMapping
    public List<MaterialCreateResponseDTO> getAll() {
        return materialService.getAllMaterials()
                .stream()
                .map(materialMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialCreateResponseDTO> getById(@PathVariable @Positive Long id) {
        Material material = materialService.getMaterialById(id);

        return new ResponseEntity<>(materialMapper.toResponse(material),HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<MaterialCreateResponseDTO>> getByUser(@PathVariable Long userId) {
        List<MaterialCreateResponseDTO> materials = materialService.
                getMaterialsByUser(userId)
                .stream()
                .map(materialMapper::toResponse)
                .toList();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/search")
    public List<MaterialCreateResponseDTO> searchMaterials(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> categories,
            @RequestParam(required = false) Integer minQuantity) {

        List<Material> materials = materialService.searchMaterials(name, categories, minQuantity);
        return materials.stream()
                .map(materialMapper::toResponse)
                .toList();
    }

    @GetMapping("/search/quantity")
    public List<MaterialCreateResponseDTO> searchMaterialDescQuantity(
            @RequestParam(required = false) String name) {
        List<Material> materials = materialService.
                searchMaterialDescQuantity(name);
        return materials.stream()
                .map(materialMapper::toResponse)
                .toList();
    }

    @GetMapping("/search/price")
    public List<MaterialCreateResponseDTO> searchMaterialsAscPrice(
            @RequestParam(required = false) String name) {
        List<Material> materials = materialService.
                searchMaterialAscPrice(name);
        return materials.stream()
                .map(materialMapper::toResponse)
                .toList();
    }

    @PutMapping("/put/{id}")
    public ResponseEntity<MaterialCreateResponseDTO> putEmployee(
            @PathVariable @Positive Long id,
            @Valid @RequestBody MaterialCreateRequestDTO materialCreateRequestDTO) {
        Material material = materialService.
                putMaterial(id, materialCreateRequestDTO);
        return new ResponseEntity<>(materialMapper.
                toResponse(material), HttpStatus.OK);
    }

    @PatchMapping("/patch/{id}/quantity")
    public ResponseEntity<MaterialCreateResponseDTO> patchMaterialQuantity(
            @PathVariable @Positive Long id,
            @RequestParam @PositiveOrZero Integer newQuantity) {
        Material material = materialService.patchMaterialQuantity(id, newQuantity);
        return new ResponseEntity<>(materialMapper.toResponse(material), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<MaterialCreateResponseDTO> createMaterial(
            @Valid @RequestBody MaterialCreateRequestDTO materialCreateRequestDTO) {
        Material material = materialService.createMaterial(materialCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(materialMapper.toResponse(material));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable @Positive Long id) {
        materialService.deleteMaterial(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
