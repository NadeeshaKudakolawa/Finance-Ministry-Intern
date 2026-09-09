package com.gov.training.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "nominations",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"training_id", "officer_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Nomination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @ManyToOne
    @JoinColumn(name = "officer_id", nullable = false)
    private Officer officer;

    @ManyToOne
    @JoinColumn(name = "nominated_by_department", nullable = false)
    private Department nominatedByDepartment;

    private LocalDateTime nominationDate;

    // Not marked nullable=false: ddl-auto=update would otherwise try to add
    // a NOT NULL column against existing rows and fail. Non-null is enforced
    // in NominationService instead; existing rows should be backfilled to
    // CONFIRMED via a one-off update statement when this deploys.
    @Enumerated(EnumType.STRING)
    private NominationStatus status;
}
