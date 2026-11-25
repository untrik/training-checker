package edu.rutmiit.training_rest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.training_checker_api_contract.dto.CardioMetricRequest;
import edu.rutmiit.training_checker_api_contract.dto.CardioMetricResponse;
import edu.rutmiit.training_checker_api_contract.dto.UserRequest;
import edu.rutmiit.training_checker_api_contract.dto.UserResponse;
import edu.rutmiit.training_rest.graphql.types.CreateMetricInputGql;
import edu.rutmiit.training_rest.graphql.types.CreateUserInputGql;
import edu.rutmiit.training_rest.service.MetricService;

import java.util.List;
import java.util.UUID;

@DgsComponent
public class MetricsDataFetcher {
    private final MetricService metricService;

    public MetricsDataFetcher(MetricService metricService) {
        this.metricService = metricService;
    }

    @DgsQuery
    public List<CardioMetricResponse> metrics(@InputArgument String workoutId) {
        return metricService.getAllMetricsByWorkoutId(UUID.fromString(workoutId));
    }

    @DgsMutation
    public CardioMetricResponse createMetric(@InputArgument String workoutId, @InputArgument CreateMetricInputGql input) {
        CardioMetricRequest request = new CardioMetricRequest(
                input.timestamp(),
                input.heartRate(),
                input.distanceKm(),
                input.calories(),
                input.pace()
        );
        return metricService.createMetric(UUID.fromString(workoutId), request);
    }
}
