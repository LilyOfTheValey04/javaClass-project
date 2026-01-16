package com.example.demo.service;

import com.dto.orderDTO.OrderRequestDTO;
import com.exception.ResourceNotFound;
import com.model.Material;
import com.model.Order;
import com.model.User;
import com.repository.MaterialRepository;
import com.repository.OrderRepository;
import com.repository.UserRepository;
import com.service.MaterialService;
import com.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private MaterialRepository materialRepository;
    private MaterialService materialService;

    private OrderService orderService;
    private User buyer;
    private Material  material;
    private Order order;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);
        materialRepository = mock(MaterialRepository.class);
        materialService = mock(MaterialService.class);

        orderService = new OrderService(orderRepository, userRepository, materialRepository, materialService);

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

    }

    @Test
    void createOrder_createsOrderSuccessfully() {
        OrderRequestDTO request = new OrderRequestDTO(
                1L,
                1L,
                2,
                "123 Main St"
        );

        User user = User.builder().id(1L).build();
        Material material = Material.builder().id(1L).price(BigDecimal.valueOf(10)).build();

        when(userRepository.findByIdAndDeletedFalse(request.buyerId())).thenReturn(Optional.of(user));
        when(materialRepository.findActiveById(request.materialId())).thenReturn(Optional.of(material));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));


         order = orderService.createOrder(request);

        assertNotNull(order);
        assertEquals(user, order.getBuyer());
        assertEquals(material, order.getMaterial());
        assertEquals(BigDecimal.valueOf(20), order.getPrice());
        assertEquals(2, order.getQuantity());
        assertEquals("123 Main St", order.getDeliveryAddress());

        verify(materialService, times(1)).reduceQuantity(request.quantity(), request.materialId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void createOrder_throwsException_whenUserNotFound() {
        OrderRequestDTO request = new OrderRequestDTO(99L, 1L, 1, "Address");
        when(userRepository.findByIdAndDeletedFalse(request.buyerId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> orderService.createOrder(request));
        verify(userRepository, times(1)).findByIdAndDeletedFalse(request.buyerId());
        verifyNoInteractions(materialRepository, orderRepository, materialService);
    }

    @Test
    void createOrder_throwsException_whenMaterialNotFound() {
        OrderRequestDTO request = new OrderRequestDTO(1L, 99L, 1, "Address");
        User user = User.builder().id(1L).build();
        when(userRepository.findByIdAndDeletedFalse(request.buyerId())).thenReturn(Optional.of(user));
        when(materialRepository.findActiveById(request.materialId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> orderService.createOrder(request));
        verify(userRepository, times(1)).findByIdAndDeletedFalse(request.buyerId());
        verify(materialRepository, times(1)).findActiveById(request.materialId());
        verifyNoInteractions(orderRepository, materialService);
    }

    @Test
    void getOrderById_returnsOrder_whenFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(order, result);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_throwsException_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> orderService.getOrderById(99L));
        verify(orderRepository, times(1)).findById(99L);
    }

    @Test
    void getAllOrders_returnsList() {
        Order order1 = Order.builder()
                .id(1L)
                .buyer(buyer)
                .material(material)
                .price(material.getPrice())
                .deliveryPrice(BigDecimal.valueOf(5))
                .quantity(1)
                .uuid("UUID1")
                .dateCreated(LocalDateTime.now())
                .build();

        Order order2 = Order.builder()
                .id(2L)
                .buyer(buyer)
                .material(material)
                .price(material.getPrice())
                .deliveryPrice(BigDecimal.valueOf(5))
                .quantity(2)
                .uuid("UUID2")
                .dateCreated(LocalDateTime.now())
                .build();
        when(orderRepository.findAllByOrderByIdAsc()).thenReturn(List.of(order, order2));

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAllByOrderByIdAsc();
    }

    @Test
    void deleteOrderById_deletesOrderSuccessfully() {
        Material material = Material.builder().id(1L).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrderById(1L);

        verify(materialService, times(1)).increaseBackQuantity(order.getQuantity(), material.getId());
        verify(orderRepository, times(1)).flush();
        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    void deleteOrderById_throwsException_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> orderService.deleteOrderById(99L));
        verify(orderRepository, times(1)).findById(99L);
        verifyNoInteractions(materialService);
    }

}