package edu.rutmiit.audit_service.listener;

import edu.rutmiit.audit_service.model.AuditEntry;
import edu.rutmiit.audit_service.storage.AuditStorage;
import edu.rutmiit.training_events.EventMetadata;
import edu.rutmiit.training_events.MetricEvent;
import edu.rutmiit.training_events.WorkoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.OffsetDateTime;

@Component
public class AuditEventListener {
    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    private final AuditStorage auditStorage;
    private final JsonMapper jsonMapper;

    public AuditEventListener(AuditStorage auditStorage, JsonMapper jsonMapper) {
        this.auditStorage = auditStorage;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Принимает все события из очереди q.audit.events.
     * <p>
     * Десериализация выполняется в два этапа:
     * 1. Парсим JSON в дерево узлов (JsonNode) — быстро и безопасно.
     * 2. Извлекаем metadata и определяем тип payload по полю eventType.
     * 3. Десериализуем payload в конкретный record по выявленному типу.
     */
    @RabbitListener(queues = "q.audit.events", messageConverter = "")
    public void handleEvent(Message message) {
        try {
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            // Извлекаем метаданные из JSON-конверта
            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            // Дедупликация — если событие уже обработано, пропускаем
            if (auditStorage.isDuplicate(metadata.eventId())) {
                log.warn("Дубликат события пропущен: eventId={}", metadata.eventId());
                return;
            }

            // Определяем тип события и формируем описание
            JsonNode payloadNode = root.get("payload");
            String description = buildDescription(metadata.eventType(), payloadNode);

            AuditEntry entry = auditStorage.save(new AuditEntry(
                    0,
                    metadata.eventId(),
                    metadata.eventType(),
                    metadata.source(),
                    metadata.timestamp(),
                    Instant.now(),
                    description
            ));

            log.info("[AUDIT #{}] {} | {}", entry.sequenceNumber(), metadata.eventType(), description);

        } catch (Exception e) {
            log.error("Ошибка обработки события: {}", e.getMessage(), e);
            // Исключение пробросится, сообщение уйдёт в DLQ после исчерпания retries
            throw new RuntimeException("Не удалось обработать событие", e);
        }
    }

    /**
     * Формирует человекочитаемое описание события для аудит-лога.
     * <p>
     * Десериализует payload в конкретный тип на основе eventType,
     * затем формирует описание через pattern matching по sealed interface.
     */
    private String buildDescription(String eventType, JsonNode payloadNode) throws Exception {
        return switch (eventType) {
            case "metric.created" -> {
                MetricEvent.Created e = jsonMapper.treeToValue(payloadNode, MetricEvent.Created.class);
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
                WorkoutEvent.Started e = jsonMapper.treeToValue(payloadNode, WorkoutEvent.Started.class);
                yield String.format("Пользователь «%d» начал тренировку «%d» в %s часов и назвал ее «%s»",
                        e.userId(), e.workoutId(),e.startedAt(),e.title());
            }
//
            case "workout.finished" -> {
                WorkoutEvent.Finished e = jsonMapper.treeToValue(payloadNode,  WorkoutEvent.Finished.class);
                yield String.format("Пользователь «%d» закончил тренировку «%d» с названием «%s» в %s часов получены следующие данные:" +
                                "\n всего метрик: %d" +
                                "\n длительность тренировки: %d секунд" +
                                "\n всего преодолено: %.2f км" +
                                "\n с средним пульсом: %d" +
                                "\n максимальный пульс: %d" +
                                "\n всего сожжено калорий: %d " +
                                "\n с средним темпом: %.2f",
                        e.userId(), e.workoutId(),e.title(),e.finishedAt(),e.MetricsCount(),e.duration(),e.totalDistanceKm(),
                        e.avgHeartRate(),e.maxHeartRate(),e.totalCalories(),e.avgPace());
            }

            case "workout.deleted" -> {
                WorkoutEvent.Deleted e = jsonMapper.treeToValue(payloadNode, WorkoutEvent.Deleted.class);
                yield String.format("Пользователь «%d» удалил тренировку «%d» с названием «%s» также с тренировкой удалены %d метрик",
                        e.userId(), e.workoutId(),e.title(),e.deletedMetricsCount());
            }
            case "workout.enriched" -> {
                WorkoutEvent.Enriched e = jsonMapper.treeToValue(payloadNode, WorkoutEvent.Enriched.class);
                yield String.format("Тренировка id=%d пользователя userId =%d обогащена (уровень нагрузки: %s, пульсовая зона: %s, оценка эффективности: %.1f, рекомендация: %s)",
                        e.workoutId(), e.userId(), e.intensityLevel(),
                        e.heartRateZone(), e.efficiencyScore(), e.Recommendation());
            }

            default -> "Неизвестное событие: " + eventType;
        };
    }
}
