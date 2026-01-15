package com.dto.material;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Set;

public record MaterialCreateRequestDTO(String name,
                                       String description,
                                       @NotNull @Positive BigDecimal price,
                                       @NotNull @PositiveOrZero Integer quantity,
                                       Set<String> categoryNames) {
}
