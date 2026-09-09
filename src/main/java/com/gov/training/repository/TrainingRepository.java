package com.gov.training.repository;

import com.gov.training.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository
        extends JpaRepository<Training, Long> {
}
