package com.mapper;

import com.dto.material.MaterialCreateRequestDTO;
import com.dto.material.MaterialCreateResponseDTO;
import com.model.Category;
import com.model.Material;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MaterialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Material toMaterial(MaterialCreateRequestDTO materialCreateRequestDTO);

    @Mapping(target = "ownerUsername",
            expression = "java(material.getOwner() != null ? " +
                    "material.getOwner().getUsername() : \"unknown\")")
    @Mapping(target = "categoryNames",
            expression = "java(material.getCategories() != null ? " +
                    "new java.util.HashSet<>(material.getCategories()).stream()" +
                    ".map(c -> c.getName())" +
                    ".collect(java.util.stream.Collectors.toSet()) : java.util.Set.of())")
    MaterialCreateResponseDTO toResponse(Material material);

    @Mapping(target = "owner", ignore = true)
    void updateMaterialFromRequestDTO(
            @MappingTarget Material material, MaterialCreateRequestDTO materialCreateRequestDTO);

}