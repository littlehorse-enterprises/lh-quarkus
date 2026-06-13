package io.littlehorse.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.SearchUserTaskDefRequest;
import io.littlehorse.sdk.common.proto.UserTaskDef;
import io.littlehorse.sdk.common.proto.UserTaskDefId;
import io.littlehorse.sdk.common.proto.UserTaskDefIdList;
import io.littlehorse.sdk.common.proto.UserTaskField;
import io.littlehorse.sdk.common.proto.VariableType;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class UserTaskRegistrationTest {

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

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

                    UserTaskDef userTaskDef = blockingStub.getUserTaskDef(results.getResults(0));

                    assertThat(userTaskDef.getName()).isEqualTo("approve-user-task");
                    assertThat(userTaskDef.getFieldsList())
                            .containsExactly(UserTaskField.newBuilder()
                                    .setName("isApproved")
                                    .setType(VariableType.BOOL)
                                    .setDisplayName("Approved?")
                                    .setDescription(
                                            "Reply 'true' if this is an acceptable request.")
                                    .setRequired(true)
                                    .build());
                });
    }
}
