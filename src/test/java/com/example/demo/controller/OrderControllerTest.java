package com.example.demo.controller;

import com.controller.OrderController;
import com.dto.orderDTO.OrderRequestDTO;
import com.dto.orderDTO.OrderResponseDTO;
import com.exception.ResourceNotFound;
import com.mapper.OrderMapper;
import com.model.Material;
import com.model.Order;
import com.model.User;
import com.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createOrder_returnsCreatedOrder() throws Exception {
        OrderRequestDTO requestDTO = new OrderRequestDTO(
                1L, // buyerId
                1L, // materialId
                2,  // quantity
                "Some Address"
        );

        User buyer = User.builder().id(1L).name("Ivan").build();
        Material material = Material.builder().id(1L).name("Book").build();

        Order order = Order.builder()
                .id(1L)
                .buyer(buyer)
                .material(material)
                .price(BigDecimal.valueOf(50))
                .deliveryPrice(BigDecimal.valueOf(5))
                .quantity(requestDTO.quantity())
                .uuid("UUID123")
                .deliveryAddress(requestDTO.deliveryAddress())
                .dateCreated(LocalDateTime.now())
                .build();

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                1L,
                "Ivan",
                "Book",
                "UUID123",
                2,
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(55), // totalPrice = price + deliveryPrice
                order.getDateCreated()
        );

        Mockito.when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(order);
        Mockito.when(orderMapper.toOrderResponseDTO(order)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.buyerName").value("Ivan"))
                .andExpect(jsonPath("$.totalPrice").value(55));
    }

    @Test
    void getAllOrders_returnsList() throws Exception {
        User buyer = User.builder().id(1L).name("Ivan").build();
        Material material = Material.builder().id(1L).name("Book").build();

        Order order1 = Order.builder().id(1L).buyer(buyer).material(material)
                .price(BigDecimal.valueOf(50)).deliveryPrice(BigDecimal.valueOf(5))
                .quantity(1).uuid("UUID1").dateCreated(LocalDateTime.now()).build();

        Order order2 = Order.builder().id(2L).buyer(buyer).material(material)
                .price(BigDecimal.valueOf(100)).deliveryPrice(BigDecimal.valueOf(10))
                .quantity(2).uuid("UUID2").dateCreated(LocalDateTime.now()).build();

        OrderResponseDTO dto1 = new OrderResponseDTO(
                1L, "Ivan", "Book", "UUID1", 1,
                BigDecimal.valueOf(50), BigDecimal.valueOf(5), BigDecimal.valueOf(55),
                order1.getDateCreated()
        );

        OrderResponseDTO dto2 = new OrderResponseDTO(
                2L, "Ivan", "Book", "UUID2", 2,
                BigDecimal.valueOf(100), BigDecimal.valueOf(10), BigDecimal.valueOf(110),
                order2.getDateCreated()
        );

        Mockito.when(orderService.getAllOrders()).thenReturn(List.of(order1, order2));
        Mockito.when(orderMapper.toOrderResponseDTO(order1)).thenReturn(dto1);
        Mockito.when(orderMapper.toOrderResponseDTO(order2)).thenReturn(dto2);

        mockMvc.perform(get("/api/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].totalPrice").value(55))
                .andExpect(jsonPath("$[1].totalPrice").value(110));
    }

    @Test
    void getOrderById_returnsOrder() throws Exception {
        User buyer = User.builder().id(1L).name("Ivan").build();
        Material material = Material.builder().id(1L).name("Book").build();

        Long id = 1L;
        Order order = Order.builder()
                .id(id)
                .buyer(buyer)
                .material(material)
                .price(BigDecimal.valueOf(50))
                .deliveryPrice(BigDecimal.valueOf(5))
                .quantity(1)
                .uuid("UUID123")
                .dateCreated(LocalDateTime.now())
                .build();

        OrderResponseDTO responseDTO = new OrderResponseDTO(
                id, "Ivan", "Book", "UUID123", 1,
                BigDecimal.valueOf(50), BigDecimal.valueOf(5),
                BigDecimal.valueOf(55),
                order.getDateCreated()
        );

        Mockito.when(orderService.getOrderById(id)).thenReturn(order);
        Mockito.when(orderMapper.toOrderResponseDTO(order)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value(55))
                .andExpect(jsonPath("$.buyerName").value("Ivan"));
    }

    @Test
    void getOrderById_whenNotFound_returns404() throws Exception {
        Long id = 1L;
        Mockito.when(orderService.getOrderById(id))
                .thenThrow(new ResourceNotFound(Order.class, id));

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void deleteOrder_returnsNoContent() throws Exception {
        Long id = 1L;
        Mockito.doNothing().when(orderService).deleteOrderById(id);

        mockMvc.perform(delete("/api/orders/{id}", id))
                .andExpect(status().isNoContent());
    }
}
