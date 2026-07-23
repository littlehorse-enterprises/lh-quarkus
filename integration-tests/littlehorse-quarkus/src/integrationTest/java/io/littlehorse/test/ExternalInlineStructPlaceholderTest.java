package io.littlehorse.test;

import static io.littlehorse.tasks.ExternalInlineStructTask.REQUEST_STRUCT_DEF_NAME;
import static io.littlehorse.tasks.ExternalInlineStructTask.REQUEST_VARIABLE;
import static io.littlehorse.tasks.ExternalInlineStructTask.RESPONSE_STRUCT_DEF_NAME;
import static io.littlehorse.tasks.ExternalInlineStructTask.TASK_DEF_NAME;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.common.InjectLittleHorseConfig;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.DeleteWfRunRequest;
import io.littlehorse.sdk.common.proto.DeleteWfSpecRequest;
import io.littlehorse.sdk.common.proto.GetLatestWfSpecRequest;
import io.littlehorse.sdk.common.proto.InlineStruct;
import io.littlehorse.sdk.common.proto.LHStatus;
import io.littlehorse.sdk.common.proto.ListVariablesRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.SearchWfSpecRequest;
import io.littlehorse.sdk.common.proto.Struct;
import io.littlehorse.sdk.common.proto.StructDef;
import io.littlehorse.sdk.common.proto.StructDefId;
import io.littlehorse.sdk.common.proto.StructField;
import io.littlehorse.sdk.common.proto.Variable;
import io.littlehorse.sdk.common.proto.VariableValue;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.sdk.common.proto.WfSpecId;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class ExternalInlineStructPlaceholderTest {

    private static final String WORKFLOW_NAME = "external-inline-struct-test-workflow";
    private static final String RESPONSE_VARIABLE = "response";
    private static final String PROMPT = "Tell me about workflows";

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @InjectLittleHorseConfig
    LHConfig config;

    @Test
    void shouldRunQuarkusManagedTaskWithExternalConfiguredStructDefs() {
        StructDef requestStructDef = blockingStub.getStructDef(
                StructDefId.newBuilder().setName(REQUEST_STRUCT_DEF_NAME).build());
        WfRun wfRun = null;
        WfSpecId wfSpecId = null;

        try {
            Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, wf -> {
                WfRunVariable request = wf.declareStruct(REQUEST_VARIABLE, REQUEST_STRUCT_DEF_NAME)
                        .required();
                WfRunVariable response =
                        wf.declareStruct(RESPONSE_VARIABLE, RESPONSE_STRUCT_DEF_NAME);
                response.assign(wf.execute(TASK_DEF_NAME, request));
            });
            workflow.registerWfSpec(config);
            wfSpecId = blockingStub
                    .getLatestWfSpec(GetLatestWfSpecRequest.newBuilder()
                            .setName(WORKFLOW_NAME)
                            .build())
                    .getId();

            wfRun = blockingStub.runWf(RunWfRequest.newBuilder()
                    .setWfSpecName(WORKFLOW_NAME)
                    .putVariables(REQUEST_VARIABLE, requestValue(requestStructDef.getId()))
                    .build());
            assertWorkflowCompleted(wfRun);
        } finally {
            if (wfRun != null) {
                blockingStub.deleteWfRun(
                        DeleteWfRunRequest.newBuilder().setId(wfRun.getId()).build());
            }
            if (wfSpecId != null) {
                blockingStub.deleteWfSpec(
                        DeleteWfSpecRequest.newBuilder().setId(wfSpecId).build());
            }
        }

        assertWorkflowRemoved();
    }

    private void assertWorkflowCompleted(WfRun wfRun) {
        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    assertThat(blockingStub.getWfRun(wfRun.getId()).getStatus())
                            .isEqualTo(LHStatus.COMPLETED);

                    VariableValue response = getVariableValue(blockingStub
                            .listVariables(ListVariablesRequest.newBuilder()
                                    .setWfRunId(wfRun.getId())
                                    .build())
                            .getResultsList());
                    assertThat(response.getStruct().getStructDefId().getName())
                            .isEqualTo(RESPONSE_STRUCT_DEF_NAME);
                    assertThat(response.getStruct()
                                    .getStruct()
                                    .getFieldsOrThrow("response")
                                    .getValue()
                                    .getStr())
                            .isEqualTo(PROMPT);
                });
    }

    private void assertWorkflowRemoved() {
        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> assertThat(blockingStub
                                .searchWfSpec(SearchWfSpecRequest.newBuilder().build())
                                .getResultsList())
                        .extracting(WfSpecId::getName)
                        .doesNotContain(WORKFLOW_NAME));
    }

    private static VariableValue requestValue(StructDefId structDefId) {
        return VariableValue.newBuilder()
                .setStruct(Struct.newBuilder()
                        .setStructDefId(structDefId)
                        .setStruct(InlineStruct.newBuilder()
                                .putFields(
                                        "prompt",
                                        StructField.newBuilder()
                                                .setValue(VariableValue.newBuilder()
                                                        .setStr(PROMPT))
                                                .build())))
                .build();
    }

    private static VariableValue getVariableValue(List<Variable> variables) {
        return variables.stream()
                .filter(variable -> variable.getId().getName().equals(RESPONSE_VARIABLE))
                .map(Variable::getValue)
                .findFirst()
                .orElseThrow();
    }
}
