package io.littlehorse.test;

import static io.littlehorse.workflows.InlineStructDefWorkflow.CUSTOMER_VARIABLE;
import static io.littlehorse.workflows.InlineStructDefWorkflow.DELIVERY_VARIABLE;
import static io.littlehorse.workflows.InlineStructDefWorkflow.INLINE_STRUCT_DEF_WORKFLOW;
import static io.littlehorse.workflows.InlineStructDefWorkflow.MESSAGE_VARIABLE;
import static io.littlehorse.workflows.InlineStructDefWorkflow.NORMALIZED_CUSTOMER_VARIABLE;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.common.ContainersTestResource;
import io.littlehorse.common.InjectLittleHorseBlockingStub;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.adapter.LHTypeAdapterRegistry;
import io.littlehorse.sdk.common.proto.LHStatus;
import io.littlehorse.sdk.common.proto.ListVariablesRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.Variable;
import io.littlehorse.sdk.common.proto.VariableValue;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.structs.InlineStructDefAddress;
import io.littlehorse.structs.InlineStructDefCustomer;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
class InlineStructDefSerializationTest {

    private static final String CUSTOMER_NAME = "Leia";
    private static final String CUSTOMER_EMAIL = "leia@rebellion.example";
    private static final String MESSAGE = "Welcome to LittleHorse";
    private static final String PRIMARY_CITY = "Alderaan";
    private static final String PREVIOUS_CITY = "Coruscant";
    private static final String LABELED_CITY = "Naboo";

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldReturnAndReceiveAnonymousInlineStructDef() throws Exception {
        InlineStructDefCustomer customer = new InlineStructDefCustomer(
                CUSTOMER_NAME,
                CUSTOMER_EMAIL,
                new InlineStructDefAddress("Royal Avenue", " " + PRIMARY_CITY + " "),
                new InlineStructDefAddress[] {
                    new InlineStructDefAddress("Senate District", " " + PREVIOUS_CITY + " ")
                },
                Map.of(
                        "vacation",
                        new InlineStructDefAddress("Lake Country", " " + LABELED_CITY + " ")));

        WfRun wfRun = blockingStub.runWf(RunWfRequest.newBuilder()
                .setWfSpecName(INLINE_STRUCT_DEF_WORKFLOW)
                .putVariables(
                        CUSTOMER_VARIABLE,
                        LHLibUtil.objToVarValAsStruct(
                                customer,
                                InlineStructDefCustomer.class,
                                LHTypeAdapterRegistry.empty(),
                                Map.of()))
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

                    VariableValue normalizedCustomer =
                            getVariableValue(variables, NORMALIZED_CUSTOMER_VARIABLE);
                    assertThat(normalizedCustomer.getStruct().hasStructDefId()).isFalse();
                    assertThat(stringField(normalizedCustomer, "name")).isEqualTo(CUSTOMER_NAME);
                    assertThat(stringField(normalizedCustomer, "email")).isEqualTo(CUSTOMER_EMAIL);

                    VariableValue address = structField(normalizedCustomer, "address");
                    assertThat(address.getStruct().hasStructDefId()).isFalse();
                    assertThat(stringField(address, "city")).isEqualTo(PRIMARY_CITY);

                    VariableValue previousAddress = structField(
                                    normalizedCustomer, "previousAddresses")
                            .getArray()
                            .getItems(0);
                    assertThat(previousAddress.getStruct().hasStructDefId()).isFalse();
                    assertThat(stringField(previousAddress, "city")).isEqualTo(PREVIOUS_CITY);

                    VariableValue labeledAddress = structField(
                                    normalizedCustomer, "addressesByLabel")
                            .getMap()
                            .getEntries(0)
                            .getValue();
                    assertThat(labeledAddress.getStruct().hasStructDefId()).isFalse();
                    assertThat(stringField(labeledAddress, "city")).isEqualTo(LABELED_CITY);

                    VariableValue delivery = getVariableValue(variables, DELIVERY_VARIABLE);
                    assertThat(delivery.getStr())
                            .isEqualTo("%s <%s> in %s: %s"
                                    .formatted(
                                            CUSTOMER_NAME, CUSTOMER_EMAIL, PRIMARY_CITY, MESSAGE));
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
        return structField(struct, fieldName).getStr();
    }

    private static VariableValue structField(VariableValue struct, String fieldName) {
        return struct.getStruct().getStruct().getFieldsOrThrow(fieldName).getValue();
    }
}
