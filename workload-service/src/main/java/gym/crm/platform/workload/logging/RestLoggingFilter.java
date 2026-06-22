package gym.crm.platform.workload.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_LENGTH = 1000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        Exception exception = null;

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            exception = ex;
            throw ex;
        } finally {
            int responseStatus = wrappedResponse.getStatus();

            if (exception != null && responseStatus == HttpServletResponse.SC_OK) {
                responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }

            log.info("REST call completed. transactionId={}, method={}, endpoint={}, query={}, requestBody={}, responseStatus={}, responseBody={}, errorMessage={}",
                    MDC.get(TransactionIdFilter.TRANSACTION_ID),
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString(),
                    getBody(wrappedRequest.getContentAsByteArray()),
                    responseStatus,
                    getBody(wrappedResponse.getContentAsByteArray()),
                    exception == null ? "" : exception.getMessage());

            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getBody(byte[] content) {
        if (content.length == 0) {
            return "";
        }

        String body = new String(content, StandardCharsets.UTF_8);

        return body.length() > MAX_BODY_LENGTH ? body.substring(0, MAX_BODY_LENGTH) : body;
    }
}