package edu.rutmiit.training_rest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_rest.graphql.types.*;
import edu.rutmiit.training_rest.service.MetricService;
import edu.rutmiit.training_rest.service.WorkoutService;

import java.util.List;

@DgsComponent
public class WorkoutDataFetcher {
    private final WorkoutService workoutService;
    private final MetricService metricService;

    public WorkoutDataFetcher(WorkoutService workoutService, MetricService metricService) {
        this.workoutService = workoutService;
        this.metricService = metricService;
    }

    @DgsQuery
    public WorkoutResponse workout(@InputArgument String id) {
        return workoutService.findById(Long.parseLong(id));
    }

    @DgsQuery
    public WorkoutConnectionGql workouts(@InputArgument String userId,
                                         @InputArgument WorkoutFilterGql filter,
                                         @InputArgument Integer page,
                                         @InputArgument Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        WorkoutType type = filter != null ? filter.type() : null;
        Double totalDistanceKm = filter != null ? filter.totalDistanceKm() : null;
        Integer totalCalories = filter != null ? filter.totalCalories() : null;
        String titleSearch = filter != null ? filter.titleSearch() : null;
        PagedResponse<WorkoutResponse> paged = workoutService.getWorkoutsByUser(Long.parseLong(userId),
                type, totalDistanceKm, totalCalories,
                titleSearch, pageNum, pageSize);
        return new WorkoutConnectionGql(
                paged.content(),
                new PageInfoGql(paged.pageNumber(), paged.pageSize(), paged.totalPages(), paged.last()),
                (int) paged.totalElements());
    }

    @DgsMutation
    public WorkoutResponse startWorkout(@InputArgument String userId,
                                        @InputArgument CreateWorkoutInputGql input) {
        WorkoutRequest request = new WorkoutRequest(
                input.type(),
                input.title()
        );
        return workoutService.startWorkout(Long.parseLong(userId), request);
    }

//    @DgsMutation
//    public WorkoutResponse finishWorkout(@InputArgument String id,
//                                         @InputArgument FinishWorkoutInputGql input) {
//        List<CardioMetricRequest> metricsRequest = input.metrics().stream()
//                .map(m -> new CardioMetricRequest(
//                        m.timestamp(),
//                        m.heartRate(),
//                        m.distanceKm(),
//                        m.calories(),
//                        m.pace()
//                ))
//                .toList();
////        WorkoutSummaryRequest summaryRequest = new WorkoutSummaryRequest(
////                input.summary().duration(),
////                input.summary().totalDistanceKm(),
////                input.summary().avgHeartRate(),
////                input.summary().maxHeartRate(),
////                input.summary().totalCalories(),
////                input.summary().avgPace());
//
//        metricService.createMetrics(Long.parseLong(id), metricsRequest);
//        metricService.createSummary(Long.parseLong(id), metricsRequest);
//        return workoutService.finishWorkout(Long.parseLong(id));
//    }
//}

@DgsMutation
public WorkoutResponse finishWorkout(@InputArgument String id) {

    List<CardioMetricResponse> metrics = metricService.getAllMetricsByWorkoutId(Long.parseLong(id));
    metricService.createSummary(Long.parseLong(id), metrics);
    return workoutService.finishWorkout(Long.parseLong(id));
}
    @DgsMutation
    public Boolean deleteWorkout(@InputArgument String id) {
        workoutService.deleteWorkouts(Long.parseLong(id));
        return true;
    }
}
