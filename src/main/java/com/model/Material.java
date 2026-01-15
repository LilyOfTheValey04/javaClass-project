package com.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean deleted = false;

    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonManagedReference
    private User owner;

    @Column(length = 2000)
    private String description;

    private String author;

    private BigDecimal price;

    private Integer quantity;

    @OneToMany
    @JoinColumn(name = "review_id")
    @JsonManagedReference
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "material_category",
            joinColumns = @JoinColumn(name = "material_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}
