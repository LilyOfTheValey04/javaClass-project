package com.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_admin")
    private boolean admin;

    private boolean deleted =false;

    @Column(unique = true)
    private String username;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password_hash")
    private String passwordHash;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @JsonBackReference
    @SQLRestriction("deleted = false")
    private Set<Material> materials = new HashSet<>();
}
