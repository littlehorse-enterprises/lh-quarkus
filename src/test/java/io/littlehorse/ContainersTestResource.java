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

    public static final String LH_VERSION = System.getProperty("lhVersion", "latest");
    private final LittleHorseCluster cluster = LittleHorseCluster.newBuilder()
            .withKafkaImage("apache/kafka:4.0.0")
            .withLittlehorseImage(
                    "ghcr.io/littlehorse-enterprises/littlehorse/lh-server:" + LH_VERSION)
            .build();
    private LittleHorseBlockingStub blockingStub;

    @Override
    public Map<String, String> start() {
        cluster.start();
        blockingStub = LHConfig.newBuilder().loadFromMap(getConfigs()).build().getBlockingStub();
        // TODO: return cluster.getClientConfig();
        return getConfigs();
    }

    // TODO: remove it in the next release
    private Map<String, String> getConfigs() {
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
        testInjector.injectIntoFields(
                blockingStub,
                new AnnotatedAndMatchesType(
                        InjectLittleHorseBlockingStub.class, LittleHorseBlockingStub.class));
    }
}
