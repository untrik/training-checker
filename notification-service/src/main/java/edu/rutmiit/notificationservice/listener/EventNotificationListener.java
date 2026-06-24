package edu.rutmiit.notificationservice.listener;

import edu.rutmiit.notificationservice.websocket.NotificationWebSocketHandler;
import edu.rutmiit.training_events.EventMetadata;
import edu.rutmiit.training_events.MetricEvent;
import edu.rutmiit.training_events.WorkoutEvent;
import org.springframework.amqp.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventNotificationListener {
    private static final Logger log = LoggerFactory.getLogger(EventNotificationListener.class);
    private final NotificationWebSocketHandler webSocketHandler;
    private final JsonMapper jsonMapper;

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public EventNotificationListener(NotificationWebSocketHandler webSocketHandler, JsonMapper jsonMapper) {
        this.webSocketHandler = webSocketHandler;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = "q.notifications.all", messageConverter = "")
    public void handleEvent(Message message) {
        try {
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);
            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);
            // Дедупликация по eventId
            if (!processedEventIds.add(metadata.eventId())) {
                log.warn("Дубликат уведомления пропущен: eventId={}", metadata.eventId());
                return;
            }
            JsonNode payloadNode = root.get("payload");
            String title = buildTitle(metadata.eventType());
            String description = buildDescription(metadata.eventType(), payloadNode);
            String icon = resolveIcon(metadata.eventType());
            String level = resolveLevel(metadata.eventType());
            // JSON для WebSocket-клиента
            String notificationJson = jsonMapper.writeValueAsString(
                    new NotificationPayload(
                            "NOTIFICATION",
                            metadata.eventId(),
                            metadata.eventType(),
                            title,
                            description,
                            icon,
                            level,
                            metadata.source(),
                            metadata.timestamp().toString(),
                            Instant.now().toString()
                    )
            );

            // Broadcast в WebSocket
            webSocketHandler.broadcast(notificationJson);

            log.info("[NOTIFY] {} | {} (клиентов: {})",
                    metadata.eventType(), description, webSocketHandler.getActiveConnectionCount());

        } catch (Exception e) {
            log.error("Ошибка обработки события для уведомлений: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие", e);
        }
    }

    private String buildTitle(String eventType) {
        return switch (eventType) {
            case "metric.created" -> "Новая метрика";
            case "workout.started" -> "Тренировка начата";
            case "workout.finished" -> "Тренировка закончена";
            case "workout.enriched" -> "Аналитика тренировки";
            case "author.created" -> "Новый автор";
            case "author.deleted" -> "Автор удалён";
            default -> "Событие: " + eventType;
        };
    }

    private String buildDescription(String eventType, JsonNode payload) {
        try {
            return switch (eventType) {
                case "metric.created" -> {
                    MetricEvent.Created e = jsonMapper.treeToValue(payload, MetricEvent.Created.class);
                    if (e.heartRate() > 200) {
                        yield String.format("Создана метрика «%d» для тренировки «%d», в %s часов со следующими данными:" +
                                        "\n преодоленная дистанция: %.2f км" +
                                        "\n всего сожжено калорий: %d" +
                                        "\n c темпом: %d" +
                                        "\n c пульсом: %d" +
                                        "\n ПУЛЬС ОЧЕНЬ БОЛЬШОЙ НУЖНО СБАВИТЬ ТЕМП!!!",
                                e.id(), e.workoutId(), e.timestamp(), e.distanceKm(), e.calories(), e.pace(), e.heartRate());
                    } else if (e.heartRate() <= 20) {
                        yield String.format("Создана метрика «%d» для тренировки «%d», в %s часов со следующими данными:" +
                                        "\n преодоленная дистанция: %.2f км" +
                                        "\n всего сожжено калорий: %d" +
                                        "\n c темпом: %d" +
                                        "\n c пульсом: %d" +
                                        "\n ПУЛЬС ОЧЕНЬ НИЗКИЙ ЗВОНЮ В СКОРУЮ!!!",
                                e.id(), e.workoutId(), e.timestamp(), e.distanceKm(), e.calories(), e.pace(), e.heartRate());
                    } else {
                        yield String.format("Создана метрика «%d» для тренировки «%d», в %s часов со следующими данными:" +
                                        "\n преодоленная дистанция: %.2f км" +
                                        "\n всего сожжено калорий: %d" +
                                        "\n c темпом: %d" +
                                        "\n c пульсом: %d",
                                e.id(), e.workoutId(), e.timestamp(), e.distanceKm(), e.calories(), e.pace(), e.heartRate());
                    }
                }
                case "workout.started" -> {
                    WorkoutEvent.Started e = jsonMapper.treeToValue(payload, WorkoutEvent.Started.class);
                    yield String.format("Пользователь «%d» начал тренировку «%d» в %s часов и назвал ее «%s»",
                            e.userId(), e.workoutId(), e.startedAt(), e.title());
                }
                case "workout.finished" -> {
                    WorkoutEvent.Finished e = jsonMapper.treeToValue(payload, WorkoutEvent.Finished.class);
                    yield String.format("Пользователь «%d» закончил тренировку «%d» с названием «%s» в %s часов получены следующие данные:" +
                                    "\n всего метрик: %d" +
                                    "\n длительность тренировки: %d секунд" +
                                    "\n всего преодолено: %.2f км" +
                                    "\n с средним пульсом: %d" +
                                    "\n максимальный пульс: %d" +
                                    "\n всего сожжено калорий: %d " +
                                    "\n с средним темпом: %.2f",
                            e.userId(), e.workoutId(), e.title(), e.finishedAt(), e.MetricsCount(), e.duration(), e.totalDistanceKm(),
                            e.avgHeartRate(), e.maxHeartRate(), e.totalCalories(), e.avgPace());
                }
                case "workout.deleted" -> {
                    WorkoutEvent.Deleted e = jsonMapper.treeToValue(payload, WorkoutEvent.Deleted.class);
                    yield String.format("Пользователь «%d» удалил тренировку «%d» с названием «%s» также с тренировкой удалены %d метрик",
                            e.userId(), e.workoutId(), e.title(), e.deletedMetricsCount());
                }
                case "workout.enriched" -> {
                    WorkoutEvent.Enriched e = jsonMapper.treeToValue(payload, WorkoutEvent.Enriched.class);
                    yield String.format("Тренировка id=%d пользователя userId =%d обогащена (уровень нагрузки: %s, пульсовая зона: %s, оценка эффективности: %.1f, рекомендация: %s)",
                            e.workoutId(), e.userId(), e.intensityLevel(),
                            e.heartRateZone(), e.efficiencyScore(), e.Recommendation());
                }
                default -> "Неизвестное событие: " + eventType;
            };
        } catch (Exception e) {
            return "Событие " + eventType + " (ошибка парсинга)";
        }
    }

    // Иконка по типу события

    private String resolveIcon(String eventType) {
        return switch (eventType) {
            case "metric.created" -> "heart-rate";
            case "workout.started" -> "workout-started";
            case "workout.finished" -> "workout-finished";
            case "workout.deleted" -> "workout-deleted";
            case "workout.enriched" -> "analytics";
            default -> "bell";
        };
    }

    // Уровень уведомления

    private String resolveLevel(String eventType) {
        return switch (eventType) {
            case "workout.deleted" -> "warning";
            case "workout.enriched" -> "info";
            default -> "success";
        };
    }

    /**
     * Payload уведомления для WebSocket.
     */
    record NotificationPayload(
            String type,
            String eventId,
            String eventType,
            String title,
            String description,
            String icon,
            String level,
            String source,
            String eventTimestamp,
            String receivedAt
    ) {
    }

}
