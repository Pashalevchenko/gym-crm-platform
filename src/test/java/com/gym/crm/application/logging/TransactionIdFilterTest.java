package com.gym.crm.application.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TransactionIdFilterTest {

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String EXISTING_TRANSACTION_ID = "test-transaction-id";

    private final TransactionIdFilter filter = new TransactionIdFilter();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    @DisplayName("Should use existing transaction id from request header")
    void doFilterInternal_whenHeaderExists_shouldUseExistingTransactionId() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader(TRANSACTION_ID_HEADER, EXISTING_TRANSACTION_ID);

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(EXISTING_TRANSACTION_ID, response.getHeader(TRANSACTION_ID_HEADER));
        assertNull(MDC.get(TRANSACTION_ID));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should generate transaction id when request header is missing")
    void doFilterInternal_whenHeaderIsMissing_shouldGenerateTransactionId() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, filterChain);

        String actualTransactionId = response.getHeader(TRANSACTION_ID_HEADER);

        assertDoesNotThrow(() -> java.util.UUID.fromString(actualTransactionId));
        assertNull(MDC.get(TRANSACTION_ID));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should generate transaction id when request header is blank")
    void doFilterInternal_whenHeaderIsBlank_shouldGenerateTransactionId() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader(TRANSACTION_ID_HEADER, "   ");

        filter.doFilterInternal(request, response, filterChain);

        String actualTransactionId = response.getHeader(TRANSACTION_ID_HEADER);

        assertDoesNotThrow(() -> java.util.UUID.fromString(actualTransactionId));
        assertNull(MDC.get(TRANSACTION_ID));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should remove transaction id from MDC after filter chain")
    void doFilterInternal_shouldRemoveTransactionIdFromMdcAfterFilterChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader(TRANSACTION_ID_HEADER, EXISTING_TRANSACTION_ID);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(MDC.get(TRANSACTION_ID));
    }
}