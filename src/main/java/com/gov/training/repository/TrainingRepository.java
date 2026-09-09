package com.gov.training.repository;

import com.gov.training.entity.Training;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainingRepository
        extends JpaRepository<Training, Long> {

    // Locks the training row for the rest of the transaction so that
    // concurrent nominations/cancellations for the same training are
    // serialized around the capacity check and waiting-list promotion.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Training t where t.id = :id")
    Optional<Training> findByIdForUpdate(@Param("id") Long id);
}
