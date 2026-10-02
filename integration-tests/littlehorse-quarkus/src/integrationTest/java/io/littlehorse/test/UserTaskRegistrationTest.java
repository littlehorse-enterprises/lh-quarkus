package io.littlehorse.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.*;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class UserTaskRegistrationTest {
    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldRegisterUserTaskWithItsResultStructDef() {
        with().pollInterval(Duration.ofMillis(100))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    UserTaskDef definition = blockingStub.getUserTaskDef(UserTaskDefId.newBuilder()
                            .setName("approve-user-task")
                            .build());
                    assertThat(definition.hasResultStructDefId()).isTrue();
                    assertThat(definition.getFieldsCount()).isZero();
                    StructDef schema = blockingStub.getStructDef(definition.getResultStructDefId());
                    assertThat(schema.getId()).isEqualTo(definition.getResultStructDefId());
                    assertThat(schema.getId().getName()).isEqualTo("approval-result");
                    assertThat(schema.getStructDef().getFieldsMap()).containsOnlyKeys("isApproved");
                    assertThat(schema.getStructDef()
                                    .getFieldsOrThrow("isApproved")
                                    .getFieldType()
                                    .getPrimitiveType())
                            .isEqualTo(VariableType.BOOL);
                });
    }

    @Test
    void shouldSaveAndCompleteUserTaskWithStructOutput() {
        String userId = UUID.randomUUID().toString();
        WfRun run = blockingStub.runWf(RunWfRequest.newBuilder()
                .setWfSpecName("user-task-approval")
                .putVariables(
                        "user-id", VariableValue.newBuilder().setStr(userId).build())
                .build());
        UserTaskRunIdList tasks = with().pollInterval(Duration.ofMillis(100))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .until(
                        () -> blockingStub.searchUserTaskRun(SearchUserTaskRunRequest.newBuilder()
                                .setUserId(userId)
                                .build()),
                        ids -> ids.getResultsCount() == 1);
        UserTaskRunId id = tasks.getResults(0);
        UserTaskRun task = blockingStub.getUserTaskRun(id);
        assertThat(task.getResultStructDefId().getName()).isEqualTo("approval-result");
        VariableValue partial = VariableValue.newBuilder()
                .setStruct(Struct.newBuilder().setStruct(InlineStruct.getDefaultInstance()))
                .build();
        UserTaskRun saved =
                blockingStub.saveUserTaskRunProgress(SaveUserTaskRunProgressRequest.newBuilder()
                        .setUserTaskRunId(id)
                        .setUserId(userId)
                        .setOutput(partial)
                        .build());
        assertThat(saved.getStatus()).isEqualTo(UserTaskRunStatus.ASSIGNED);
        assertThat(saved.getOutput().hasStruct()).isTrue();

        VariableValue output = VariableValue.newBuilder()
                .setStruct(Struct.newBuilder()
                        .setStruct(InlineStruct.newBuilder()
                                .putFields(
                                        "isApproved",
                                        StructField.newBuilder()
                                                .setValue(VariableValue.newBuilder()
                                                        .setBool(true))
                                                .build())))
                .build();
        blockingStub.completeUserTaskRun(CompleteUserTaskRunRequest.newBuilder()
                .setUserTaskRunId(id)
                .setUserId(userId)
                .setOutput(output)
                .build());
        with().pollInterval(Duration.ofMillis(100))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    assertThat(blockingStub.getWfRun(run.getId()).getStatus())
                            .isEqualTo(LHStatus.COMPLETED);
                    UserTaskRun completed = blockingStub.getUserTaskRun(id);
                    assertThat(completed.getOutput().getStruct().getStructDefId())
                            .isEqualTo(task.getResultStructDefId());
                    VariableValue approval = blockingStub
                            .getVariable(VariableId.newBuilder()
                                    .setWfRunId(run.getId())
                                    .setThreadRunNumber(0)
                                    .setName("approval")
                                    .build())
                            .getValue();
                    assertThat(approval.getStruct()
                                    .getStruct()
                                    .getFieldsOrThrow("isApproved")
                                    .getValue()
                                    .getBool())
                            .isTrue();
                    assertThat(blockingStub
                                    .getVariable(VariableId.newBuilder()
                                            .setWfRunId(run.getId())
                                            .setThreadRunNumber(0)
                                            .setName("approved")
                                            .build())
                                    .getValue()
                                    .getBool())
                            .isTrue();
                });
    }
}
