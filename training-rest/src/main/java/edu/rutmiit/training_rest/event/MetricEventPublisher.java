package edu.rutmiit.training_rest.event;

import edu.rutmiit.training_checker_api_contract.dto.CardioMetricResponse;
import edu.rutmiit.training_events.EventEnvelope;
import edu.rutmiit.training_events.MetricEvent;
import edu.rutmiit.training_events.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MetricEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MetricEventPublisher.class);
    private static final String SOURCE = "training-rest";

    private final RabbitTemplate rabbitTemplate;

    public MetricEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(CardioMetricResponse metric) {
        var event = new MetricEvent.Created(
                metric.getId(),
                metric.getWorkoutId(),
                metric.getTimestamp(),
                metric.getHeartRate(),
                metric.getDistanceKm(),
                metric.getCalories(),
                metric.getPace()
        );
        send(RoutingKeys.METRIC_CREATED,event);
    }
    private void send(String routingKey, MetricEvent event){
        try {
            EventEnvelope<MetricEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Событие отправлено: {} [eventId={}]", routingKey, envelope.metadata().eventId());
        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}", routingKey, e.getMessage());
        }
    }
}
