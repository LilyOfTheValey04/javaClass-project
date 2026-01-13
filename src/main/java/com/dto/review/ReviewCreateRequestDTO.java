package com.dto.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReviewCreateRequestDTO(@NotNull(message = "Comment cannot me empty") String text,
                                     @NotNull @Positive Integer rating,
                                     Long materialId) {
}
