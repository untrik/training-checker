package edu.rutmiit.training_rest.graphql.types;

public record PageInfoGql(
        int pageNumber,
        int pageSize,
        int totalPages,
        boolean last
) {}
