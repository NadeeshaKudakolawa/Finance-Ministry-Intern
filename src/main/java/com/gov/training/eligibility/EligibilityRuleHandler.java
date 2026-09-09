package com.gov.training.eligibility;

import com.gov.training.entity.Officer;
import com.gov.training.entity.Training;

import java.util.Map;

/**
 * One pluggable eligibility check. Implementations are Spring components auto-discovered
 * by EligibilityEvaluator and looked up by type() to match an EligibilityRuleConfig row.
 * Adding a new kind of rule means adding a new implementation of this interface -
 * no existing code has to change.
 */
public interface EligibilityRuleHandler {

    String type();

    RuleResult evaluate(Officer officer, Training training, Map<String, Object> parameters);
}
