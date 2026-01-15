package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name="order_id")
    @NonNull
    private Order order;

    @CreationTimestamp
    @Column(name = "date_created")
    private LocalDateTime dateCreated;
}


