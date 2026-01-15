package com.dto.orderDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDTO(
        Long id,
        String buyerName,
        String materialTitle,
        String uniqueNumber,
        Integer quantity,
        BigDecimal price,
        BigDecimal deliveryPrice,
        BigDecimal totalPrice,
        LocalDateTime dateCreated) {
}
