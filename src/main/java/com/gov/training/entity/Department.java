package com.gov.training.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Broad category this department belongs to (e.g. FINANCE, BUDGET, PLANNING, IT).
     * Used by eligibility rules so several differently-named departments can be
     * grouped under one category without changing rule code.
     */
    private String category;
}
