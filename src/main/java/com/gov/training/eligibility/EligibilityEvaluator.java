package com.gov.training.eligibility;

import com.gov.training.entity.EligibilityRuleConfig;
import com.gov.training.entity.Officer;
import com.gov.training.entity.Training;
import com.gov.training.repository.EligibilityRuleConfigRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Evaluates every active EligibilityRuleConfig row for a training's programme against
 * an officer, dispatching each row to the matching EligibilityRuleHandler. Adding or
 * changing eligibility requirements is a data change (insert/update a rule row) - this
 * class and the handlers never need to change for that.
 */
@Service
public class EligibilityEvaluator {

    private final Map<String, EligibilityRuleHandler> handlersByType;
    private final EligibilityRuleConfigRepository ruleConfigRepository;

    public EligibilityEvaluator(
            List<EligibilityRuleHandler> handlers,
            EligibilityRuleConfigRepository ruleConfigRepository
    ) {
        this.handlersByType = handlers.stream()
                .collect(Collectors.toMap(EligibilityRuleHandler::type, Function.identity()));
        this.ruleConfigRepository = ruleConfigRepository;
    }

    public EligibilityDecision evaluate(Officer officer, Training training) {
        String programmeCode = training.getProgrammeCode();
        if (programmeCode == null || programmeCode.isBlank()) {
            return EligibilityDecision.allowed();
        }

        List<EligibilityRuleConfig> configs = ruleConfigRepository.findByProgrammeCodeAndActiveTrue(programmeCode);
        List<String> failures = new ArrayList<>();

        for (EligibilityRuleConfig config : configs) {
            EligibilityRuleHandler handler = handlersByType.get(config.getRuleType());
            if (handler == null) {
                failures.add("Unknown eligibility rule type configured: " + config.getRuleType());
                continue;
            }
            RuleResult result = handler.evaluate(officer, training, config.getParameters());
            if (!result.passed()) {
                failures.add(result.reason());
            }
        }

        return failures.isEmpty() ? EligibilityDecision.allowed() : EligibilityDecision.denied(failures);
    }
}
