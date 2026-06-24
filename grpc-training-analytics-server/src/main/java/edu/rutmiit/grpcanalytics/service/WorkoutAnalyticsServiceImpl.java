package edu.rutmiit.grpcanalytics.service;

import edu.rutmiit.grpc.AnalyzeWorkoutRequest;
import edu.rutmiit.grpc.WorkoutAnalysisResponse;
import edu.rutmiit.grpc.WorkoutAnalyticsGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkoutAnalyticsServiceImpl extends WorkoutAnalyticsGrpc.WorkoutAnalyticsImplBase {

    private static final Logger log = LoggerFactory.getLogger(WorkoutAnalyticsServiceImpl.class);

    @Override
    public void analyzeWorkout(AnalyzeWorkoutRequest request, StreamObserver<WorkoutAnalysisResponse> responseObserver) {
        log.info("gRPC запрос: анализ тренировки id={} userId={}", request.getWorkoutId(), request.getUserId());
        String intensityLevel = classifyIntensityLevel(request.getAvgHeartRate());
        Double efficiencyScore = calculateEfficiencyScore(request.getTotalDistanceKm(), request.getTotalCalories(), request.getAvgPace());
        String heartRateZone = classifyHeartRateZone(request.getMaxHeartRate());
        String recommendation = recomend(intensityLevel);
        WorkoutAnalysisResponse response = WorkoutAnalysisResponse.newBuilder()
                .setWorkoutId(request.getWorkoutId())
                .setUserId(request.getUserId())
                .setIntensityLevel(intensityLevel)
                .setEfficiencyScore(efficiencyScore)
                .setHeartRateZone(heartRateZone)
                .setRecommendation(recommendation)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private String classifyIntensityLevel(int avgHeatRate) {
        if (avgHeatRate < 110) {
            return "LOW";
        } else if (avgHeatRate < 140) {
            return "MEDIUM";
        } else if (avgHeatRate < 165) {
            return "HIGH";
        } else {
            return "EXTREME";
        }
    }

    private Double calculateEfficiencyScore(double totalDistanceKm, int totalCalories, double avgPace) {
        double score = totalDistanceKm * 10 + (double) totalCalories / 10 - avgPace * 2;
        if (score < 0) return 0.0;
        else if (score > 100) return 100.0;
        else return score;
    }

    private String classifyHeartRateZone(int maxHeartRate) {
        if (maxHeartRate < 110) return "WARM_UP";
        else if (maxHeartRate < 140) return "FAT_BURNING";
        else if (maxHeartRate < 165) return "CARDIO";
        else return "PEAK";
    }

    private String recomend(String intensityLevel) {
        String recomendation = switch (intensityLevel) {
            case "LOW" -> "INCREASE_LOAD";
            case "MEDIUM" -> "NORMAL";
            case "HIGH" -> "REST";
            case "EXTREME" -> "REST_REQUIRED";
            default -> throw new IllegalStateException("Unexpected value: " + intensityLevel);
        };
        return recomendation;
    }
}
