package edu.rutmiit.training_rest.service;

import edu.rutmiit.training_checker_api_contract.dto.WorkoutRequest;
import edu.rutmiit.training_checker_api_contract.dto.WorkoutResponse;
import edu.rutmiit.training_rest.exception.ApiException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HexFormat;
import java.util.UUID;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Service
public class IdempotentWorkoutService {
    private static final String OPERATION = "create-workout-v1";
    private final JdbcTemplate jdbc;
    private final WorkoutService workouts;
    private final JsonMapper jsonMapper;

    public IdempotentWorkoutService(JdbcTemplate jdbc, WorkoutService workouts, JsonMapper jsonMapper) {
        this.jdbc = jdbc;
        this.workouts = workouts;
        this.jsonMapper = jsonMapper;
    }

    public record Result(UUID workoutId, JsonNode responseBody, boolean replayed) {
    }

    private record Stored(String fingerprint, String state, UUID workoutId, String responseBody, boolean expired) {
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 3)
    public Result create(String principal, UUID key, WorkoutRequest request, UUID userId) {
        if (principal == null || principal.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "В токене отсутствует идентификатор пользователя");
        }
        // Ожидание конкурирующей транзакции ограничено и не превращается в зависший запрос.
        jdbc.execute("SET LOCAL lock_timeout = '1000ms'");
        jdbc.execute("SET LOCAL statement_timeout = '2000ms'");
        String requestBody = jsonMapper.writeValueAsString(List.of(userId, request.type(), request.title()));
        String fingerprint = fingerprint(requestBody);
        int claimed;
        try {
            claimed = jdbc.update("""
                    INSERT INTO workout_idempotency(principal_id, operation, operation_key, request_fingerprint, state)
                    VALUES (?, ?, ?, ?, 'IN_PROGRESS') ON CONFLICT DO NOTHING
                    """, principal, OPERATION, key, fingerprint);
        } catch (DataAccessException failure) {
            // Классы-обёртки зависят от translator; PostgreSQL SQLSTATE устойчивее для этого случая.
            if (failure.getMostSpecificCause() instanceof SQLException sql && "55P03".equals(sql.getSQLState())) {
                throw new ApiException(HttpStatus.CONFLICT, "Конкурирующая команда ещё выполняется; повторите тот же ключ позже");
            }
            throw failure;
        }
        if (claimed == 0) {
            // Это отдельная команда: READ COMMITTED видит уже зафиксированную запись победителя.
            Stored stored = jdbc.queryForObject("""
                            SELECT request_fingerprint, state, workout_id, response_body,
                                   expires_at <= clock_timestamp() AS expired
                            FROM workout_idempotency
                            WHERE principal_id = ?
                              AND operation = ?
                              AND operation_key = ?
                            """,
                    (row, index) -> new Stored(
                            row.getString("request_fingerprint"),
                            row.getString("state"),
                            row.getObject("workout_id", UUID.class),
                            row.getString("response_body"),
                            row.getBoolean("expired")),
                    principal, OPERATION, key);
            if (stored == null) {
                throw new IllegalStateException("Запись ключа не найдена после конфликта");
            }
            if (stored.expired()) {
                throw new ApiException(HttpStatus.GONE, "Срок ключа истёк; сначала согласуйте результат операции");
            }
            if (!stored.fingerprint().equals(fingerprint)) {
                throw new ApiException(HttpStatus.UNPROCESSABLE_CONTENT, "Ключ уже использован с другими данными тренировки");
            }
            if (!stored.state().equals("COMPLETED")) {
                throw new ApiException(HttpStatus.CONFLICT, "Команда с этим ключом ещё выполняется");
            }
            return new Result(stored.workoutId(), jsonMapper.readTree(stored.responseBody()), true);
        }

        WorkoutResponse workout = workouts.startWorkout(userId, request);
        String responseBody = jsonMapper.writeValueAsString(workout);

        int completed = jdbc.update("""
                UPDATE workout_idempotency
                SET state = 'COMPLETED',
                    workout_id = ?,
                    response_body = CAST(? AS JSONB)
                WHERE principal_id = ?
                  AND operation = ?
                  AND operation_key = ?
                """, workout.getId(), responseBody, principal, OPERATION, key);

        if (completed != 1) {
            throw new IllegalStateException("Не удалось сохранить результат команды");
        }

        return new Result(workout.getId(), jsonMapper.readTree(responseBody), false);
    }

    static String fingerprint(String name) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(name.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("JDK обязан предоставлять SHA-256", impossible);
        }
    }
}

