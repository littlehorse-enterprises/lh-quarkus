package io.littlehorse;

import io.littlehorse.container.LittleHorseCluster;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

public class ContainersTestResource implements QuarkusTestResourceLifecycleManager {

    private final LittleHorseCluster cluster = LittleHorseCluster.newBuilder()
            .withKafkaImage("apache/kafka-native:4.0.0")
            .withLittlehorseImage("ghcr.io/littlehorse-enterprises/littlehorse/lh-server:"
                    + System.getProperty("lhVersion", "latest"))
            .build();

    @Override
    public Map<String, String> start() {
        cluster.start();
        return cluster.getClientConfig();
    }

    @Override
    public void stop() {
        cluster.stop();
    }
}
