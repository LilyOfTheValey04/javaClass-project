package com.dto.orderDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OrderRequestDTO(@NotNull(message = "Buyer ID is required")
                              Long buyerId,
                              @NotNull(message = "Material ID is required")
                              Long materialId,
                              @Positive(message = "Quantity must be greater than 0")
                              Integer quantity,
                              @Size(max = 255, message = "Delivery address is too long")
                              String deliveryAddress) {
}
