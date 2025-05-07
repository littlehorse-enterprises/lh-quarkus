package io.littlehorse;

import io.littlehorse.container.LittleHorseCluster;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector.AnnotatedAndMatchesType;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ContainersTestResource implements QuarkusTestResourceLifecycleManager {

    private final LittleHorseCluster cluster = LittleHorseCluster.newBuilder()
            .withKafkaImage("apache/kafka-native:4.0.0")
            .withLittlehorseImage("ghcr.io/littlehorse-enterprises/littlehorse/lh-server:"
                    + System.getProperty("lhVersion", "latest"))
            .build();

    @Override
    public Map<String, String> start() {
        cluster.start();
        //        return cluster.getClientConfig();
        return lhConfig();
    }

    // TODO: remove it in the next release
    private Map<String, String> lhConfig() {
        Properties clientProperties = cluster.getClientProperties();
        return clientProperties.keySet().stream()
                .collect(Collectors.toMap(
                        Object::toString, key -> clientProperties.get(key).toString()));
    }

    @Override
    public void stop() {
        cluster.stop();
    }

    @Override
    public void inject(TestInjector testInjector) {
        LHConfig config = LHConfig.newBuilder().loadFromMap(lhConfig()).build();
        testInjector.injectIntoFields(
                config.getBlockingStub(),
                new AnnotatedAndMatchesType(
                        InjectLittleHorseBlockingStub.class, LittleHorseBlockingStub.class));
    }
}
