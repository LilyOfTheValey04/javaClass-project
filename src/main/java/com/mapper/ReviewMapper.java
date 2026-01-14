package com.mapper;

import com.dto.review.ReviewCreateRequestDTO;
import com.dto.review.ReviewCreateResponseDTO;
import com.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "material", ignore = true)
    @Mapping(target = "user", ignore = true)
    Review toReview(ReviewCreateRequestDTO reviewCreateRequestDTO);


    ReviewCreateResponseDTO toResponse(Review review);
}
