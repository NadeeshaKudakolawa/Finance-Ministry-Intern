package com.gov.training.eligibility.handler;

import com.gov.training.eligibility.EligibilityRuleHandler;
import com.gov.training.eligibility.RuleResult;
import com.gov.training.entity.Officer;
import com.gov.training.entity.Training;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Rule: officer must have at least N years of service.
 * Parameters: {"minYears": 5}
 */
@Component
public class MinimumServiceYearsRuleHandler implements EligibilityRuleHandler {

    @Override
    public String type() {
        return "MINIMUM_SERVICE_YEARS";
    }

    @Override
    public RuleResult evaluate(Officer officer, Training training, Map<String, Object> parameters) {
        int minYears = ((Number) parameters.getOrDefault("minYears", 0)).intValue();

        LocalDate serviceStartDate = officer.getServiceStartDate();
        if (serviceStartDate == null) {
            return RuleResult.fail("Officer's service start date is not recorded.");
        }

        long years = ChronoUnit.YEARS.between(serviceStartDate, LocalDate.now());
        if (years < minYears) {
            return RuleResult.fail(
                    "Officer has " + years + " year(s) of service; this programme requires at least " + minYears + ".");
        }

        return RuleResult.pass();
    }
}
