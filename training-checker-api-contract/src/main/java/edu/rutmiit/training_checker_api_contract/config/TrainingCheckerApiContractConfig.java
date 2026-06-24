package edu.rutmiit.training_checker_api_contract.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "TrainingChecker API",
                version = "1.0.0",
                description = """
                        REST API для управления кардио тренировок.
                        """
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local development")
        })
@SecurityScheme(
        name = TrainingCheckerApiContractConfig .SECURITY_SCHEME_BEARER,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "JWT Bearer token. Пример: `Authorization: Bearer eyJhbGci...`"
)
public final class TrainingCheckerApiContractConfig  {

    /**
     * Имя схемы безопасности. Используется в аннотациях @SecurityRequirement на методах API.
     */
    public static final String SECURITY_SCHEME_BEARER = "bearerAuth";

    private TrainingCheckerApiContractConfig () {
        // utility class
    }
}


