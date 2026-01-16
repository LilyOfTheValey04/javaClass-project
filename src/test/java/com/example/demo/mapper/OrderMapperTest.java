package com.example.demo.mapper;

import com.dto.orderDTO.OrderResponseDTO;
import com.mapper.OrderMapper;
import com.model.Material;
import com.model.Order;
import com.model.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderMapperTest {
    private final OrderMapper underTest = Mappers.getMapper(OrderMapper.class);

    private static Stream<Arguments> orderProvider() {
        User buyer = User.builder()
                .id(1L)
                .name("John Doe")
                .build();

        Material material = Material.builder()
                .id(1L)
                .name("Laptop")
                .build();

        LocalDateTime now = LocalDateTime.now();

        return Stream.of(
                Arguments.of(
                        Order.builder()
                                .id(1L)
                                .buyer(buyer)
                                .material(material)
                                .uuid("ORD-123")
                                .quantity(2)
                                .price(new BigDecimal("1000.00"))
                                .deliveryPrice(new BigDecimal("50.00"))
                                .dateCreated(now)
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("orderProvider")
    void toOrderResponseDTOTest(Order order) {
        OrderResponseDTO result = underTest.toOrderResponseDTO(order);

        assertThat(result).isNotNull();

        assertThat(result.id()).isEqualTo(order.getId());
        assertThat(result.uniqueNumber()).isEqualTo(order.getUuid());
        assertThat(result.quantity()).isEqualTo(order.getQuantity());
        assertThat(result.dateCreated()).isEqualTo(order.getDateCreated());

        assertThat(result.buyerName()).isEqualTo(order.getBuyer().getName());
        assertThat(result.materialTitle()).isEqualTo(order.getMaterial().getName());


        assertThat(result.price()).isEqualTo(order.getPrice());
        assertThat(result.deliveryPrice()).isEqualTo(order.getDeliveryPrice());

        assertThat(result.totalPrice())
                .isEqualByComparingTo(
                        order.getPrice().add(order.getDeliveryPrice())
                );
    }
}
