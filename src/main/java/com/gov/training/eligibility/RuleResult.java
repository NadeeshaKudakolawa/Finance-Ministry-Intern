package com.gov.training.eligibility;

public record RuleResult(boolean passed, String reason) {

    public static RuleResult pass() {
        return new RuleResult(true, null);
    }

    public static RuleResult fail(String reason) {
        return new RuleResult(false, reason);
    }
}
