package gym.crm.platform.workload.config;

import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.util.ErrorHandler;

import java.util.List;

@Slf4j
@Configuration
@EnableJms
public class JmsConfig {

    private static final String ERROR_MESSAGE = "Workload message processing failed. Message will be retried and moved to DLQ after retry limit";

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory(@Value("${spring.activemq.broker-url}") String brokerUrl,
                                                               @Value("${spring.activemq.user}") String user,
                                                               @Value("${spring.activemq.password}") String password,
                                                               @Value("${workload.messaging.redelivery.maximum-redeliveries}") int maximumRedeliveries,
                                                               @Value("${workload.messaging.redelivery.initial-delay-ms}") long initialDelay,
                                                               @Value("${workload.messaging.redelivery.delay-ms}") long delay) {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(maximumRedeliveries);
        redeliveryPolicy.setInitialRedeliveryDelay(initialDelay);
        redeliveryPolicy.setRedeliveryDelay(delay);

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, brokerUrl);
        connectionFactory.setTrustedPackages(List.of("java.lang", "java.util"));
        connectionFactory.setTrustAllPackages(false);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);

        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                          DefaultJmsListenerContainerFactoryConfigurer configurer,
                                                                          ErrorHandler jmsErrorHandler,
                                                                          @Value("${workload.messaging.consumer.concurrency}") String concurrency) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setSessionTransacted(true);
        factory.setErrorHandler(jmsErrorHandler);
        factory.setConcurrency(concurrency);

        return factory;
    }

    @Bean
    public ErrorHandler jmsErrorHandler() {
        return throwable -> log.error(ERROR_MESSAGE, throwable);
    }
}
