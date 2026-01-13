package com.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)

    @Column(name = "id")
    private Long id;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "buyer_id")
    @NonNull
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "material_id")
    @NonNull
    private Material material;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    @NonNull
    private double price;

    @Column(name = "delivery_price", nullable = false)
    private double deliveryPrice;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @CreationTimestamp
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
}
