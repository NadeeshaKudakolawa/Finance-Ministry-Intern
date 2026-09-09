package com.gov.training.repository;

import com.gov.training.entity.Nomination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NominationRepository
        extends JpaRepository<Nomination, Long> {

    boolean existsByTrainingIdAndOfficerId(
            Long trainingId,
            Long officerId
    );

    long countByTrainingId(Long trainingId);
}
