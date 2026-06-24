package edu.rutmiit.training_rest.graphql.types;

import java.time.LocalDate;

public record CreateUserInputGql(
        String username,
        String email,
        LocalDate birthDate,
        Integer weightKg
) {
}
