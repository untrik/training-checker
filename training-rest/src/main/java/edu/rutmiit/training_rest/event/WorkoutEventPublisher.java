package edu.rutmiit.training_rest.event;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;
import edu.rutmiit.training_events.EventEnvelope;
import edu.rutmiit.training_events.EventMetadata;
import edu.rutmiit.training_events.RoutingKeys;
import edu.rutmiit.training_events.WorkoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.WatchEvent;

@Component
public class WorkoutEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(WorkoutEventPublisher.class);

    private static final String SOURCE = "training-rest";

    private final RabbitTemplate rabbitTemplate;

    public WorkoutEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishStarted(WorkoutResponse workout) {
        var event = new WorkoutEvent.Started(
                workout.getTitle(),
                workout.getUser().getId(),
                workout.getId(),
                workout.getStartedAt()
        );
        send(RoutingKeys.WORKOUT_STARTED, event);
    }

    public void publishFinished(WorkoutResponse workout) {
        var event = new WorkoutEvent.Finished(
                workout.getTitle(),
                workout.getUser().getId(),
                workout.getId(),
                workout.getFinishedAt(),
                workout.getMetrics().size(),
                workout.getWorkoutSummary().getDuration(),
                workout.getWorkoutSummary().getTotalDistanceKm(),
                workout.getWorkoutSummary().getAvgHeartRate(),
                workout.getWorkoutSummary().getMaxHeartRate(),
                workout.getWorkoutSummary().getTotalCalories(),
                workout.getWorkoutSummary().getAvgPace()
        );
        send(RoutingKeys.WORKOUT_FINISHED, event);
    }

    public void publishDeleted(Long userId, Long workoutId, String title, int deletedMetricsCount) {
        var event = new WorkoutEvent.Deleted(
                userId,
                workoutId,
                title,
                deletedMetricsCount
        );
        send(RoutingKeys.WORKOUT_DELETED, event);
    }

    /**
     * Отправляет событие в RabbitMQ, обёрнутое в EventEnvelope.
     * <p>
     * convertAndSend:
     * - 1й аргумент: имя exchange
     * - 2й аргумент: routing key (определяет, в какие очереди попадёт сообщение)
     * - 3й аргумент: объект, который Jackson сериализует в JSON
     * <p>
     * try-catch гарантирует, что ошибка публикации не сломает основной бизнес-поток.
     */

    private void send(String routingKey, WorkoutEvent event) {
        try {
            EventEnvelope<WorkoutEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Событие отправлено: {} [eventId={}]", routingKey, envelope.metadata().eventId());
        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}", routingKey, e.getMessage());
        }
    }
}
