package edu.rutmiit.training_rest.graphql.exception;

import com.netflix.graphql.types.errors.TypedGraphQLError;
import edu.rutmiit.training_checker_api_contract.exception.InvalidMetricTimestampException;
import edu.rutmiit.training_checker_api_contract.exception.ResourceNotFoundException;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
@Slf4j
@Component
public class GraphQLExceptionHandler implements DataFetcherExceptionHandler {
    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
            DataFetcherExceptionHandlerParameters handlerParameters) {

        Throwable exception = handlerParameters.getException();

        log.error(
                "GraphQL error. Path: {}",
                handlerParameters.getPath(),
                exception
        );

        if (exception instanceof AuthorizationDeniedException) {
            var error = TypedGraphQLError.newPermissionDeniedBuilder()
                    .message("Недостаточно прав для выполнения операции")
                    .path(handlerParameters.getPath())
                    .build();

            return CompletableFuture.completedFuture(
                    DataFetcherExceptionHandlerResult.newResult()
                            .error(error)
                            .build()
            );
        }

        if (exception instanceof ResourceNotFoundException) {
            var error = TypedGraphQLError.newNotFoundBuilder()
                    .message(exception.getMessage())
                    .path(handlerParameters.getPath())
                    .build();

            return CompletableFuture.completedFuture(
                    DataFetcherExceptionHandlerResult.newResult()
                            .error(error)
                            .build());
        }
        if (exception instanceof InvalidMetricTimestampException) {
            var error = TypedGraphQLError.newConflictBuilder()
                    .message(exception.getMessage())
                    .path(handlerParameters.getPath())
                    .build();

            return CompletableFuture.completedFuture(
                    DataFetcherExceptionHandlerResult.newResult()
                            .error(error)
                            .build());
        }

        var error = TypedGraphQLError.newInternalErrorBuilder()
                .message("Внутренняя ошибка сервера")
                .path(handlerParameters.getPath())
                .build();

        return CompletableFuture.completedFuture(
                DataFetcherExceptionHandlerResult.newResult()
                        .error(error)
                        .build());
    }
}
