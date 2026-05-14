package com.catjudge.couplejudge.model;

public class JudgmentSections {
    private final String fact;
    private final String adviceA;
    private final String adviceB;
    private final String improvement;

    public JudgmentSections(String fact, String adviceA, String adviceB, String improvement) {
        this.fact = fact;
        this.adviceA = adviceA;
        this.adviceB = adviceB;
        this.improvement = improvement;
    }

    public String getFact() {
        return fact;
    }

    public String getAdviceA() {
        return adviceA;
    }

    public String getAdviceB() {
        return adviceB;
    }

    public String getImprovement() {
        return improvement;
    }
}
