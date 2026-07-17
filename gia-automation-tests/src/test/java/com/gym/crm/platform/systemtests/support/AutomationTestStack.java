package com.gym.crm.platform.systemtests.support;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

public final class AutomationTestStack {

    private static final String POSTGRES_IMAGE = "postgres:16-alpine";
    private static final String MONGO_IMAGE = "mongo:7.0.12";
    private static final String REDIS_IMAGE = "redis:7.2-alpine";
    private static final String ACTIVEMQ_IMAGE = "apache/activemq-classic:5.18.6";
    private static final String DISCOVERY_IMAGE = "discovery-server:local";
    private static final String CORE_IMAGE = "gym-core-service:local";
    private static final String WORKLOAD_IMAGE = "workload-service:local";
    private static final String GATEWAY_IMAGE = "api-gateway:local";
    private static final String POSTGRES_ALIAS = "postgres";
    private static final String MONGO_ALIAS = "mongodb";
    private static final String REDIS_ALIAS = "redis";
    private static final String ACTIVEMQ_ALIAS = "activemq";
    private static final String DISCOVERY_ALIAS = "discovery-server";
    private static final String CORE_ALIAS = "gym-core-service";
    private static final String WORKLOAD_ALIAS = "workload-service";
    private static final String DATABASE_NAME = "gym_crm_automation";
    private static final String DATABASE_USER = "gym";
    private static final String DATABASE_PASSWORD = "gym";
    private static final String MONGO_DATABASE = "workload_service_automation";
    private static final String ACTIVEMQ_USER = "admin";
    private static final String ACTIVEMQ_PASSWORD = "admin";
    private static final String JWT_SECRET = "automation-secret-key-automation-secret-key";
    private static final String TRAINER_WORKLOAD_QUEUE = "trainer.workload.automation.queue";
    private static final String TRAINER_WORKLOAD_DLQ = "trainer.workload.automation.queue.dlq";
    private static final int POSTGRES_PORT = 5432;
    private static final int MONGO_PORT = 27017;
    private static final int REDIS_PORT = 6379;
    private static final int ACTIVEMQ_PORT = 61616;
    private static final int ACTIVEMQ_WEB_PORT = 8161;
    private static final int DISCOVERY_PORT = 8761;
    private static final int CORE_PORT = 8081;
    private static final int WORKLOAD_PORT = 8082;
    private static final int GATEWAY_PORT = 8080;
    private static final int HTTP_OK = 200;
    private static final Duration STARTUP_TIMEOUT = Duration.ofMinutes(3);

    private static Network network;
    private static PostgreSQLContainer<?> postgres;
    private static MongoDBContainer mongo;
    private static GenericContainer<?> redis;
    private static GenericContainer<?> activeMq;
    private static GenericContainer<?> discovery;
    private static GenericContainer<?> core;
    private static GenericContainer<?> workload;
    private static GenericContainer<?> gateway;

    private AutomationTestStack() {
    }

    public static synchronized void start() {
        if (gateway != null) {
            return;
        }

        initializeContainers();
        startContainers();
        Runtime.getRuntime().addShutdownHook(new Thread(AutomationTestStack::stop));
    }

    public static String coreBaseUrl() {
        start();
        return gatewayBaseUrl() + "/gym-crm-application/api/v1";
    }

    public static String workloadBaseUrl() {
        start();
        return gatewayBaseUrl() + "/workload-service/api/v1";
    }

    public static void stop() {
        stopContainer(gateway);
        stopContainer(workload);
        stopContainer(core);
        stopContainer(discovery);
        stopContainer(activeMq);
        stopContainer(redis);
        stopContainer(mongo);
        stopContainer(postgres);

        if (network != null) {
            network.close();
        }

        gateway = null;
        workload = null;
        core = null;
        discovery = null;
        activeMq = null;
        redis = null;
        mongo = null;
        postgres = null;
        network = null;
    }

