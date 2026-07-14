package io.littlehorse.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.ExternalEventDef;
import io.littlehorse.sdk.common.proto.ExternalEventDefId;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.workflows.JsonWorkflow;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;

/**
 * Verifies that the {@code ExternalEventDef} referenced by {@link JsonWorkflow} via
 * {@code waitForEvent} is registered as part of workflow registration.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class ExternalEventDefRegistrationTest {

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldRegisterExternalEventDef() {
        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    ExternalEventDef def =
                            blockingStub.getExternalEventDef(ExternalEventDefId.newBuilder()
                                    .setName(JsonWorkflow.UNBLOCK_JSON_WORKFLOW)
                                    .build());
                    assertThat(def.getId().getName()).isEqualTo(JsonWorkflow.UNBLOCK_JSON_WORKFLOW);
                });
    }
}
