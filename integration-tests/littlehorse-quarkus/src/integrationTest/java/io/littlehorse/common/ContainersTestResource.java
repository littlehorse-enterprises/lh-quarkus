package io.littlehorse.common;

import static io.littlehorse.tasks.ExternalInlineStructTask.REQUEST_STRUCT_DEF_NAME;
import static io.littlehorse.tasks.ExternalInlineStructTask.RESPONSE_STRUCT_DEF_NAME;

import io.littlehorse.container.LittleHorseCluster;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.InlineStructDef;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutStructDefRequest;
import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
import io.littlehorse.sdk.common.proto.StructFieldDef;
import io.littlehorse.sdk.common.proto.TypeDefinition;
import io.littlehorse.sdk.common.proto.VariableType;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector.AnnotatedAndMatchesType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ContainersTestResource implements QuarkusTestResourceLifecycleManager {

    private static final String KAFKA_VERSION = System.getProperty("kafkaVersion");
    private static final String LH_VERSION = System.getProperty("version");

    private static final Logger log = LoggerFactory.getLogger(ContainersTestResource.class);

    private LittleHorseCluster cluster;
    private LHConfig config;
    private LittleHorseBlockingStub blockingStub;

    @Override
    public Map<String, String> start() {
        log.info("Starting testcontainers");
        cluster = LittleHorseCluster.newBuilder()
                .withKafkaImage("apache/kafka:" + KAFKA_VERSION)
                .withLittlehorseImage(
                        "ghcr.io/littlehorse-enterprises/littlehorse/lh-server:" + LH_VERSION)
                .build();
        cluster.start();
        config = LHConfig.newBuilder().loadFromMap(cluster.getClientConfig()).build();
        blockingStub = config.getBlockingStub();
        registerExternalStructDefs();
        return cluster.getClientConfig();
    }

    private void registerExternalStructDefs() {
        blockingStub.putStructDef(PutStructDefRequest.newBuilder()
                .setName(REQUEST_STRUCT_DEF_NAME)
                .setStructDef(InlineStructDef.newBuilder().putFields("prompt", stringField()))
                .setAllowedUpdates(StructDefCompatibilityType.NO_SCHEMA_UPDATES)
                .build());
        blockingStub.putStructDef(PutStructDefRequest.newBuilder()
                .setName(RESPONSE_STRUCT_DEF_NAME)
                .setStructDef(InlineStructDef.newBuilder().putFields("response", stringField()))
                .setAllowedUpdates(StructDefCompatibilityType.NO_SCHEMA_UPDATES)
                .build());
    }

    private static StructFieldDef stringField() {
        return StructFieldDef.newBuilder()
                .setFieldType(TypeDefinition.newBuilder().setPrimitiveType(VariableType.STR))
                .build();
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
        testInjector.injectIntoFields(
                config, new AnnotatedAndMatchesType(InjectLittleHorseConfig.class, LHConfig.class));
    }
}
