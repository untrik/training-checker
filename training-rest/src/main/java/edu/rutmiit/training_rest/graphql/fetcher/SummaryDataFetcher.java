package edu.rutmiit.training_rest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutSummaryResponse;
import edu.rutmiit.training_rest.service.MetricService;

import java.util.UUID;

@DgsComponent
public class SummaryDataFetcher {
    private final MetricService metricService;

    public SummaryDataFetcher(MetricService metricService) {
        this.metricService = metricService;
    }

    @DgsQuery
    public WorkoutSummaryResponse workoutSummary(@InputArgument String workoutId) {
        return metricService.getSummaryByWorkoutId(UUID.fromString(workoutId));
    }
}
