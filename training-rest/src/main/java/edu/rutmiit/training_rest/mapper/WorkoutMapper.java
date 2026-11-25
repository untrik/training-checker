package edu.rutmiit.training_rest.mapper;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;
import edu.rutmiit.training_rest.domain.CardioMetricEntity;
import edu.rutmiit.training_rest.domain.WorkoutEntity;
import edu.rutmiit.training_rest.domain.WorkoutSummaryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkoutMapper {

    private final UserMapper userMapper;
    private final CardioMetricMapper metricMapper;
    private final WorkoutSummaryMapper summaryMapper;

    public WorkoutMapper(UserMapper userMapper, CardioMetricMapper metricMapper, WorkoutSummaryMapper summaryMapper) {
        this.userMapper = userMapper;
        this.metricMapper = metricMapper;
        this.summaryMapper = summaryMapper;
    }

    public WorkoutResponse toResponse(WorkoutEntity workout, List<CardioMetricEntity> metrics, WorkoutSummaryEntity summary) {
        return WorkoutResponse.builder()
                .id(workout.getId())
                .user(userMapper.toResponse(workout.getUser()))
                .type(workout.getType())
                .title(workout.getTitle())
                .startedAt(workout.getStartedAt())
                .finishedAt(workout.getFinishedAt())
                .metrics(metrics.stream()
                        .map(metricMapper::toResponse)
                        .toList())
                .workoutSummary(summary != null ? summaryMapper.toResponse(summary) : null)
                .build();
    }
}