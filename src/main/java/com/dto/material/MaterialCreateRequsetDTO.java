package com.dto.material;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

public record MaterialCreateRequsetDTO(String name,
                                       String description,
                                       @NotNull @Positive Double price,
                                       @NotNull @PositiveOrZero Integer quantity,
                                       Set<String> categoryNames) {
}
