package com.gym.crm.platform.config;

import com.gym.crm.platform.client.workload.BearerTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient workloadRestClient(RestClient.Builder builder, BearerTokenProvider bearerTokenProvider, @Value("${workload.service.base-url}") String workloadBaseUrl) {
        return builder
                .baseUrl(workloadBaseUrl)
                .requestInterceptor(bearerTokenInterceptor(bearerTokenProvider))
                .build();
    }

    private ClientHttpRequestInterceptor bearerTokenInterceptor(BearerTokenProvider bearerTokenProvider) {
        return (request, body, execution) -> {
            bearerTokenProvider.getToken()
                    .ifPresent(token -> request.getHeaders().setBearerAuth(token));

            return execution.execute(request, body);
        };
    }
}