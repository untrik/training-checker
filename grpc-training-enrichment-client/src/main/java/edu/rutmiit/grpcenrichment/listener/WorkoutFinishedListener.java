package edu.rutmiit.grpcenrichment.listener;

import edu.rutmiit.grpc.AnalyzeWorkoutRequest;
import edu.rutmiit.grpc.WorkoutAnalysisResponse;
import edu.rutmiit.grpc.WorkoutAnalyticsGrpc;
import edu.rutmiit.grpcenrichment.publisher.EnrichmentEventPublisher;
import edu.rutmiit.training_events.EventMetadata;
import edu.rutmiit.training_events.WorkoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.WatchEvent;

@Component
public class WorkoutFinishedListener {
    private static final Logger log = LoggerFactory.getLogger(WorkoutFinishedListener.class);

    private final WorkoutAnalyticsGrpc.WorkoutAnalyticsBlockingStub analyticsStub;
    private final EnrichmentEventPublisher enrichmentPublisher;
    private final JsonMapper jsonMapper;

    public WorkoutFinishedListener(WorkoutAnalyticsGrpc.WorkoutAnalyticsBlockingStub analyticsStub, EnrichmentEventPublisher enrichmentPublisher, JsonMapper jsonMapper) {
        this.analyticsStub = analyticsStub;
        this.enrichmentPublisher = enrichmentPublisher;
        this.jsonMapper = jsonMapper;
    }
    /**
     * Обрабатывает событие workout.finished:
     * 1. Десериализует событие из JSON
     * 2. Формирует gRPC-запрос
     * 3. Вызывает gRPC-сервер (синхронно)
     * 4. Публикует результат как событие workout.enriched
     */
    @RabbitListener(queues = "q.enrichment.workout.finished", messageConverter = "")
    public void handleWorkoutFinished(Message message) {
        try {
            // 1. Парсим JSON-конверт
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            JsonNode payloadNode = root.get("payload");
            WorkoutEvent.Finished workoutFinished = jsonMapper.treeToValue(payloadNode, WorkoutEvent.Finished.class);

            log.info("Получено событие  workout.finished: workoutId={}, «{}» [eventId={}]",
                    workoutFinished.workoutId(), workoutFinished.title(), metadata.eventId());
            // 2. Формируем gRPC-запрос
            AnalyzeWorkoutRequest grpcRequest = AnalyzeWorkoutRequest.newBuilder()
                    .setWorkoutId(workoutFinished.workoutId())
                    .setUserId(workoutFinished.userId())
                    .setDuration(workoutFinished.duration())
                    .setAvgHeartRate(workoutFinished.avgHeartRate())
                    .setMaxHeartRate(workoutFinished.maxHeartRate())
                    .setTotalCalories(workoutFinished.totalCalories())
                    .setAvgPace(workoutFinished.avgPace())
                    .setTotalDistanceKm(workoutFinished.totalDistanceKm())
                    .build();

            // 3. Вызываем gRPC-сервер (синхронно)
            log.info("Вызов gRPC: WorkoutAnalysis.AnalyzeWorkout(workoutId={})", workoutFinished.workoutId());
            WorkoutAnalysisResponse grpcResponse = analyticsStub.analyzeWorkout(grpcRequest);

            log.info("gRPC ответ получен: workoutId={}, userId={}, уровень нагрузки={}, эффективность={}, пульсовая зона={}, рекомендация={}",
                    grpcResponse.getWorkoutId(),
                    grpcResponse.getUserId(),
                    grpcResponse.getIntensityLevel(),
                    grpcResponse.getEfficiencyScore(),
                    grpcResponse.getHeartRateZone(),
                    grpcResponse.getRecommendation());

            // 4. Публикуем событие workout.enriched
            WorkoutEvent.Enriched enrichedEvent = new WorkoutEvent.Enriched(
                    grpcResponse.getWorkoutId(),
                    grpcResponse.getUserId(),
                    grpcResponse.getIntensityLevel(),
                    grpcResponse.getEfficiencyScore(),
                    grpcResponse.getHeartRateZone(),
                    grpcResponse.getRecommendation()
            );

            enrichmentPublisher.publishEnriched(enrichedEvent);

            log.info("Тренировка обогащена: workoutId={}, «{}» и workout.enriched отправлено",
                    workoutFinished.workoutId(), workoutFinished.title());

        } catch (io.grpc.StatusRuntimeException e) {
            log.error("gRPC ошибка при обогащении тренировки: {} ({})",
                    e.getStatus().getDescription(), e.getStatus().getCode());
            throw new RuntimeException("gRPC-вызов завершился ошибкой", e);

        } catch (Exception e) {
            log.error("Ошибка обработки события workout.finished: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие workout.finished", e);
        }
    }
}
