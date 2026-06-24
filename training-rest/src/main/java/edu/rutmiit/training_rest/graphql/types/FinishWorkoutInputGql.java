package edu.rutmiit.training_rest.graphql.types;

import java.time.LocalDateTime;
import java.util.List;

public record FinishWorkoutInputGql(
        List<CreateMetricInputGql> metrics
//        CreateSummaryInputGql summary
) {
}
