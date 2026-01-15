package com.dto.category;

import jakarta.validation.constraints.NotNull;

public record CategoryResponseDTO(@NotNull String name) {
}
