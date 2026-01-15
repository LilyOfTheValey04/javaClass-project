package com.service;

import com.dto.paymentDTO.PaymentRequestDTO;
import com.exception.ResourceNotFound;
import com.model.Order;
import com.model.Payment;
import com.repository.OrderRepository;
import com.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(PaymentRequestDTO requestDTO) {

        Order order = orderRepository.findById(requestDTO.orderId())
                .orElseThrow(() -> new ResourceNotFound(Order.class, requestDTO.orderId()));

        Payment payment = Payment.builder()
                .order(order)
                .build();

        paymentRepository.save(payment);
        return payment;

    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments;
    }

    public Payment getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(Payment.class, id));

        return payment;
    }

    @Transactional
    public void deletePaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(Payment.class, id));

        if (payment.getOrder() != null) {
            payment.getOrder().setPayment(null);
        }

        paymentRepository.flush();
        paymentRepository.deleteById(id);
    }
}
