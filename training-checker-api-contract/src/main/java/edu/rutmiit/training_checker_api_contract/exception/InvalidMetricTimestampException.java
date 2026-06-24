package edu.rutmiit.training_checker_api_contract.exception;

import java.time.OffsetDateTime;

public class InvalidMetricTimestampException extends RuntimeException {
    public InvalidMetricTimestampException(OffsetDateTime startWorkout, OffsetDateTime timestamp) {
        super("metric with timestamp=" + timestamp + " before start workout= " + startWorkout);
    }
}
