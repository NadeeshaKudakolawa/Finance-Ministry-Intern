package com.gov.training.eligibility;

import java.util.List;

public record EligibilityDecision(boolean eligible, List<String> reasons) {

    public static EligibilityDecision allowed() {
        return new EligibilityDecision(true, List.of());
    }

    public static EligibilityDecision denied(List<String> reasons) {
        return new EligibilityDecision(false, reasons);
    }
}
