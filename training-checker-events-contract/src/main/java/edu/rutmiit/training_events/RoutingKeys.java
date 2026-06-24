package edu.rutmiit.training_events;

public final class RoutingKeys {
    private RoutingKeys() {
    }
    // Имя общего topic exchange для доменных событий
    public static final String EXCHANGE = "trainings.events";
    // Routing keys для событий метрик
    public static final String METRIC_CREATED = "metric.created";

    // Routing keys для событий тренировки
    public static final String WORKOUT_STARTED = "workout.started";
    public static final String WORKOUT_FINISHED = "workout.finished";
    public static final String WORKOUT_DELETED = "workout.deleted";
    public static final String WORKOUT_ENRICHED = "workout.enriched";
    // Паттерны для подписки (wildcard)
    public static final String ALL_WORKOUT_EVENTS = "workout.*";
    public static final String ALL_METRIC_EVENTS = "metric.*";
    public static final String ALL_EVENTS = "#";
}
