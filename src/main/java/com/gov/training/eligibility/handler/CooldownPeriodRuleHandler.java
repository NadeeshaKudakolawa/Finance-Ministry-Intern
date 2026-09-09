package com.gov.training.eligibility.handler;

import com.gov.training.eligibility.EligibilityRuleHandler;
import com.gov.training.eligibility.RuleResult;
import com.gov.training.entity.NominationStatus;
import com.gov.training.entity.Officer;
import com.gov.training.entity.Training;
import com.gov.training.repository.NominationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Rule: an officer who was nominated for the same training programme (any session,
 * matched by programmeCode) within the last N months is not eligible again.
 * Parameters: {"cooldownMonths": 12}
 */
@Component
public class CooldownPeriodRuleHandler implements EligibilityRuleHandler {

    private final NominationRepository nominationRepository;

    public CooldownPeriodRuleHandler(NominationRepository nominationRepository) {
        this.nominationRepository = nominationRepository;
    }

    @Override
    public String type() {
        return "COOLDOWN_PERIOD";
    }

    @Override
    public RuleResult evaluate(Officer officer, Training training, Map<String, Object> parameters) {
        int cooldownMonths = ((Number) parameters.getOrDefault("cooldownMonths", 12)).intValue();
        LocalDateTime cutoff = LocalDateTime.now().minusMonths(cooldownMonths);

        boolean recentlyParticipated = nominationRepository
                .existsByOfficerIdAndTraining_ProgrammeCodeAndNominationDateAfterAndStatusNot(
                        officer.getId(), training.getProgrammeCode(), cutoff, NominationStatus.CANCELLED);

        if (recentlyParticipated) {
            return RuleResult.fail(
                    "Officer already participated in this programme within the last " + cooldownMonths + " months.");
        }

        return RuleResult.pass();
    }
}
