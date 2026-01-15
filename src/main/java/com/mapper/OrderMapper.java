package com.mapper;

import com.dto.orderDTO.OrderResponseDTO;
import com.model.Order;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "buyerName", source = "buyer.name")
    @Mapping(target = "materialTitle", source = "material.name")
    @Mapping(target = "uniqueNumber", source = "uuid")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "deliveryPrice", source = "deliveryPrice")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "dateCreated", source = "dateCreated")
    @Mapping(
            target = "totalPrice",
            expression = "java(order.getPrice().add(order.getDeliveryPrice()))"
    )
    OrderResponseDTO toOrderResponseDTO(Order order);
}



