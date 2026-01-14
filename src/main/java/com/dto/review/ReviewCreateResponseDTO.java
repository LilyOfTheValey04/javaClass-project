package com.dto.review;

import jakarta.validation.constraints.*;

public record ReviewCreateResponseDTO(@NotNull String text,
                                      @Min(1) @Max(10) Integer rating) {
}
