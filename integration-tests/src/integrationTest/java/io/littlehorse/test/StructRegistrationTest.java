package io.littlehorse.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.SearchStructDefRequest;
import io.littlehorse.sdk.common.proto.StructDefId;
import io.littlehorse.sdk.common.proto.StructDefIdList;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class StructRegistrationTest {

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldRegisterUserTasks() {
        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    StructDefIdList results = blockingStub.searchStructDef(
                            SearchStructDefRequest.newBuilder().build());
                    StructDefIdList expectedResult = StructDefIdList.newBuilder()
                            .addResults(
                                    StructDefId.newBuilder().setName("address").build())
                            .addResults(
                                    StructDefId.newBuilder().setName("person").build())
                            .build();

                    assertThat(results.getResultsCount()).isEqualTo(2);
                    assertThat(results).isEqualTo(expectedResult);
                });
    }
}
