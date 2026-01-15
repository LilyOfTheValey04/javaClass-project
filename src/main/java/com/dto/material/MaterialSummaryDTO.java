package com.dto.material;

import java.math.BigDecimal;

public record MaterialSummaryDTO(Long id,
                                 String name,
                                 BigDecimal price,
                                 Integer quantity) {
}
