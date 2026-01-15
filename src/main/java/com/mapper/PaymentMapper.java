package com.mapper;

import com.dto.paymentDTO.PaymentResponseDTO;
import com.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "uuid", source = "order.uuid")
    @Mapping(target = "buyerName", source = "order.buyer.name")
    @Mapping(
            target = "totalPrice",
            expression = "java(payment.getOrder().getPrice().add(payment.getOrder().getDeliveryPrice()))"
    )
    PaymentResponseDTO toPaymentResponseDTO(Payment payment);

    List<PaymentResponseDTO> toPaymentResponseDTOList(List<Payment> payments);
}
