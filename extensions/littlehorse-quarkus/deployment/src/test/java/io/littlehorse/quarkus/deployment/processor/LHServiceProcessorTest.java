package io.littlehorse.quarkus.deployment.processor;

import static org.assertj.core.api.Assertions.assertThat;

import io.littlehorse.sdk.common.proto.InlineStruct;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

import org.jboss.jandex.Index;
import org.jboss.jandex.MethodInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class LHServiceProcessorTest {

    @Test
    void shouldCollectInputAndOutputStructDefNameExpressions() throws IOException {
        MethodInfo method = methodInfo("transform");

        assertThat(LHServiceProcessor.getStructDefNameExpressions(method))
                .containsExactly(
                        "${raw.input.struct.name}", "${raw.output.struct.name:default-response}");
    }

    @Test
    void shouldDeduplicateStructDefNameExpressionsDeterministically() throws IOException {
        MethodInfo method = methodInfo("reuse");

        assertThat(LHServiceProcessor.getStructDefNameExpressions(method))
                .containsExactly("${shared.struct.name:shared}");
    }

    private static MethodInfo methodInfo(String methodName) throws IOException {
        return Index.singleClass(RawInlineStructTask.class).methods().stream()
                .filter(method -> method.name().equals(methodName))
                .findFirst()
                .orElseThrow();
    }

    static class RawInlineStructTask {

        @LHTaskMethod("transform")
        @LHType(structDefName = "${raw.output.struct.name:default-response}")
        InlineStruct transform(
                @LHType(structDefName = "${raw.input.struct.name}") InlineStruct input) {
            return input;
        }

        @LHTaskMethod("reuse")
        @LHType(structDefName = "${shared.struct.name:shared}")
        InlineStruct reuse(
                @LHType(structDefName = "${shared.struct.name:shared}") InlineStruct input) {
            return input;
        }
    }
}
