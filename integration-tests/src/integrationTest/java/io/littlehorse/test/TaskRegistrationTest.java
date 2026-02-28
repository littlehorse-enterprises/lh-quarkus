package io.littlehorse.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.SearchTaskDefRequest;
import io.littlehorse.sdk.common.proto.TaskDefId;
import io.littlehorse.sdk.common.proto.TaskDefIdList;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class TaskRegistrationTest {

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldRegisterTasks() {
        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    TaskDefIdList results = blockingStub.searchTaskDef(
                            SearchTaskDefRequest.newBuilder().build());
                    TaskDefIdList expectedResult = TaskDefIdList.newBuilder()
                            .addResults(
                                    TaskDefId.newBuilder().setName("greetings").build())
                            .addResults(TaskDefId.newBuilder().setName("print").build())
                            .addResults(TaskDefId.newBuilder()
                                    .setName("return-json-array")
                                    .build())
                            .addResults(TaskDefId.newBuilder()
                                    .setName("return-json-list")
                                    .build())
                            .addResults(TaskDefId.newBuilder()
                                    .setName("return-json-object")
                                    .build())
                            .build();

                    assertThat(results.getResultsCount()).isEqualTo(5);
                    assertThat(results).isEqualTo(expectedResult);
                });
    }
}
