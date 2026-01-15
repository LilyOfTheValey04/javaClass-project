package com.dto.material;

import com.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record MaterialCreateRequestDTO(String name,
                                       String description,
                                       @NotNull @Positive BigDecimal price,
                                       @NotNull @PositiveOrZero Integer quantity,
                                       String author,
                                       @NotNull @Schema(description  = "Owner ID",
                                               example = "1",
                                               required = true)
                                       Long ownerId,
                                       @Schema(example = "[\"Science\", \"Physics\"]")
                                       Set<String> categoryNames) {
}
