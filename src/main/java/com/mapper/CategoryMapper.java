package com.mapper;

import com.dto.category.CategoryResponseDTO;
import com.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryResponseDTO toResponse(Category category);
}
