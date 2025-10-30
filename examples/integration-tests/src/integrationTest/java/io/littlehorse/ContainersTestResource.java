package io.littlehorse;

import io.littlehorse.container.LittleHorseCluster;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector.AnnotatedAndMatchesType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ContainersTestResource implements QuarkusTestResourceLifecycleManager {

    private static final String KAFKA_VERSION = System.getProperty("kafkaVersion", "latest");
    private static final String LH_VERSION = System.getProperty("lhVersion", "latest");

    private static final Logger log = LoggerFactory.getLogger(ContainersTestResource.class);

    private LittleHorseCluster cluster;
    private LittleHorseBlockingStub blockingStub;

    @Override
    public Map<String, String> start() {
        log.info("Starting testcontainers");
        cluster = LittleHorseCluster.newBuilder()
                .withKafkaImage("apache/kafka:" + KAFKA_VERSION)
                .withLittlehorseImage(
                        // TODO: use LH_VERSION instead of master
                        "ghcr.io/littlehorse-enterprises/littlehorse/lh-server:master")
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
        log.info("Stoping testcontainers");
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
