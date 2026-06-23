package gym.crm.platform.workload.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("REST logging filter tests")
class RestLoggingFilterTest {

    private static final String TRANSACTION_ID = "transaction-123";

    private final RestLoggingFilter filter = new RestLoggingFilter();

    @Test
    @DisplayName("Should preserve response body after logging")
    void doFilterInternal_shouldCopyCachedResponseBodyToOriginalResponse() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (servletRequest, servletResponse) -> servletResponse.getWriter().write("response-body");
        MDC.put(TransactionIdFilter.TRANSACTION_ID, TRANSACTION_ID);
        filter.doFilterInternal(request, response, filterChain);

        String actual = response.getContentAsString();

        assertThat(actual).isEqualTo("response-body");
    }
}