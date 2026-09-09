package com.gov.training.repository;

import com.gov.training.entity.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficerRepository
        extends JpaRepository<Officer, Long> {
}
