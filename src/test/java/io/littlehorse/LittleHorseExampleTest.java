package io.littlehorse;

import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.LHStatus;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.SearchTaskDefRequest;
import io.littlehorse.sdk.common.proto.SearchUserTaskDefRequest;
import io.littlehorse.sdk.common.proto.SearchWfSpecRequest;
import io.littlehorse.sdk.common.proto.TaskDefId;
import io.littlehorse.sdk.common.proto.TaskDefIdList;
import io.littlehorse.sdk.common.proto.UserTaskDefId;
import io.littlehorse.sdk.common.proto.UserTaskDefIdList;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.sdk.common.proto.WfSpecId;
import io.littlehorse.sdk.common.proto.WfSpecIdList;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(ContainersTestResource.class)
class LittleHorseExampleTest {

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
                    WfSpecIdList expectedResult = WfSpecIdList.newBuilder()
                            .addResults(WfSpecId.newBuilder()
                                    .setName("execute-order-66")
                                    .build())
                            .addResults(
                                    WfSpecId.newBuilder().setName("greetings").build())
                            .addResults(
                                    WfSpecId.newBuilder().setName("hello-world").build())
                            .addResults(WfSpecId.newBuilder()
                                    .setName("reactive-workflow")
                                    .build())
                            .addResults(WfSpecId.newBuilder().setName("test").build())
                            .build();

                    assertThat(results.getResultsCount()).isEqualTo(5);
                    assertThat(results).isEqualTo(expectedResult);
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
                            .build();

                    assertThat(results.getResultsCount()).isEqualTo(2);
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

    @Test
    void testReactiveEndpoint() {
        String expectedId = UUID.randomUUID().toString();
        String expectedMessage = "Hello Anakin";

        given().queryParam("id", expectedId)
                .queryParam("message", expectedMessage)
                .when()
                .get("/print")
                .then()
                .statusCode(200)
                .body(is(expectedMessage));

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