    private static void initializeContainers() {
        network = Network.newNetwork();
        postgres = createPostgresContainer();
        mongo = createMongoContainer();
        redis = createRedisContainer();
        activeMq = createActiveMqContainer();
    }

    private static void startContainers() {
        try {
            Startables.deepStart(Stream.of(postgres, mongo, redis, activeMq)).join();
            discovery = createDiscoveryContainer();
            Startables.deepStart(Stream.of(discovery)).join();

            core = createCoreContainer();
            workload = createWorkloadContainer();
            Startables.deepStart(Stream.of(core, workload)).join();

            gateway = createGatewayContainer();
            Startables.deepStart(Stream.of(gateway)).join();
        } catch (RuntimeException exception) {
            printServiceLogs();
            stop();
            throw exception;
        }
    }

    private static PostgreSQLContainer<?> createPostgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
                .withDatabaseName(DATABASE_NAME)
                .withUsername(DATABASE_USER)
                .withPassword(DATABASE_PASSWORD)
                .withNetwork(network)
                .withNetworkAliases(POSTGRES_ALIAS);
    }

    private static MongoDBContainer createMongoContainer() {
        return new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
                .withNetwork(network)
                .withNetworkAliases(MONGO_ALIAS);
    }

    private static GenericContainer<?> createRedisContainer() {
        return new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT)
                .withNetwork(network)
                .withNetworkAliases(REDIS_ALIAS)
                .waitingFor(Wait.forListeningPort());
    }

    private static GenericContainer<?> createActiveMqContainer() {
        return new GenericContainer<>(DockerImageName.parse(ACTIVEMQ_IMAGE))
                .withExposedPorts(ACTIVEMQ_PORT, ACTIVEMQ_WEB_PORT)
                .withNetwork(network)
                .withNetworkAliases(ACTIVEMQ_ALIAS)
                .waitingFor(new WaitAllStrategy()
                        .withStrategy(Wait.forListeningPort())
                        .withStrategy(Wait.forHttp("/")
                                .forPort(ACTIVEMQ_WEB_PORT)
                                .withBasicCredentials(ACTIVEMQ_USER, ACTIVEMQ_PASSWORD)
                                .forStatusCode(HTTP_OK)));
    }

    private static GenericContainer<?> createDiscoveryContainer() {
        return applicationContainer(DISCOVERY_IMAGE, DISCOVERY_PORT, DISCOVERY_ALIAS)
                .waitingFor(Wait.forListeningPort().withStartupTimeout(STARTUP_TIMEOUT));
    }

    private static GenericContainer<?> createCoreContainer() {
        return applicationContainer(CORE_IMAGE, CORE_PORT, CORE_ALIAS)
                .withEnv(commonServiceEnvironment())
                .withEnv(Map.ofEntries(Map.entry("SPRING_DATASOURCE_URL", postgresJdbcUrl()),
                                       Map.entry("SPRING_DATASOURCE_USERNAME", DATABASE_USER),
                                       Map.entry("SPRING_DATASOURCE_PASSWORD", DATABASE_PASSWORD),
                                       Map.entry("DB_URL", postgresJdbcUrl()),
                                       Map.entry("DB_USERNAME", DATABASE_USER),
                                       Map.entry("DB_PASSWORD", DATABASE_PASSWORD),
                                       Map.entry("SPRING_ACTIVEMQ_BROKER_URL", brokerUrl()),
                                       Map.entry("ACTIVEMQ_BROKER_URL", brokerUrl()),
                                       Map.entry("ACTIVEMQ_USER", ACTIVEMQ_USER),
                                       Map.entry("ACTIVEMQ_PASSWORD", ACTIVEMQ_PASSWORD),
                                       Map.entry("TRAINER_WORKLOAD_QUEUE", TRAINER_WORKLOAD_QUEUE)))
                .waitingFor(Wait.forListeningPort().withStartupTimeout(STARTUP_TIMEOUT));
    }

    private static GenericContainer<?> createWorkloadContainer() {
        return applicationContainer(WORKLOAD_IMAGE, WORKLOAD_PORT, WORKLOAD_ALIAS)
                .withEnv(commonServiceEnvironment())
                .withEnv(Map.of("SPRING_DATA_MONGODB_URI", mongoUri(),
                                "MONGODB_URI", mongoUri(),
                                "SPRING_ACTIVEMQ_BROKER_URL", brokerUrl(),
                                "ACTIVEMQ_BROKER_URL", brokerUrl(),
                                "ACTIVEMQ_USER", ACTIVEMQ_USER,
                                "ACTIVEMQ_PASSWORD", ACTIVEMQ_PASSWORD,
                                "TRAINER_WORKLOAD_QUEUE", TRAINER_WORKLOAD_QUEUE,
                                "TRAINER_WORKLOAD_DLQ", TRAINER_WORKLOAD_DLQ))
                .waitingFor(Wait.forListeningPort().withStartupTimeout(STARTUP_TIMEOUT));
    }

    private static GenericContainer<?> createGatewayContainer() {
        return applicationContainer(GATEWAY_IMAGE, GATEWAY_PORT, "api-gateway")
                .withEnv(Map.of("SPRING_PROFILES_ACTIVE", "local",
                                "API_GATEWAY_PORT", String.valueOf(GATEWAY_PORT),
                                "EUREKA_SERVER_URL", eurekaUrl()))
                .waitingFor(Wait.forListeningPort().withStartupTimeout(STARTUP_TIMEOUT));
    }

    private static GenericContainer<?> applicationContainer(String image, int port, String alias) {
        return new GenericContainer<>(DockerImageName.parse(System.getProperty(imagePropertyName(alias), image)))
                .withExposedPorts(port)
                .withNetwork(network)
                .withNetworkAliases(alias);
    }

    private static Map<String, String> commonServiceEnvironment() {
        return Map.of("SPRING_PROFILES_ACTIVE", "local",
                      "EUREKA_SERVER_URL", eurekaUrl(),
                      "JWT_SECRET", JWT_SECRET,
                      "REDIS_HOST", REDIS_ALIAS,
                      "REDIS_PORT", String.valueOf(REDIS_PORT),
                      "SPRING_DATA_REDIS_HOST", REDIS_ALIAS,
                      "SPRING_DATA_REDIS_PORT", String.valueOf(REDIS_PORT));
    }

    private static String gatewayBaseUrl() {
        return "http://" + gateway.getHost() + ":" + gateway.getMappedPort(GATEWAY_PORT);
    }

    private static String postgresJdbcUrl() {
        return "jdbc:postgresql://" + POSTGRES_ALIAS + ":" + POSTGRES_PORT + "/" + DATABASE_NAME;
    }

    private static String mongoUri() {
        return "mongodb://" + MONGO_ALIAS + ":" + MONGO_PORT + "/" + MONGO_DATABASE;
    }

    private static String brokerUrl() {
        return "tcp://" + ACTIVEMQ_ALIAS + ":" + ACTIVEMQ_PORT;
    }

    private static String eurekaUrl() {
        return "http://" + DISCOVERY_ALIAS + ":" + DISCOVERY_PORT + "/eureka/";
    }

    private static String imagePropertyName(String alias) {
        return "system.tests.image." + alias;
    }

    private static void printServiceLogs() {
        printLogs("discovery-server", discovery);
        printLogs("gym-core-service", core);
        printLogs("workload-service", workload);
        printLogs("api-gateway", gateway);
    }

    private static void printLogs(String serviceName, GenericContainer<?> container) {
        if (container != null) {
            System.err.println("===== " + serviceName + " logs =====");
            System.err.println(container.getLogs());
        }
    }

    private static void stopContainer(GenericContainer<?> container) {
        if (container != null) {
            container.stop();
        }
    }
}