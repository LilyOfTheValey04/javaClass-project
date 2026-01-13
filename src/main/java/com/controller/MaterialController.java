package com.controller;

import com.mapper.MaterialMapper;
import com.model.Material;
import com.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/materials")
@Validated
public class MaterialController {
    private final MaterialService materialService;
    private final MaterialMapper materialMapper;

    @GetMapping
    public List<Material> getAll() {
        return materialService.getAllMaterials();
    }
}
