package gym.crm.platform.workload.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionIdFilter extends OncePerRequestFilter {

    public static final String TRANSACTION_ID = "transactionId";
    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);

        transactionId = Optional.ofNullable(transactionId)
                .filter(value -> !value.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);

        try (Closeable ignored = MDC.putCloseable(TRANSACTION_ID, transactionId)) {
            filterChain.doFilter(request, response);
        }

    }
}