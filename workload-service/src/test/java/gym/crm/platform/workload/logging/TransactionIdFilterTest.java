package gym.crm.platform.workload.logging;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Transaction id filter tests")
class TransactionIdFilterTest {

    private static final String TRANSACTION_ID = "transaction-123";

    private final TransactionIdFilter filter = new TransactionIdFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("Should use transaction id from request header")
    void doFilterInternal_whenHeaderExists_shouldUseExistingTransactionId() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        request.addHeader(TransactionIdFilter.TRANSACTION_ID_HEADER, TRANSACTION_ID);

        filter.doFilterInternal(request, response, filterChain);

        String actual = response.getHeader(TransactionIdFilter.TRANSACTION_ID_HEADER);

        assertThat(actual).isEqualTo(TRANSACTION_ID);
        assertThat(MDC.get(TransactionIdFilter.TRANSACTION_ID)).isNull();
    }

    @Test
    @DisplayName("Should generate transaction id when request header is missing")
    void doFilterInternal_whenHeaderIsMissing_shouldGenerateTransactionId() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilterInternal(request, response, filterChain);

        String actual = response.getHeader(TransactionIdFilter.TRANSACTION_ID_HEADER);

        assertThat(actual).isNotBlank();
        assertThat(MDC.get(TransactionIdFilter.TRANSACTION_ID)).isNull();
    }
}
