package io.littlehorse.test;

import static io.littlehorse.workflows.NestedChildWorkflows.CHILD_WF;
import static io.littlehorse.workflows.NestedChildWorkflows.GRANDPARENT_WF;
import static io.littlehorse.workflows.NestedChildWorkflows.PARENT_WF;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.WfSpec;
import io.littlehorse.sdk.common.proto.WfSpecId;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class ChildWorkflowRegistrationTest {

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldRegisterNestedChildWorkflows() {
        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    WfSpec grandparent = blockingStub.getWfSpec(
                            WfSpecId.newBuilder().setName(GRANDPARENT_WF).build());
                    WfSpec parent = blockingStub.getWfSpec(
                            WfSpecId.newBuilder().setName(PARENT_WF).build());
                    WfSpec child = blockingStub.getWfSpec(
                            WfSpecId.newBuilder().setName(CHILD_WF).build());

                    assertThat(grandparent.getId().getName()).isEqualTo(GRANDPARENT_WF);
                    assertThat(parent.getId().getName()).isEqualTo(PARENT_WF);
                    assertThat(child.getId().getName()).isEqualTo(CHILD_WF);
                });
    }
}
