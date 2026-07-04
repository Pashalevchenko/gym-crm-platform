package gym.crm.platform.workload.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.MongoDBContainer;

public final class MongoContainerTestConfig {

    private static final String MONGO_IMAGE = "mongo:7.0.12";
    private static final MongoDBContainer MONGO = new MongoDBContainer(MONGO_IMAGE);

    static {
        MONGO.start();
    }

    private MongoContainerTestConfig() {
    }

    public static void setMongoContainerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO::getReplicaSetUrl);
    }
}