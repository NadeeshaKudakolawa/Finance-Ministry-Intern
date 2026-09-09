package com.gov.training.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

/**
 * Data-driven eligibility rule: which check ("ruleType") applies to which training
 * programme, and what it's parameterized with. New requirements are added or changed
 * by inserting/editing rows here — no code changes required. The ruleType must match
 * the type() of a registered EligibilityRuleHandler bean.
 */
@Entity
@Table(name = "eligibility_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityRuleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String programmeCode;

    @Column(nullable = false)
    private String ruleType;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT", nullable = false)
    private Map<String, Object> parameters;

    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
