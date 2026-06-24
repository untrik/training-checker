package edu.rutmiit.grpcenrichment.publisher;

import edu.rutmiit.training_events.EventEnvelope;
import edu.rutmiit.training_events.RoutingKeys;
import edu.rutmiit.training_events.WorkoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Публикация событий обогащения (workout.enriched) в RabbitMQ.
 * <p>
 * Паттерн fire-and-forget: если RabbitMQ недоступен, ошибка логируется,
 * но gRPC-вызов уже выполнен — результат не теряется полностью.
 */
@Component
public class EnrichmentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(EnrichmentEventPublisher.class);
    private static final String SOURCE = "grpc-enrichment-client";

    private final RabbitTemplate rabbitTemplate;

    public EnrichmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEnriched(WorkoutEvent.Enriched enrichedEvent) {
        try {
            EventEnvelope<WorkoutEvent> envelope = EventEnvelope.wrap(enrichedEvent, SOURCE, RoutingKeys.WORKOUT_ENRICHED);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.WORKOUT_ENRICHED, envelope);
            log.info("Событие отправлено: {} [workoutId={}, eventId={}]",
                    RoutingKeys.WORKOUT_ENRICHED,
                    enrichedEvent.workoutId(),
                    envelope.metadata().eventId());

        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}",
                    RoutingKeys.WORKOUT_ENRICHED, e.getMessage());
        }
    }
}
