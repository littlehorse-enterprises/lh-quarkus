package io.littlehorse;

import io.littlehorse.container.LittleHorseCluster;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector.AnnotatedAndMatchesType;

import java.util.Map;

public class ContainersTestResource implements QuarkusTestResourceLifecycleManager {

    private LittleHorseCluster cluster;
    private LittleHorseBlockingStub blockingStub;

    @Override
    public Map<String, String> start() {
        cluster = LittleHorseCluster.newBuilder()
                .withKafkaImage("apache/kafka:" + System.getProperty("kafkaVersion", "latest"))
                .withLittlehorseImage("ghcr.io/littlehorse-enterprises/littlehorse/lh-server:"
                        + System.getProperty("lhVersion", "latest"))
                .build();
        cluster.start();
        blockingStub = LHConfig.newBuilder()
                .loadFromMap(cluster.getClientConfig())
                .build()
                .getBlockingStub();
        return cluster.getClientConfig();
    }

    @Override
    public void stop() {
        cluster.stop();
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(
                blockingStub,
                new AnnotatedAndMatchesType(
                        InjectLittleHorseBlockingStub.class, LittleHorseBlockingStub.class));
    }
}
