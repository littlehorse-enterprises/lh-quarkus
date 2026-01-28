package io.littlehorse.test;

import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.ExponentialBackoffRetryPolicy;
import io.littlehorse.sdk.common.proto.LHStatus;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.SearchTaskDefRequest;
import io.littlehorse.sdk.common.proto.SearchUserTaskDefRequest;
import io.littlehorse.sdk.common.proto.SearchWfSpecRequest;
import io.littlehorse.sdk.common.proto.TaskDefId;
import io.littlehorse.sdk.common.proto.TaskDefIdList;
import io.littlehorse.sdk.common.proto.TaskNode;
import io.littlehorse.sdk.common.proto.UserTaskDefId;
import io.littlehorse.sdk.common.proto.UserTaskDefIdList;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.sdk.common.proto.WfSpec;
import io.littlehorse.sdk.common.proto.WfSpecId;
import io.littlehorse.sdk.common.proto.WfSpecIdList;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class WorkflowRegistrationIT {

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
                    WfSpecId greetings =
                            WfSpecId.newBuilder().setName("greetings").build();
                    WfSpecId json = WfSpecId.newBuilder().setName("json").build();
                    WfSpecIdList expectedResult = WfSpecIdList.newBuilder()
                            .addResults(greetings)
                            .addResults(json)
                            .build();
                    assertThat(results.getResultsCount()).isEqualTo(2);
                    assertThat(results).isEqualTo(expectedResult);

                    WfSpec wfSpec = blockingStub.getWfSpec(greetings);
                    wfSpec.getThreadSpecsOrThrow("entrypoint")
                            .getNodesMap()
                            .values()
                            .forEach(node -> {
                                if (node.hasTask()) {
                                    TaskNode task = node.getTask();
                                    assertThat(task.getExponentialBackoff())
                                            .isEqualTo(ExponentialBackoffRetryPolicy.newBuilder()
                                                    .setMultiplier(2.2f)
                                                    .setMaxDelayMs(5000)
                                                    .setBaseIntervalMs(4000)
                                                    .build());
                                }
                            });
                });
    }

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

    @Test
    void shouldRegisterUserTasks() {
        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    UserTaskDefIdList results = blockingStub.searchUserTaskDef(
                            SearchUserTaskDefRequest.newBuilder().build());
                    UserTaskDefIdList expectedResult = UserTaskDefIdList.newBuilder()
                            .addResults(UserTaskDefId.newBuilder()
                                    .setName("approve-user-task")
                                    .build())
                            .build();

                    assertThat(results.getResultsCount()).isEqualTo(1);
                    assertThat(results).isEqualTo(expectedResult);
                });
    }

    @Test
    void shouldGetStatusUp() {
        given().when()
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("checks.name", hasItems("LH Tasks", "LH Server"))
                .body("checks.status", hasItems("UP", "UP"));
    }

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
}
