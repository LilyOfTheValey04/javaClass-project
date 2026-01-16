package com.controller;

import com.dto.orderDTO.OrderRequestDTO;
import com.dto.orderDTO.OrderResponseDTO;
import com.mapper.OrderMapper;
import com.model.Order;
import com.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/orders")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping()
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO requestDTO) {
        Order order = orderService.createOrder(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderMapper.toOrderResponseDTO(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> allOrders = orderService.getAllOrders()
                .stream()
                .map(orderMapper::toOrderResponseDTO)
                .toList();

        return ResponseEntity.ok(allOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return new ResponseEntity<>(orderMapper.toOrderResponseDTO(order), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}
