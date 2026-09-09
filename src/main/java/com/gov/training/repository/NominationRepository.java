package com.gov.training.repository;

import com.gov.training.entity.Nomination;
import com.gov.training.entity.NominationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NominationRepository
        extends JpaRepository<Nomination, Long> {

    boolean existsByTrainingIdAndOfficerId(
            Long trainingId,
            Long officerId
    );

    Optional<Nomination> findByTrainingIdAndOfficerId(
            Long trainingId,
            Long officerId
    );

    long countByTrainingId(Long trainingId);

    long countByTrainingIdAndStatus(
            Long trainingId,
            NominationStatus status
    );

    List<Nomination> findByTrainingIdAndStatusOrderByNominationDateAscIdAsc(
            Long trainingId,
            NominationStatus status
    );

    Optional<Nomination> findFirstByTrainingIdAndStatusOrderByNominationDateAscIdAsc(
            Long trainingId,
            NominationStatus status
    );
}
