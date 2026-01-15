package com.service;

import com.dto.orderDTO.OrderRequestDTO;
import com.exception.ResourceNotFound;
import com.model.Material;
import com.model.Order;
import com.model.User;
import com.repository.MaterialRepository;
import com.repository.OrderRepository;
import com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final MaterialService materialService;

    @Transactional
    public Order createOrder(OrderRequestDTO request) {

        User buyer = userRepository.findByIdAndDeletedFalse(request.buyerId())
                .orElseThrow(() -> new ResourceNotFound(User.class, request.buyerId()));

        Material material = materialRepository.findActiveById(request.materialId())
                .orElseThrow(() -> new ResourceNotFound(Material.class, request.materialId()));

        BigDecimal totalPrice = material.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

       materialService.reduceQuantity(request.quantity(), request.materialId());

        String orderNumber = generateOrderNumber();
        Order order = Order.builder()
                .uuid(orderNumber)
                .buyer(buyer)
                .material(material)
                .price(totalPrice)
                .quantity(request.quantity())
                .deliveryPrice(BigDecimal.valueOf(5.00))
                .deliveryAddress(request.deliveryAddress())
                .build();

        orderRepository.save(order);
        return order;
    }

    public List<Order> getAllOrders() {

        List<Order> allOrders = orderRepository.findAllByOrderByIdAsc();
        return allOrders;
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id).
                orElseThrow(() -> new ResourceNotFound(Order.class, id));

        return order;
    }

    @Transactional
    public void deleteOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(Order.class, id));

        orderRepository.flush();
        orderRepository.delete(order);
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "ORD-" + date + "-" + random;
    }
}
