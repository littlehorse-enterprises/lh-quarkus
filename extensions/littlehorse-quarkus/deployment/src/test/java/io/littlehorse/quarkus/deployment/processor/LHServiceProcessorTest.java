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
    void shouldCollectInputAndOutputStructDefNameTemplates() throws IOException {
        MethodInfo method = methodInfo("transform");

        assertThat(LHServiceProcessor.getStructDefNameTemplates(method))
                .containsExactly("${raw.input.struct.name}", "${raw.output.struct.name}");
    }

    @Test
    void shouldDeduplicateStructDefNameTemplatesDeterministically() throws IOException {
        MethodInfo method = methodInfo("reuse");

        assertThat(LHServiceProcessor.getStructDefNameTemplates(method))
                .containsExactly("${shared.struct.name}");
    }

    private static MethodInfo methodInfo(String methodName) throws IOException {
        return Index.singleClass(RawInlineStructTask.class).methods().stream()
                .filter(method -> method.name().equals(methodName))
                .findFirst()
                .orElseThrow();
    }

    static class RawInlineStructTask {

        @LHTaskMethod("transform")
        @LHType(structDefName = "${raw.output.struct.name}")
        InlineStruct transform(
                @LHType(structDefName = "${raw.input.struct.name}") InlineStruct input) {
            return input;
        }

        @LHTaskMethod("reuse")
        @LHType(structDefName = "${shared.struct.name}")
        InlineStruct reuse(@LHType(structDefName = "${shared.struct.name}") InlineStruct input) {
            return input;
        }
    }
}
