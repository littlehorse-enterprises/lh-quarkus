package io.littlehorse.test;

import static io.littlehorse.tasks.UUIDTask.EXAMPLE_TYPE_ADAPTER;
import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.CoreMatchers.is;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.LHStatus;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.WfRun;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class RunWorkflowTest {

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void testHelloEndpoint() {
        String expectedId = UUID.randomUUID().toString();

        given().queryParam("id", expectedId)
                .queryParam("name", "Anakin")
                .when()
                .get("/hello")
                .then()
                .statusCode(200)
                .body(is(expectedId));

        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    WfRun result = blockingStub.getWfRun(LHLibUtil.wfRunIdFromString(expectedId));

                    assertThat(result.getId().getId()).isEqualTo(expectedId);
                    assertThat(result.getStatus()).isEqualTo(LHStatus.COMPLETED);
                });
    }

    @Test
    void testRunTypeAdapterWf() {
        String expectedId = UUID.randomUUID().toString();

        blockingStub.runWf(RunWfRequest.newBuilder()
                .setId(expectedId)
                .setWfSpecName(EXAMPLE_TYPE_ADAPTER)
                .build());

        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    WfRun result = blockingStub.getWfRun(LHLibUtil.wfRunIdFromString(expectedId));

                    assertThat(result.getId().getId()).isEqualTo(expectedId);
                    assertThat(result.getStatus()).isEqualTo(LHStatus.COMPLETED);
                });
    }
}
