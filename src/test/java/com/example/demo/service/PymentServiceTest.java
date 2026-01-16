package com.example.demo.service;
import com.dto.paymentDTO.PaymentRequestDTO;
import com.exception.ResourceNotFound;
import com.model.Material;
import com.model.Order;
import com.model.Payment;
import com.model.User;
import com.repository.OrderRepository;
import com.repository.PaymentRepository;
import com.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PymentServiceTest {
    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;
    private PaymentService paymentService;

    private Order order;
    private Payment payment;
    private User buyer;
    private Material material;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        orderRepository = mock(OrderRepository.class);

        paymentService = new PaymentService(orderRepository, paymentRepository);

        material = Material.builder()
                .id(1L)
                .name("Book Title")
                .price(BigDecimal.valueOf(50))
                .quantity(10)
                .build();

        buyer = User.builder()
                .id(1L)
                .name("Ivan")
                .username("ivan123")
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
    void createPayment_createsPaymentSuccessfully() {
        PaymentRequestDTO request = new PaymentRequestDTO(order.getId());

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.createPayment(request);

        assertNotNull(result);
        assertEquals(order, result.getOrder());

        verify(orderRepository, times(1)).findById(order.getId());
        verify(paymentRepository, times(1)).save(result);
    }

    @Test
    void createPayment_throwsException_whenOrderNotFound() {
        PaymentRequestDTO request = new PaymentRequestDTO(99L);

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> paymentService.createPayment(request));
        verify(orderRepository, times(1)).findById(99L);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void getAllPayments_returnsList() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(1, result.size());
        assertEquals(payment, result.get(0));
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void getPaymentById_returnsPayment_whenFound() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById(payment.getId());

        assertNotNull(result);
        assertEquals(payment, result);
        verify(paymentRepository, times(1)).findById(payment.getId());
    }

    @Test
    void getPaymentById_throwsException_whenNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> paymentService.getPaymentById(99L));
        verify(paymentRepository, times(1)).findById(99L);
    }

    @Test
    void deletePaymentById_deletesPaymentSuccessfully() {
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        doNothing().when(paymentRepository).deleteById(payment.getId());

        paymentService.deletePaymentById(payment.getId());

        // Уверяваме се, че order.payment става null
        assertNull(payment.getOrder().getPayment());

        verify(paymentRepository, times(1)).findById(payment.getId());
        verify(paymentRepository, times(1)).flush();
        verify(paymentRepository, times(1)).deleteById(payment.getId());
    }

    @Test
    void deletePaymentById_throwsException_whenNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> paymentService.deletePaymentById(99L));
        verify(paymentRepository, times(1)).findById(99L);
        verify(paymentRepository, never()).deleteById(anyLong());
    }
}
