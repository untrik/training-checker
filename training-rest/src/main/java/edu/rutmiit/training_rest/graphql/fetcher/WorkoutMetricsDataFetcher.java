package edu.rutmiit.training_rest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import edu.rutmiit.training_checker_api_contract.dto.CardioMetricResponse;
import edu.rutmiit.training_checker_api_contract.dto.UserResponse;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;
import edu.rutmiit.training_rest.service.MetricService;
import edu.rutmiit.training_rest.service.UserService;

import java.util.List;

@DgsComponent
public class WorkoutMetricsDataFetcher {
    private final MetricService metricService;


    public WorkoutMetricsDataFetcher(MetricService metricService) {
        this.metricService = metricService;
    }

    @DgsData(parentType = "Workout", field = "metrics")
    public List<CardioMetricResponse> metrics(DgsDataFetchingEnvironment dfe){
        WorkoutResponse workout = dfe.getSource();
        if (workout.getMetrics() != null) {
            return workout.getMetrics();
        }
        return null;
    }
}
