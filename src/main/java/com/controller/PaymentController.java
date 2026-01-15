package com.controller;

import com.dto.paymentDTO.PaymentRequestDTO;
import com.dto.paymentDTO.PaymentResponseDTO;
import com.mapper.PaymentMapper;
import com.model.Payment;
import com.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/payments")
@Validated
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        Payment payment = paymentService.createPayment(requestDTO);
        return new ResponseEntity<>(paymentMapper.toPaymentResponseDTO(payment), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        List<Payment> allPayments = paymentService.getAllPayments();

        if (allPayments.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return new ResponseEntity<>(paymentMapper.toPaymentResponseDTOList(allPayments), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return new ResponseEntity<>(paymentMapper.toPaymentResponseDTO(payment), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(Long id) {
        paymentService.deletePaymentById(id);
        return ResponseEntity.noContent().build();
    }
}
