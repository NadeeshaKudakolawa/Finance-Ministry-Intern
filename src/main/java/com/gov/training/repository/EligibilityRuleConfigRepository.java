package com.gov.training.repository;

import com.gov.training.entity.EligibilityRuleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EligibilityRuleConfigRepository
        extends JpaRepository<EligibilityRuleConfig, Long> {

    List<EligibilityRuleConfig> findByProgrammeCodeAndActiveTrue(String programmeCode);
}
