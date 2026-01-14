package com.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequestDTO(@NotNull(message = "Comment cannot me empty") String text,
                                     @Min(1) @Max(10) Integer rating,
                                     Long materialId) {
}
