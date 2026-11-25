package edu.rutmiit.training_rest.client;

import edu.rutmiit.training_rest.exception.AnalyticsUnavailableException;
import edu.rutmiit.training_rest.exception.ApiException;
import edu.rutmiit.training_rest.service.AnalyticsRetry;
import edu.rutmiit.training_rest.service.HttpFailures;
import edu.rutmiit.training_rest.service.TimeBudget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.client.RequestAttributePrincipalResolver.principal;

@Service
@Profile("!session-auth")
public class AnalyticsClient {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsClient.class);
    private final RestClient restClient;
    private final AnalyticsRetry retry;
    private final OAuth2AuthorizedClientManager clients;

    public AnalyticsClient(RestClient restClient, AnalyticsRetry retry,
                           OAuth2AuthorizedClientManager clients) {
        this.restClient = restClient;
        this.retry = retry;
        this.clients = clients;
    }

    public Map<String, String> getAnalytics() {
        log.info("Calling analytics-service");
        try {
            var client = clients.authorize(OAuth2AuthorizeRequest.withClientRegistrationId("analytics")
                    .principal("training-rest").build());
            if (client == null) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Не удалось получить сервисную идентичность");
            }
            return retry.execute(() -> {
                var response = restClient.get().uri("/internal/analytics")
                        .headers(headers -> headers.setBearerAuth(client.getAccessToken().getTokenValue()))
                        .retrieve()
                        .body(new ParameterizedTypeReference<Map<String, String>>() {
                        });
                if (response == null) {
                    throw new ApiException(HttpStatus.BAD_GATEWAY, "Пустой ответ сервиса аналитики");
                }
                return response;
            });
        } catch (TimeBudget.Exhausted exhausted) {
            throw new ApiException(HttpStatus.GATEWAY_TIMEOUT, exhausted.getMessage());
        } catch (RestClientException | OAuth2AuthorizationException failure) {
            String result = HttpFailures.result(failure);

            log.atWarn().addKeyValue("event", "analytics.unavailable")
                    .addKeyValue("outcome", result)
                    .log("Аналитика недоступна");

            throw new ApiException(result.equals("timeout") ? HttpStatus.GATEWAY_TIMEOUT : HttpStatus.BAD_GATEWAY, "Сервис аналитики недоступен; причина: " + result);
        }
    }

}
