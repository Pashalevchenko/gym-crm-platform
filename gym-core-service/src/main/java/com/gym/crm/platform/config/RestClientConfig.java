package com.gym.crm.platform.config;

import com.gym.crm.platform.client.workload.BearerTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.slf4j.MDC;

@Configuration
public class RestClientConfig {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    @Bean
    public RestClient workloadRestClient(RestClient.Builder builder, BearerTokenProvider bearerTokenProvider, @Value("${workload.service.base-url}") String workloadBaseUrl) {
        return builder
                .baseUrl(workloadBaseUrl)
                .requestInterceptor(bearerTokenInterceptor(bearerTokenProvider))
                .requestInterceptor(transactionIdInterceptor())
                .build();
    }

    private ClientHttpRequestInterceptor bearerTokenInterceptor(BearerTokenProvider bearerTokenProvider) {
        return (request, body, execution) -> {
            bearerTokenProvider.getToken().ifPresent(token -> request.getHeaders().setBearerAuth(token));

            return execution.execute(request, body);
        };
    }

    private ClientHttpRequestInterceptor transactionIdInterceptor() {
        return (request, body, execution) -> {
            String transactionId = MDC.get(TRANSACTION_ID);

            if (transactionId != null) {
                request.getHeaders().set(TRANSACTION_ID_HEADER, transactionId);
            }

            return execution.execute(request, body);
        };
    }
}