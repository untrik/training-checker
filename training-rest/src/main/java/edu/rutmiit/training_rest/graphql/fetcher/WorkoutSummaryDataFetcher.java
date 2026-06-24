package edu.rutmiit.training_rest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import edu.rutmiit.training_checker_api_contract.dto.CardioMetricResponse;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutSummaryResponse;
import edu.rutmiit.training_rest.service.MetricService;

import java.util.List;

@DgsComponent
public class WorkoutSummaryDataFetcher {
    private final MetricService metricService;


    public WorkoutSummaryDataFetcher(MetricService metricService) {
        this.metricService = metricService;
    }

    @DgsData(parentType = "Workout", field = "workoutSummary")
    public WorkoutSummaryResponse workoutSummary(DgsDataFetchingEnvironment dfe){
        WorkoutResponse workout = dfe.getSource();
        if (workout.getWorkoutSummary() != null) {
            return workout.getWorkoutSummary();
        }
        return null;
    }
}
