package com.dto.paymentDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(Long id,
                                 Long orderId,
                                 BigDecimal totalPrice,
                                 String buyerName,
                                 String uuid,
                                 LocalDateTime dateCreated) {
}
