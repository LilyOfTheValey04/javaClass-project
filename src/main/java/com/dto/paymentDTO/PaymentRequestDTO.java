package com.dto.paymentDTO;

import jakarta.validation.constraints.NotNull;

public record PaymentRequestDTO(
        @NotNull(message = "Order ID is required")
        Long orderId) {
}
