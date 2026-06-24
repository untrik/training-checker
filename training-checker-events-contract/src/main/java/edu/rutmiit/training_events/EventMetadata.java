package edu.rutmiit.training_events;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        // Уникальный идентификатор события (UUID v4).
        // Позволяет обнаружить повторную доставку и реализовать idempotent consumer.
        String eventId,

        // Момент создания события в формате ISO-8601 (UTC).
        Instant timestamp,

        // Источник события — имя сервиса, который его породил.
        String source,

        // Тип события: "book.created", "author.deleted" и т.д.
        // Совпадает с routing key в RabbitMQ — удобно для логирования.
        String eventType
) {
    /**
     * Фабричный метод для создания метаданных на стороне publisher'а.
     * Генерирует UUID и ставит текущее время автоматически.
     */
    public static EventMetadata create(String source, String eventType) {
        return new EventMetadata(
                UUID.randomUUID().toString(),
                Instant.now(),
                source,
                eventType
        );
    }
}
