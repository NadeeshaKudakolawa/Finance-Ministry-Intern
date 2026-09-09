package com.gov.training.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "trainings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate trainingDate;

    private String venue;

    private String trainer;

    @Column(nullable = false)
    private Integer maxParticipants;

    /**
     * Identifies the recurring programme this session belongs to (e.g. "FINANCIAL_MANAGEMENT").
     * Distinct from a single dated session so eligibility rules and the participation
     * cooldown apply across every session of the same programme, not just this instance.
     * Left blank, a training has no eligibility restrictions.
     */
    private String programmeCode;
}
