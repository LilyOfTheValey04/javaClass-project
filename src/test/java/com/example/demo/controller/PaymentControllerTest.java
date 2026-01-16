package com.example.demo.controller;

import com.controller.PaymentController;
import com.dto.paymentDTO.PaymentRequestDTO;
import com.dto.paymentDTO.PaymentResponseDTO;
import com.mapper.PaymentMapper;
import com.model.Material;
import com.model.Order;
import com.model.Payment;
import com.model.User;
import com.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private PaymentMapper paymentMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User buyer;
    private Material material;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {

        buyer = User.builder()
                .id(1L)
                .name("Ivan")
                .username("ivan123")
                .build();

        material = Material.builder()
                .id(1L)
                .name("Book Title")
                .price(BigDecimal.valueOf(50))
                .quantity(10)
                .build();

        order = Order.builder()
                .id(1L)
                .buyer(buyer)
                .material(material)
                .price(material.getPrice())
                .deliveryPrice(BigDecimal.valueOf(5))
                .quantity(1)
                .uuid("UUID123")
                .dateCreated(LocalDateTime.now())
                .build();

        payment = Payment.builder()
                .id(1L)
                .order(order)
                .dateCreated(LocalDateTime.now())
                .build();
    }

    @Test
    void createPayment_returnsCreatedPayment() throws Exception {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO(order.getId());

        PaymentResponseDTO responseDTO = new PaymentResponseDTO(
                payment.getId(),
                order.getId(),
                order.getPrice().add(order.getDeliveryPrice()),
                buyer.getName(),
                order.getUuid(),
                payment.getDateCreated()
        );

        Mockito.when(paymentService.createPayment(any(PaymentRequestDTO.class)))
                .thenReturn(payment);
        Mockito.when(paymentMapper.toPaymentResponseDTO(payment))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buyerName").value("Ivan"))
                .andExpect(jsonPath("$.totalPrice").value(55));
    }

    @Test
    void getAllPayments_returnsList() throws Exception {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(
                payment.getId(),
                order.getId(),
                order.getPrice().add(order.getDeliveryPrice()),
                buyer.getName(),
                order.getUuid(),
                payment.getDateCreated()
        );

        Mockito.when(paymentService.getAllPayments()).thenReturn(List.of(payment));
        Mockito.when(paymentMapper.toPaymentResponseDTOList(List.of(payment)))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/payments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buyerName").value("Ivan"))
                .andExpect(jsonPath("$[0].totalPrice").value(55));
    }

    @Test
    void getPaymentById_returnsPayment() throws Exception {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(
                payment.getId(),
                order.getId(),
                order.getPrice().add(order.getDeliveryPrice()),
                buyer.getName(),
                order.getUuid(),
                payment.getDateCreated()
        );

        Mockito.when(paymentService.getPaymentById(payment.getId())).thenReturn(payment);
        Mockito.when(paymentMapper.toPaymentResponseDTO(payment)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/payments/{id}", payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.buyerName").value("Ivan"))
                .andExpect(jsonPath("$.totalPrice").value(55));
    }

    @Test
    void deletePayment_returnsNoContent() throws Exception {
        Long id = 1L;
        Mockito.doNothing().when(paymentService).deletePaymentById(id);

        mockMvc.perform(delete("/api/payments/{id}", id))
                .andExpect(status().isNoContent());
    }
}
