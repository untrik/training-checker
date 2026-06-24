package edu.rutmiit.training_rest.controllers;

import edu.rutmiit.training_checker_api_contract.dto.*;
import edu.rutmiit.training_checker_api_contract.endpoints.WorkoutApi;
import edu.rutmiit.training_rest.assemblers.WorkoutModelAssembler;
import edu.rutmiit.training_rest.service.MetricService;
import edu.rutmiit.training_rest.service.WorkoutService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WorkoutController implements WorkoutApi {
    private final MetricService metricService;
    private final WorkoutService workoutService;
    private final WorkoutModelAssembler workoutModelAssembler;

    public WorkoutController(MetricService metricService,
                             WorkoutService workoutService,
                             WorkoutModelAssembler workoutModelAssembler) {
        this.metricService = metricService;
        this.workoutService = workoutService;
        this.workoutModelAssembler = workoutModelAssembler;
    }

    @Override
    public EntityModel<WorkoutResponse> getWorkoutById(Long id) {
        return workoutModelAssembler.toModel(workoutService.findById(id));
    }

    @Override
    public ResponseEntity<EntityModel<WorkoutResponse>> createWorkout(Long id, WorkoutRequest request) {
        WorkoutResponse created = workoutService.startWorkout(id, request);
        EntityModel<WorkoutResponse> model = workoutModelAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public  ResponseEntity<CardioMetricResponse>  createMetric(Long workoutId, CardioMetricRequest request){
      CardioMetricResponse created = metricService.createMetric(workoutId,request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @Override
    public EntityModel<WorkoutResponse> finishWorkout(Long id /*FinishWorkoutRequest request */) {
//        metricService.createMetrics(id, request.metrics());
        List<CardioMetricResponse> metrics = metricService.getAllMetricsByWorkoutId(id);
        metricService.createSummary(id, metrics);
        WorkoutResponse response = workoutService.finishWorkout(id);
        return workoutModelAssembler.toModel(response);
    }

    @Override
    public List<CardioMetricResponse> getMetricsByWorkoutId(Long id) {
        return  metricService.getAllMetricsByWorkoutId(id);
    }

    @Override
    public WorkoutSummaryResponse getSummaryByWorkoutId(Long id) {
        return metricService.getSummaryByWorkoutId(id);
    }


    @Override
    public EntityModel<WorkoutResponse> deleteWorkout(Long id) {
        WorkoutResponse response = workoutService.findById(id);
        workoutService.deleteWorkouts(id);
        return workoutModelAssembler.toModel(response);
    }

}
