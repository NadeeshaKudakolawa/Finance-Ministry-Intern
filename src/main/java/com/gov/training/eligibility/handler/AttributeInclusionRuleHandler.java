package com.gov.training.eligibility.handler;

import com.gov.training.eligibility.EligibilityRuleHandler;
import com.gov.training.eligibility.RuleResult;
import com.gov.training.entity.Officer;
import com.gov.training.entity.Training;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generic rule: an officer attribute (division, grade, designation) must be one of an
 * allowed list. Covers the Financial Management / Technical programme "must belong to
 * one of these divisions" requirements and the Management Development "particular grade
 * or designation" requirement without needing a dedicated handler per programme.
 *
 * Parameters: {"attribute": "division" | "grade" | "designation", "in": ["FINANCE", "BUDGET", ...]}
 */
@Component
public class AttributeInclusionRuleHandler implements EligibilityRuleHandler {

    @Override
    public String type() {
        return "ATTRIBUTE_INCLUSION";
    }

    @Override
    public RuleResult evaluate(Officer officer, Training training, Map<String, Object> parameters) {
        String attribute = String.valueOf(parameters.get("attribute"));
        Object allowedRaw = parameters.get("in");

        if (!(allowedRaw instanceof List<?> allowedList) || allowedList.isEmpty()) {
            return RuleResult.fail("Misconfigured eligibility rule: 'in' must be a non-empty list.");
        }

        Set<String> allowed = allowedList.stream()
                .map(String::valueOf)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        String actual = resolveAttribute(officer, attribute);

        // The resolved value (department category, in particular) may itself be a
        // comma-separated list of tags, e.g. "Finance,Budget,Planning" - match if
        // any one of the officer's tags is in the allowed set.
        boolean matches = actual != null && Arrays.stream(actual.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toUpperCase)
                .anyMatch(allowed::contains);

        if (!matches) {
            return RuleResult.fail("Officer's " + attribute + " does not meet the requirement for this programme.");
        }

        return RuleResult.pass();
    }

    private String resolveAttribute(Officer officer, String attribute) {
        return switch (attribute) {
            case "division" -> officer.getDepartment() == null ? null
                    : (officer.getDepartment().getCategory() != null
                            ? officer.getDepartment().getCategory()
                            : officer.getDepartment().getName());
            case "grade" -> officer.getGrade();
            case "designation" -> officer.getDesignation();
            default -> null;
        };
    }
}
