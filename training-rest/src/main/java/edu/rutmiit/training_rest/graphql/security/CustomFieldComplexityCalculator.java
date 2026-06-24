package edu.rutmiit.training_rest.graphql.security;

import graphql.analysis.FieldComplexityCalculator;
import graphql.analysis.FieldComplexityEnvironment;

public class CustomFieldComplexityCalculator implements FieldComplexityCalculator {
    public CustomFieldComplexityCalculator() {
    }

    @Override
    public int calculate(FieldComplexityEnvironment environment, int childComplexity) {
        return switch (environment.getField().getName()) {
            case "content" -> 300 + childComplexity;
            case "workouts" -> 100 + childComplexity;
            case "metrics" -> 50 + childComplexity;
            case "summary" -> 50 + childComplexity;
            case "user" -> 100 + childComplexity;
            default -> 1 + childComplexity;
        };
    }
}
