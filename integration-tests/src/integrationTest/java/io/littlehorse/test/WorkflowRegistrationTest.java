package io.littlehorse.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.ExponentialBackoffRetryPolicy;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.SearchWfSpecRequest;
import io.littlehorse.sdk.common.proto.TaskNode;
import io.littlehorse.sdk.common.proto.ThreadSpec;
import io.littlehorse.sdk.common.proto.WfSpecId;
import io.littlehorse.sdk.common.proto.WfSpecIdList;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.stream.Stream;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class WorkflowRegistrationTest {

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldRegisterWorkflows() {
        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    WfSpecIdList results = blockingStub.searchWfSpec(
                            SearchWfSpecRequest.newBuilder().build());
                    WfSpecId typeAdapter = WfSpecId.newBuilder()
                            .setName("example-type-adapter")
                            .build();
                    WfSpecId greetings =
                            WfSpecId.newBuilder().setName("greetings").build();
                    WfSpecId json = WfSpecId.newBuilder().setName("json").build();
                    WfSpecId beanWorkflow =
                            WfSpecId.newBuilder().setName("workflow-in-a-bean").build();
                    WfSpecIdList expectedResult = WfSpecIdList.newBuilder()
                            .addResults(typeAdapter)
                            .addResults(greetings)
                            .addResults(json)
                            .addResults(beanWorkflow)
                            .build();
                    assertThat(results.getResultsCount()).isEqualTo(4);
                    assertThat(results).isEqualTo(expectedResult);

                    Stream.of(
                                    blockingStub.getWfSpec(greetings),
                                    blockingStub.getWfSpec(beanWorkflow))
                            .map(wfSpec -> wfSpec.getThreadSpecsOrThrow("entrypoint"))
                            .map(ThreadSpec::getNodesMap)
                            .flatMap(stringNodeMap -> stringNodeMap.values().stream())
                            .forEach(node -> {
                                if (node.hasTask()) {
                                    TaskNode task = node.getTask();
                                    assertThat(task.getExponentialBackoff())
                                            .isEqualTo(ExponentialBackoffRetryPolicy.newBuilder()
                                                    .setMultiplier(2.2f)
                                                    .setMaxDelayMs(5000)
                                                    .setBaseIntervalMs(4321)
                                                    .build());
                                }
                            });
                });
    }
}
