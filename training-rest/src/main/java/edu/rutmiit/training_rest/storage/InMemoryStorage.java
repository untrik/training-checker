package edu.rutmiit.training_rest.storage;

import edu.rutmiit.training_checker_api_contract.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    public final Map<Long, UserResponse> users = new ConcurrentHashMap<>();
    public final Map<Long, WorkoutResponse> workouts = new ConcurrentHashMap<>();
    public final Map<Long, CardioMetricResponse> metrics = new ConcurrentHashMap<>();
    public final Map<Long, WorkoutSummaryResponse> workoutSummaries = new ConcurrentHashMap<>();

    public final AtomicLong metricSequence = new AtomicLong(0);
    public final AtomicLong userSequence = new AtomicLong(0);
    public final AtomicLong workoutSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {
        // Создаем айди тренировок
        long workoutId1 = workoutSequence.incrementAndGet();
        long workoutId2 = workoutSequence.incrementAndGet();
        long workoutId3 = workoutSequence.incrementAndGet();

        // Создаем пользователей с полным набором полей (демонстрация Lombok @Builder)
        UserResponse user1 = UserResponse.builder()
                .id(userSequence.incrementAndGet())
                .username("untrick")
                .email("zkirpa2005@mail.ru")
                .birthDate(LocalDate.of(2005, 8, 9))
                .weightKg(63)
                .build();

        UserResponse user2 = UserResponse.builder()
                .id(userSequence.incrementAndGet())
                .username("untr1")
                .email("zkirpa5@gmail.com")
                .birthDate(LocalDate.of(1990, 3, 2))
                .weightKg(86)
                .build();

        users.put(user1.getId(), user1);
        users.put(user2.getId(), user2);

        // Создаем метрики с полным набором полей

        long metricId1 = metricSequence.incrementAndGet();
        metrics.put(metricId1, CardioMetricResponse.builder()
                .id(metricId1)
                .workoutId(workoutId1)
                .timestamp(LocalDateTime.of(LocalDate.of(2026, 3, 31), LocalTime.of(10, 45, 0)).atOffset(ZoneOffset.UTC))
                .heartRate(144)
                .distanceKm(2.1)
                .calories(180)
                .pace(6)
                .build());

        long metricId2 = metricSequence.incrementAndGet();
        metrics.put(metricId2, CardioMetricResponse.builder()
                .id(metricId2)
                .workoutId(workoutId1)
                .timestamp(LocalDateTime.of(LocalDate.of(2026, 3, 31), LocalTime.of(11, 50, 0)).atOffset(ZoneOffset.UTC))
                .heartRate(156)
                .distanceKm(5.4)
                .calories(430)
                .pace(5)
                .build());

        long metricId3 = metricSequence.incrementAndGet();
        metrics.put(metricId3, CardioMetricResponse.builder()
                .id(metricId3)
                .workoutId(workoutId2)
                .timestamp(LocalDateTime.of(LocalDate.of(2026, 2, 18), LocalTime.of(9, 40, 0)).atOffset(ZoneOffset.UTC))
                .heartRate(132)
                .distanceKm(8.7)
                .calories(250)
                .pace(4)
                .build());

        long metricId4 = metricSequence.incrementAndGet();
        metrics.put(metricId4, CardioMetricResponse.builder()
                .id(metricId4)
                .workoutId(workoutId2)
                .timestamp(LocalDateTime.of(LocalDate.of(2026, 2, 18), LocalTime.of(10, 55, 0)).atOffset(ZoneOffset.UTC))
                .heartRate(149)
                .distanceKm(16.3)
                .calories(510)
                .pace(4)
                .build());

        long metricId5 = metricSequence.incrementAndGet();
        metrics.put(metricId5, CardioMetricResponse.builder()
                .id(metricId5)
                .workoutId(workoutId3)
                .timestamp(LocalDateTime.of(LocalDate.of(2026, 4, 1), LocalTime.of(9, 30, 0)).atOffset(ZoneOffset.UTC))
                .heartRate(98)
                .distanceKm(1.6)
                .calories(90)
                .pace(8)
                .build());

        long metricId6 = metricSequence.incrementAndGet();
        metrics.put(metricId6, CardioMetricResponse.builder()
                .id(metricId6)
                .workoutId(workoutId3)
                .timestamp(LocalDateTime.of(LocalDate.of(2026, 4, 1), LocalTime.of(10, 5, 0)).atOffset(ZoneOffset.UTC))
                .heartRate(104)
                .distanceKm(3.2)
                .calories(170)
                .pace(7)
                .build());

        // Создаем итоговую статистику по тренировкам с полным набором полей

        workoutSummaries.put(workoutId1, WorkoutSummaryResponse.builder()
                .workoutId(workoutId1)
                .duration(7731L)
                .totalDistanceKm(5.4)
                .avgHeartRate(150)
                .maxHeartRate(162)
                .totalCalories(430)
                .avgPace(5D)
                .build());

        workoutSummaries.put(workoutId2, WorkoutSummaryResponse.builder()
                .workoutId(workoutId2)
                .duration(7999L)
                .totalDistanceKm(16.3)
                .avgHeartRate(141)
                .maxHeartRate(149)
                .totalCalories(510)
                .avgPace(4D)
                .build());

        workoutSummaries.put(workoutId3, WorkoutSummaryResponse.builder()
                .workoutId(workoutId3)
                .duration(4054L)
                .totalDistanceKm(3.2)
                .avgHeartRate(101)
                .maxHeartRate(104)
                .totalCalories(170)
                .avgPace(7D)
                .build());
        // Создаем тренировки с полным набором полей

        WorkoutResponse workout1 = WorkoutResponse.builder()
                .id(workoutId1)
                .user(user1)
                .type(WorkoutType.RUNNING)
                .title("Утренняя пробежка")
                .startedAt(LocalDateTime.of(LocalDate.of(2026, 3, 31), LocalTime.of(10, 15, 3)).atOffset(ZoneOffset.UTC))
                .finishedAt(LocalDateTime.of(LocalDate.of(2026, 3, 31), LocalTime.of(12, 23, 54)).atOffset(ZoneOffset.UTC))
                .metrics(metrics.values().stream()
                        .filter(m -> m.getWorkoutId() == workoutId1)
                        .toList())
                .workoutSummary(workoutSummaries.get(workoutId1))
                .build();
        workouts.put(workoutId1, workout1);

        WorkoutResponse workout2 = WorkoutResponse.builder()
                .id(workoutId2)
                .user(user1)
                .type(WorkoutType.CYCLING)
                .title("Тренировка на велотренажере")
                .startedAt(LocalDateTime.of(LocalDate.of(2026, 2, 18), LocalTime.of(9, 10, 13)).atOffset(ZoneOffset.UTC))
                .finishedAt(LocalDateTime.of(LocalDate.of(2026, 2, 18), LocalTime.of(11, 23, 32)).atOffset(ZoneOffset.UTC))
                .workoutSummary(workoutSummaries.get(workoutId2))
                .metrics(metrics.values().stream()
                        .filter(m -> m.getWorkoutId() == workoutId2)
                        .toList())
                .build();

        workouts.put(workoutId2, workout2);
        WorkoutResponse workout3 = WorkoutResponse.builder()
                .id(workoutId3)
                .user(user2)
                .type(WorkoutType.WALKING)
                .title("Разминка")
                .startedAt(LocalDateTime.of(LocalDate.of(2026, 4, 1), LocalTime.of(9, 10, 13)).atOffset(ZoneOffset.UTC))
                .finishedAt(LocalDateTime.of(LocalDate.of(2026, 4, 1), LocalTime.of(10, 17, 47)).atOffset(ZoneOffset.UTC))
                .workoutSummary(workoutSummaries.get(workoutId3))
                .metrics(metrics.values().stream()
                        .filter(m -> m.getWorkoutId() == workoutId3)
                        .toList())
                .build();
        workouts.put(workoutId3, workout3);

    }
}
