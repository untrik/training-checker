package edu.rutmiit.training_rest.graphql.types;

import java.time.LocalDate;

public record UpdateUserInputGql(
        String   username,
        String email,
        LocalDate birthDate,
        Integer weightKg
) {
}
