package com.mapper;

import com.dto.material.MaterialCreateRequsetDTO;
import com.model.Material;
import org.mapstruct.*;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MaterialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Material toMaterial(MaterialCreateRequsetDTO materialCreateRequsetDTO);
}
