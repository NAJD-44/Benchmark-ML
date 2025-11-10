package com.example.benchc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 32)
    @Column(unique = true, length = 32, nullable = false)
    private String code;

    @NotBlank
    @Size(max = 128)
    @Column(length = 128, nullable = false)
    private String name;

    // on cache la liste d'items pour éviter la boucle JSON
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Item> items = new ArrayList<>();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
