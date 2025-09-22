package io.littlehorse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;

import io.littlehorse.sdk.common.proto.LHStatus;
import io.littlehorse.sdk.common.proto.ListVariablesRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.Variable;
import io.littlehorse.sdk.common.proto.WfRun;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

@QuarkusIntegrationTest
@QuarkusTestResource(ContainersTestResource.class)
public class JsonSerializationExampleIT {

    @InjectLittleHorseBlockingStub
    LittleHorseBlockingStub blockingStub;

    @Test
    void shouldSerializeJsonObjects() {
        WfRun wfRun = blockingStub.runWf(
                RunWfRequest.newBuilder().setWfSpecName("json").build());

        with().pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .await()
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    WfRun result = blockingStub.getWfRun(wfRun.getId());
                    assertThat(result.getStatus()).isEqualTo(LHStatus.COMPLETED);

                    List<Variable> varsResult = blockingStub
                            .listVariables(ListVariablesRequest.newBuilder()
                                    .setWfRunId(wfRun.getId())
                                    .build())
                            .getResultsList();

                    String character = varsResult.stream()
                            .filter(variable -> variable.getId().getName().equals("character"))
                            .map(variable -> variable.getValue().getJsonObj())
                            .findFirst()
                            .orElseThrow();

                    String family = varsResult.stream()
                            .filter(variable -> variable.getId().getName().equals("family"))
                            .map(variable -> variable.getValue().getJsonArr())
                            .findFirst()
                            .orElseThrow();

                    String children = varsResult.stream()
                            .filter(variable -> variable.getId().getName().equals("children"))
                            .map(variable -> variable.getValue().getJsonArr())
                            .findFirst()
                            .orElseThrow();

                    assertThat(character)
                            .isEqualTo("{\"firstName\":\"Anakin\",\"lastName\":\"Skywalker\"}");
                    assertThat(children)
                            .isEqualTo(
                                    "[{\"firstName\":\"Luke\",\"lastName\":\"Skywalker\"},{\"firstName\":\"Leia\",\"lastName\":\"Organa\"}]");
                    assertThat(family)
                            .isEqualTo(
                                    "[{\"firstName\":\"Shmi\",\"lastName\":\"Skywalker\"},{\"firstName\":\"Padme\",\"lastName\":\"Amidala\"}]");
                });
    }
}
