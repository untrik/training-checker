package edu.rutmiit.training_rest.config;

import org.slf4j.MDC;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.RestClientClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@Profile("!session-auth")
@EnableConfigurationProperties(AnalyticsSettings.class)
public class HttpClientConfig {
    @Bean
    OAuth2AuthorizedClientManager serviceAuthorizedClientManager(
            ClientRegistrationRepository registrations, OAuth2AuthorizedClientService clients) {
        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, clients);
        // Получение токена тоже является сетевым вызовом; оно не должно ждать бесконечно.
        var tokenFactory = new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(200)).build());
        tokenFactory.setReadTimeout(Duration.ofMillis(500));
        var tokenClient = new RestClientClientCredentialsTokenResponseClient();
        tokenClient.setRestClient(RestClient.builder().requestFactory(tokenFactory)
                .messageConverters(converters -> {
                    converters.clear();
                    converters.add(new FormHttpMessageConverter());
                    converters.add(new OAuth2AccessTokenResponseHttpMessageConverter());
                })
                .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler()).build());
        manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials(config -> config.accessTokenResponseClient(tokenClient)).build());
        return manager;
    }

//    @Bean
//    RestClient analyticsRestClient(RestClient.Builder builder,
//                                   OAuth2AuthorizedClientManager manager,
//                                   @Value("${app.analytics.base-url}") String baseUrl) {
//        var httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(500)).build();
//        var factory = new JdkClientHttpRequestFactory(httpClient);
//        factory.setReadTimeout(Duration.ofSeconds(2));
//        var oauth = new OAuth2ClientHttpRequestInterceptor(manager);
//        oauth.setPrincipalResolver(new RequestAttributePrincipalResolver());
//
//        return builder.baseUrl(baseUrl).requestFactory(factory)
//                .requestInterceptor((request, body, execution) -> {
//                    String requestId = MDC.get("requestId");
//                    if (requestId != null) {
//                        request.getHeaders().set(RequestIds.HEADER, requestId);
//                    }
//                    return execution.execute(request, body);
//                })
//                .requestInterceptor(oauth)
//                .build();
//    }

    @Bean
    RestClient analyticsRestClient(RestClient.Builder builder, AnalyticsSettings settings) {
        var httpClient = HttpClient.newBuilder().connectTimeout(settings.connectTimeout()).build();
        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(settings.readTimeout());

        return builder.baseUrl(settings.baseUrl()).requestFactory(factory)
                .requestInterceptor((request, body, execution) -> {
                    String requestId = MDC.get("requestId");
                    if (requestId != null) {
                        request.getHeaders().set(RequestIds.HEADER, requestId);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
