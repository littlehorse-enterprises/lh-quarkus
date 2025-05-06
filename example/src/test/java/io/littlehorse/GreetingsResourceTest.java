package io.littlehorse;

import static io.restassured.RestAssured.given;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;
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

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(ContainersTestResource.class)
class GreetingsResourceTest {

    @Inject
    LittleHorseBlockingStub blockingStub;

    String expectedId;

    @BeforeEach
    void setUp() {
        expectedId = UUID.randomUUID().toString();
    }

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
                            .addResults(WfSpecId.newBuilder().setName("test").build())
                            .build();

                    assertThat(results.getResultsCount()).isEqualTo(4);
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

    @ParameterizedTest
    @ValueSource(strings = {"/hello", "/hello/reactive"})
    void testHelloEndpoint(String path) {
        given().queryParam("id", expectedId)
                .queryParam("name", "Anakin")
                .when()
                .get(path)
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
