package io.littlehorse.test;

import static io.littlehorse.workflows.InlineStructWorkflow.CUSTOMER_VARIABLE;
import static io.littlehorse.workflows.InlineStructWorkflow.DELIVERY_VARIABLE;
import static io.littlehorse.workflows.InlineStructWorkflow.EMAIL_VARIABLE;
import static io.littlehorse.workflows.InlineStructWorkflow.ID_VARIABLE;
import static io.littlehorse.workflows.InlineStructWorkflow.INLINE_STRUCT_WORKFLOW;
import static io.littlehorse.workflows.InlineStructWorkflow.MESSAGE_VARIABLE;
import static io.littlehorse.workflows.InlineStructWorkflow.NAME_VARIABLE;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.LHStatus;
import io.littlehorse.sdk.common.proto.ListVariablesRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.Variable;
import io.littlehorse.sdk.common.proto.VariableValue;
import io.littlehorse.sdk.common.proto.WfRun;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class InlineStructSerializationTest {

    private static final String CUSTOMER_ID = "customer-123";
    private static final String CUSTOMER_NAME = "Leia";
    private static final String CUSTOMER_EMAIL = "leia@rebellion.example";
    private static final String MESSAGE = "Welcome to LittleHorse";
    private static final String STRUCT_DEF_NAME = "lh-inline-customer";

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldReturnAndReceiveInlineStructWithPlaceholderStructDefName() {
        WfRun wfRun = blockingStub.runWf(RunWfRequest.newBuilder()
                .setWfSpecName(INLINE_STRUCT_WORKFLOW)
                .putVariables(ID_VARIABLE, LHLibUtil.objToVarVal(CUSTOMER_ID))
                .putVariables(NAME_VARIABLE, LHLibUtil.objToVarVal(CUSTOMER_NAME))
                .putVariables(EMAIL_VARIABLE, LHLibUtil.objToVarVal(CUSTOMER_EMAIL))
                .putVariables(MESSAGE_VARIABLE, LHLibUtil.objToVarVal(MESSAGE))
                .build());

        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    WfRun result = blockingStub.getWfRun(wfRun.getId());
                    assertThat(result.getStatus()).isEqualTo(LHStatus.COMPLETED);

                    List<Variable> variables = blockingStub
                            .listVariables(ListVariablesRequest.newBuilder()
                                    .setWfRunId(wfRun.getId())
                                    .build())
                            .getResultsList();

                    VariableValue customer = getVariableValue(variables, CUSTOMER_VARIABLE);
                    assertThat(customer.getStruct().getStructDefId().getName())
                            .isEqualTo(STRUCT_DEF_NAME);
                    assertThat(stringField(customer, "id")).isEqualTo(CUSTOMER_ID);
                    assertThat(stringField(customer, "name")).isEqualTo(CUSTOMER_NAME);
                    assertThat(stringField(customer, "email")).isEqualTo(CUSTOMER_EMAIL);

                    VariableValue delivery = getVariableValue(variables, DELIVERY_VARIABLE);
                    assertThat(delivery.getStr())
                            .isEqualTo("%s <%s>: %s"
                                    .formatted(CUSTOMER_NAME, CUSTOMER_EMAIL, MESSAGE));
                });
    }

    private static VariableValue getVariableValue(List<Variable> variables, String variableName) {
        return variables.stream()
                .filter(variable -> variable.getId().getName().equals(variableName))
                .map(Variable::getValue)
                .findFirst()
                .orElseThrow();
    }

    private static String stringField(VariableValue struct, String fieldName) {
        return struct.getStruct()
                .getStruct()
                .getFieldsOrThrow(fieldName)
                .getValue()
                .getStr();
    }
}
